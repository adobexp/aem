/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 AdobeXP
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobexp.aem.core.components.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves the intrinsic pixel dimensions of an image referenced by repository path.
 *
 * Emitting the true intrinsic ratio as the width/height attributes on an img tag lets the browser
 * reserve the correct box before the bytes arrive, which is what keeps a logo from shifting the
 * layout when it loads.
 */
public final class ImageDimensionUtils {

    private static final String PN_TIFF_WIDTH = "tiff:ImageWidth";
    private static final String PN_TIFF_LENGTH = "tiff:ImageLength";
    private static final String RN_ORIGINAL = "/jcr:content/renditions/original";
    private static final String RN_METADATA = "/jcr:content/metadata";

    /**
     * SVG carries no TIFF metadata, so its intrinsic size has to come out of the markup itself.
     * Only the opening tag is ever inspected, so reading a small prefix of the file is enough.
     */
    private static final int SVG_HEAD_BYTES = 4096;

    private static final Pattern SVG_VIEWBOX = Pattern.compile(
        "viewBox\\s*=\\s*[\"']\\s*[-+0-9.eE]+[,\\s]+[-+0-9.eE]+[,\\s]+([-+0-9.eE]+)[,\\s]+([-+0-9.eE]+)\\s*[\"']");
    private static final Pattern SVG_WIDTH = Pattern.compile("\\bwidth\\s*=\\s*[\"']\\s*([0-9.]+)\\s*(?:px)?\\s*[\"']");
    private static final Pattern SVG_HEIGHT = Pattern.compile("\\bheight\\s*=\\s*[\"']\\s*([0-9.]+)\\s*(?:px)?\\s*[\"']");

    private ImageDimensionUtils() {
        // NOOP
    }

    /**
     * Intrinsic dimensions of an image, in pixels.
     */
    public static final class Dimensions {

        private final int width;
        private final int height;

        private Dimensions(final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    /**
     * Resolves the intrinsic dimensions of the image at the given path.
     *
     * Handles DAM assets that carry TIFF metadata, DAM assets holding SVG, and SVG served straight
     * out of a clientlib resource folder under /apps.
     *
     * @param resolver The resource resolver.
     * @param path The repository path of the image.
     * @return The dimensions, or an empty optional if they cannot be determined.
     */
    @NotNull
    public static Optional<Dimensions> getDimensions(@Nullable final ResourceResolver resolver,
                                                     @Nullable final String path) {
        if (resolver == null || StringUtils.isBlank(path)) {
            return Optional.empty();
        }

        Optional<Dimensions> fromMetadata = readTiffMetadata(resolver, path);
        if (fromMetadata.isPresent()) {
            return fromMetadata;
        }

        return readSvgMarkup(resolver, path);
    }

    @NotNull
    private static Optional<Dimensions> readTiffMetadata(@NotNull final ResourceResolver resolver,
                                                         @NotNull final String path) {
        return Optional.ofNullable(resolver.getResource(path + RN_METADATA))
            .map(Resource::getValueMap)
            .flatMap(metadata -> {
                Integer width = toPositiveInt(metadata, PN_TIFF_WIDTH);
                Integer height = toPositiveInt(metadata, PN_TIFF_LENGTH);
                return (width != null && height != null)
                    ? Optional.of(new Dimensions(width, height))
                    : Optional.empty();
            });
    }

    /**
     * AEM stores these as Long when the asset workflow extracts them, but a package installed with
     * hand written metadata can just as easily carry a String.
     */
    @Nullable
    private static Integer toPositiveInt(@NotNull final ValueMap valueMap, @NotNull final String name) {
        Long asLong = valueMap.get(name, Long.class);
        if (asLong == null) {
            String asString = valueMap.get(name, String.class);
            if (StringUtils.isNumeric(StringUtils.trimToEmpty(asString))) {
                asLong = Long.valueOf(StringUtils.trim(asString));
            }
        }
        return (asLong != null && asLong > 0 && asLong <= Integer.MAX_VALUE) ? asLong.intValue() : null;
    }

    @NotNull
    private static Optional<Dimensions> readSvgMarkup(@NotNull final ResourceResolver resolver,
                                                      @NotNull final String path) {
        Resource binary = resolver.getResource(path + RN_ORIGINAL);
        if (binary == null) {
            binary = resolver.getResource(path);
        }
        if (binary == null) {
            return Optional.empty();
        }

        String head = readHead(binary);
        if (StringUtils.isBlank(head) || !StringUtils.contains(head, "<svg")) {
            return Optional.empty();
        }

        // The viewBox is authoritative: width/height may be a rendered size in any CSS unit,
        // whereas the viewBox always describes the true aspect ratio.
        String openingTag = StringUtils.substring(head, StringUtils.indexOf(head, "<svg"));
        Matcher viewBox = SVG_VIEWBOX.matcher(openingTag);
        if (viewBox.find()) {
            Integer width = roundPositive(viewBox.group(1));
            Integer height = roundPositive(viewBox.group(2));
            if (width != null && height != null) {
                return Optional.of(new Dimensions(width, height));
            }
        }

        Matcher width = SVG_WIDTH.matcher(openingTag);
        Matcher height = SVG_HEIGHT.matcher(openingTag);
        if (width.find() && height.find()) {
            Integer w = roundPositive(width.group(1));
            Integer h = roundPositive(height.group(1));
            if (w != null && h != null) {
                return Optional.of(new Dimensions(w, h));
            }
        }

        return Optional.empty();
    }

    @Nullable
    private static String readHead(@NotNull final Resource binary) {
        try (InputStream stream = binary.adaptTo(InputStream.class)) {
            if (stream == null) {
                return null;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int read;
            while (buffer.size() < SVG_HEAD_BYTES && (read = stream.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static Integer roundPositive(@Nullable final String raw) {
        try {
            long value = Math.round(Double.parseDouble(StringUtils.trimToEmpty(raw)));
            return (value > 0 && value <= Integer.MAX_VALUE) ? (int) value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

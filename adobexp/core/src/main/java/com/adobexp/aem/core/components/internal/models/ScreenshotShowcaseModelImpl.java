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

package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.ScreenshotShowcaseModel;

/**
 * Sling Model implementation for the Screenshot Showcase component.
 * Reads multifield configurations for the browser chrome frames.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ScreenshotShowcaseModel.class,
    resourceType = ScreenshotShowcaseModelImpl.RESOURCE_TYPE
)
public class ScreenshotShowcaseModelImpl implements ScreenshotShowcaseModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/screenshot-showcase";

    private static final String FRAMES_NODE = "frames";
    private static final String DEFAULT_PARALLAX = "26";
    private static final String DEFAULT_DEPTH = "1";
    private static final String DEFAULT_IMAGE_WIDTH = "1280";
    private static final String DEFAULT_IMAGE_HEIGHT = "720";
    private static final String DEFAULT_ARIA_LABEL = "Product screenshots";
    private static final String DEFAULT_COLUMNS = "1";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_COLUMNS)
    private String columns;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_PARALLAX)
    private String parallaxStrength;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String enableTilt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_ARIA_LABEL)
    private String ariaLabel;

    private List<Frame> frames;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            frames = parseFrames(resource);
        } else {
            frames = Collections.emptyList();
        }
    }

    private Resource getResource() {
        if (currentResource != null) {
            return currentResource;
        }
        if (request != null) {
            return request.getResource();
        }
        return null;
    }

    /**
     * Parse showcase frames from the frames child node.
     */
    private List<Frame> parseFrames(Resource componentResource) {
        List<Frame> items = new ArrayList<>();
        Resource framesResource = componentResource.getChild(FRAMES_NODE);

        if (framesResource != null) {
            for (Resource frameResource : framesResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (frameResource.getName().startsWith("jcr:")) {
                    continue;
                }
                FrameImpl frame = parseFrame(frameResource);
                if (frame != null) {
                    items.add(frame);
                }
            }
        }

        return items;
    }

    /**
     * Parse a single frame resource. Frames without a dark image cannot be rendered.
     */
    private FrameImpl parseFrame(Resource frameResource) {
        ValueMap props = frameResource.getValueMap();

        String imageDark = props.get("imageDark", String.class);
        if (StringUtils.isBlank(imageDark)) {
            return null;
        }

        String imageLight = props.get("imageLight", String.class);
        String altText = props.get("altText", String.class);
        String urlLabel = props.get("urlLabel", String.class);
        String badge = props.get("badge", String.class);
        String frameTitle = props.get("frameTitle", String.class);
        String description = props.get("description", String.class);
        String depth = normaliseNumber(props.get("depth", String.class), DEFAULT_DEPTH);
        String imageWidth = normaliseNumber(props.get("imageWidth", String.class), DEFAULT_IMAGE_WIDTH);
        String imageHeight = normaliseNumber(props.get("imageHeight", String.class), DEFAULT_IMAGE_HEIGHT);

        // The CSS hides the dark image under .theme-light, so a frame with only a dark
        // image would disappear in the light theme. Reuse the dark path for both.
        if (StringUtils.isBlank(imageLight)) {
            imageLight = imageDark;
        }

        // Alt text must never be null in the rendered markup
        if (StringUtils.isBlank(altText)) {
            altText = StringUtils.isNotBlank(frameTitle) ? frameTitle : StringUtils.EMPTY;
        }

        return new FrameImpl(imageDark, imageLight, altText, urlLabel, badge, frameTitle, description,
                depth, imageWidth, imageHeight);
    }

    /**
     * Normalises a number typed into a Granite numberfield, which may be persisted as
     * "26" or "26.0". Returns the fallback when the value is blank or not a number.
     */
    private static String normaliseNumber(String rawValue, String fallback) {
        if (StringUtils.isBlank(rawValue)) {
            return fallback;
        }
        try {
            double parsed = Double.parseDouble(rawValue.trim());
            if (parsed == Math.floor(parsed) && !Double.isInfinite(parsed)) {
                return String.valueOf((long) parsed);
            }
            return String.valueOf(parsed);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // Getter implementations
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public String getAriaLabel() {
        return StringUtils.isNotBlank(ariaLabel) ? ariaLabel : DEFAULT_ARIA_LABEL;
    }

    @Override
    public String getVariant() {
        if ("screenshot-showcase--with-bg".equals(style) || "screenshot-showcase--muted".equals(style)) {
            return style;
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getGridClass() {
        String selected = StringUtils.isNotBlank(columns) ? columns.trim() : DEFAULT_COLUMNS;
        if (!"1".equals(selected) && !"2".equals(selected)) {
            selected = DEFAULT_COLUMNS;
        }
        return "screenshot-showcase__grid--" + selected;
    }

    @Override
    public String getParallaxStrength() {
        return normaliseNumber(parallaxStrength, DEFAULT_PARALLAX);
    }

    @Override
    public boolean isEnableTilt() {
        return !"false".equals(enableTilt);
    }

    @Override
    public List<Frame> getFrames() {
        return frames != null ? frames : Collections.<Frame>emptyList();
    }

    @Override
    public boolean hasFrames() {
        return frames != null && !frames.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasFrames();
    }

    /**
     * Implementation of Frame interface.
     */
    public static class FrameImpl implements Frame {
        private final String imageDark;
        private final String imageLight;
        private final String altText;
        private final String urlLabel;
        private final String badge;
        private final String frameTitle;
        private final String description;
        private final String depth;
        private final String imageWidth;
        private final String imageHeight;

        public FrameImpl(String imageDark, String imageLight, String altText, String urlLabel, String badge,
                String frameTitle, String description, String depth, String imageWidth, String imageHeight) {
            this.imageDark = imageDark;
            this.imageLight = imageLight;
            this.altText = altText;
            this.urlLabel = urlLabel;
            this.badge = badge;
            this.frameTitle = frameTitle;
            this.description = description;
            this.depth = depth;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
        }

        @Override
        public String getImageDark() {
            return imageDark;
        }

        @Override
        public String getImageLight() {
            return imageLight;
        }

        @Override
        public String getAltText() {
            return altText != null ? altText : StringUtils.EMPTY;
        }

        @Override
        public String getUrlLabel() {
            return urlLabel;
        }

        @Override
        public String getBadge() {
            return badge;
        }

        @Override
        public String getFrameTitle() {
            return frameTitle;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getDepth() {
            return depth;
        }

        @Override
        public String getImageWidth() {
            return imageWidth;
        }

        @Override
        public String getImageHeight() {
            return imageHeight;
        }

        @Override
        public boolean hasCaption() {
            return StringUtils.isNotBlank(badge) || StringUtils.isNotBlank(frameTitle)
                    || StringUtils.isNotBlank(description);
        }
    }
}

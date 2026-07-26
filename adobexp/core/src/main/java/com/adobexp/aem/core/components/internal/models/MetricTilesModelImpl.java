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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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

import com.adobexp.aem.core.components.models.MetricTilesModel;

/**
 * Sling Model implementation for the Metric Tiles component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = MetricTilesModel.class,
    resourceType = MetricTilesModelImpl.RESOURCE_TYPE
)
public class MetricTilesModelImpl implements MetricTilesModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/metric-tiles";

    private static final String TILES_NODE = "tiles";
    private static final String DEFAULT_COLUMNS = "4";
    private static final String DEFAULT_ARIA_LABEL = "Key metrics";
    private static final String FACE_ACCENT_PROPERTY = "--metric-tiles-face-accent";
    private static final int MAX_DECIMALS = 10;

    /** #rgb, #rgba, #rrggbb and #rrggbbaa hex colours. */
    private static final Pattern HEX_COLOR = Pattern.compile("^#(?:[0-9a-fA-F]{3,4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    /** rgb()/rgba()/hsl()/hsla() colours; the argument list may not contain CSS syntax characters. */
    private static final Pattern FUNCTIONAL_COLOR =
        Pattern.compile("^(?:rgb|rgba|hsl|hsla)\\(\\s*[0-9a-zA-Z.,%\\s/+-]{1,64}\\)$");

    /** CSS named colours such as {@code rebeccapurple} plus keywords like {@code currentcolor}. */
    private static final Pattern NAMED_COLOR = Pattern.compile("^[a-zA-Z]{3,24}$");

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_ARIA_LABEL)
    private String ariaLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_COLUMNS)
    private String columns;

    private List<MetricTile> tiles;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            tiles = parseTiles(resource);
        } else {
            tiles = Collections.emptyList();
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

    private List<MetricTile> parseTiles(Resource componentResource) {
        List<MetricTile> items = new ArrayList<>();
        Resource tilesResource = componentResource.getChild(TILES_NODE);

        if (tilesResource != null) {
            for (Resource itemResource : tilesResource.getChildren()) {
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                MetricTileImpl item = parseTile(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    private MetricTileImpl parseTile(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        String icon = props.get("icon", String.class);
        String label = props.get("label", String.class);
        String rawValue = props.get("value", String.class);
        String prefix = props.get("prefix", String.class);
        String suffix = props.get("suffix", String.class);
        String rawDecimals = props.get("decimals", String.class);
        String rawCompact = props.get("compact", String.class);
        String rawAccentColor = props.get("accentColor", String.class);
        String caption = props.get("caption", String.class);

        if (StringUtils.isBlank(icon) && StringUtils.isBlank(label)
                && StringUtils.isBlank(rawValue) && StringUtils.isBlank(caption)) {
            return null;
        }

        return new MetricTileImpl(
            icon,
            label,
            normalizeValue(rawValue),
            prefix,
            suffix,
            normalizeDecimals(rawDecimals),
            "true".equals(rawCompact) ? "true" : null,
            sanitizeColor(rawAccentColor),
            caption
        );
    }

    /**
     * Turns an authored value into a plain numeric string for {@code data-metric-value}.
     * Grouping separators and surrounding whitespace are tolerated; anything that is not
     * a number yields an empty string so the attribute is simply not rendered.
     */
    private static String normalizeValue(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return StringUtils.EMPTY;
        }
        String candidate = rawValue.trim().replace(",", StringUtils.EMPTY).replace(" ", StringUtils.EMPTY);
        try {
            return new BigDecimal(candidate).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Clamps the authored decimal count to a sane range, returning {@code null} when
     * nothing usable was authored so the data attribute is omitted.
     */
    private static String normalizeDecimals(String rawDecimals) {
        if (StringUtils.isBlank(rawDecimals)) {
            return null;
        }
        try {
            int decimals = Integer.parseInt(rawDecimals.trim());
            if (decimals < 0) {
                return null;
            }
            return String.valueOf(Math.min(decimals, MAX_DECIMALS));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Accepts only values that look like a CSS colour so an author cannot inject
     * arbitrary declarations into the inline style attribute.
     */
    private static String sanitizeColor(String rawColor) {
        if (StringUtils.isBlank(rawColor)) {
            return null;
        }
        String candidate = rawColor.trim();
        if (HEX_COLOR.matcher(candidate).matches()
                || FUNCTIONAL_COLOR.matcher(candidate).matches()
                || NAMED_COLOR.matcher(candidate).matches()) {
            return candidate;
        }
        return null;
    }

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
        if ("metric-tiles--with-bg".equals(style)) {
            return "metric-tiles--with-bg";
        }
        if ("metric-tiles--muted".equals(style)) {
            return "metric-tiles--muted";
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getGridClass() {
        String grid = StringUtils.isNotBlank(columns) ? columns.trim() : DEFAULT_COLUMNS;
        if (!"2".equals(grid) && !"3".equals(grid) && !"4".equals(grid)) {
            grid = DEFAULT_COLUMNS;
        }
        return "metric-tiles__grid--" + grid;
    }

    @Override
    public List<MetricTile> getTiles() {
        return tiles != null ? tiles : Collections.<MetricTile>emptyList();
    }

    @Override
    public boolean hasTiles() {
        return tiles != null && !tiles.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasTiles();
    }

    public static class MetricTileImpl implements MetricTile {
        private final String icon;
        private final String label;
        private final String value;
        private final String prefix;
        private final String suffix;
        private final String decimals;
        private final String compact;
        private final String accentColor;
        private final String caption;

        public MetricTileImpl(String icon, String label, String value, String prefix, String suffix,
                              String decimals, String compact, String accentColor, String caption) {
            this.icon = icon;
            this.label = label;
            this.value = value;
            this.prefix = prefix;
            this.suffix = suffix;
            this.decimals = decimals;
            this.compact = compact;
            this.accentColor = accentColor;
            this.caption = caption;
        }

        @Override
        public String getIcon() {
            return icon;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getSuffix() {
            return suffix;
        }

        @Override
        public String getDecimals() {
            return decimals;
        }

        @Override
        public String getCompact() {
            return compact;
        }

        @Override
        public String getAccentColor() {
            return accentColor;
        }

        @Override
        public String getFaceStyle() {
            if (StringUtils.isBlank(accentColor)) {
                return null;
            }
            return FACE_ACCENT_PROPERTY + ": " + accentColor;
        }

        @Override
        public String getCaption() {
            return caption;
        }
    }
}

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.adobexp.aem.core.components.models.AnalyticsChartModel;

/**
 * Sling Model implementation for the Analytics Chart component.
 *
 * <p>The authored panel data is serialised into the JSON payloads that the client-side
 * renderer reads from the {@code data-chart-*} attributes. All parsing is defensive:
 * unparseable input is skipped rather than surfaced as an error.</p>
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = AnalyticsChartModel.class,
    resourceType = AnalyticsChartModelImpl.RESOURCE_TYPE
)
public class AnalyticsChartModelImpl implements AnalyticsChartModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/analytics-chart";

    private static final String CHART_PANELS_NODE = "chartPanels";

    private static final String DEFAULT_COLUMNS = "2";
    private static final String DEFAULT_CHART_TYPE = "area";
    private static final String EMPTY_ARRAY = "[]";
    private static final String EMPTY_OBJECT = "{}";

    private static final Set<String> COLUMN_VALUES =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("1", "2", "3")));

    private static final Set<String> VARIANT_VALUES = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("analytics-chart--with-bg", "analytics-chart--muted")));

    private static final Set<String> CHART_TYPES = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("area", "line", "bar", "hbar", "stacked-bar", "donut", "gauge", "sparkline")));

    private static final Set<String> VALUE_FORMATS = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("number", "compact", "bytes", "percent")));

    private static final Set<String> CURVES =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("smooth", "linear")));

    private static final Set<String> COLOR_MODES =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("category", "series")));

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Analytics reports")
    private String ariaLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_COLUMNS)
    private String columns;

    private List<ChartPanel> chartPanels;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            chartPanels = parseChartPanels(resource);
        } else {
            chartPanels = Collections.emptyList();
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

    private List<ChartPanel> parseChartPanels(Resource componentResource) {
        List<ChartPanel> panels = new ArrayList<>();
        Resource panelsResource = componentResource.getChild(CHART_PANELS_NODE);

        if (panelsResource != null) {
            for (Resource panelResource : panelsResource.getChildren()) {
                if (panelResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ChartPanelImpl panel = parseChartPanel(panelResource);
                if (panel != null) {
                    panels.add(panel);
                }
            }
        }

        return panels;
    }

    private ChartPanelImpl parseChartPanel(Resource panelResource) {
        ValueMap props = panelResource.getValueMap();

        String panelTitle = readString(props, "panelTitle");
        String badge = readString(props, "badge");
        String description = readString(props, "description");
        String labelsRaw = readString(props, "labels");
        String seriesRaw = readString(props, "series");
        String canvasLabel = readString(props, "canvasLabel");

        if (StringUtils.isBlank(panelTitle) && StringUtils.isBlank(description)
            && StringUtils.isBlank(labelsRaw) && StringUtils.isBlank(seriesRaw)) {
            return null;
        }

        String chartType = readString(props, "chartType");
        if (!CHART_TYPES.contains(chartType)) {
            chartType = DEFAULT_CHART_TYPE;
        }

        return new ChartPanelImpl(
            panelTitle,
            badge,
            description,
            chartType,
            StringUtils.isNotBlank(canvasLabel) ? canvasLabel : panelTitle,
            buildLabelsJson(labelsRaw),
            buildSeriesJson(seriesRaw),
            buildConfigJson(props)
        );
    }

    /**
     * Reads a property as a string, tolerating values stored under a different type.
     */
    private static String readString(ValueMap props, String name) {
        String value = props.get(name, String.class);
        return value != null ? value.trim() : null;
    }

    /**
     * Splits the authored label list on newlines and/or commas.
     */
    @SuppressWarnings("deprecation")
    private static String buildLabelsJson(String raw) {
        if (StringUtils.isBlank(raw)) {
            return EMPTY_ARRAY;
        }
        JSONArray labels = new JSONArray();
        for (String token : raw.split("[\\r\\n,]+")) {
            String label = token.trim();
            if (!label.isEmpty()) {
                labels.put(label);
            }
        }
        return labels.toString();
    }

    /**
     * Parses one series per line in the form {@code Name|#colour|v1,v2,v3}, where both the
     * name and the colour are optional.
     * <p>
     * The colour slot may hold several colours separated by {@code /}, as in
     * {@code Sessions|#f4c15e/#5b9dff/#4ecdc4|58,34,8}. Donut, stacked bar and ranked bar
     * charts then use one colour per category instead of the theme palette.
     */
    @SuppressWarnings("deprecation")
    private static String buildSeriesJson(String raw) {
        if (StringUtils.isBlank(raw)) {
            return EMPTY_ARRAY;
        }

        JSONArray series = new JSONArray();
        for (String line : raw.split("[\\r\\n]+")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            String name = null;
            String color = null;
            String valuesRaw;

            if (parts.length >= 3) {
                name = StringUtils.trimToNull(parts[0]);
                color = StringUtils.trimToNull(parts[1]);
                valuesRaw = parts[2];
            } else if (parts.length == 2) {
                // A single separator is ambiguous: a leading '#' means it is the colour.
                String prefix = StringUtils.trimToNull(parts[0]);
                if (prefix != null && prefix.startsWith("#")) {
                    color = prefix;
                } else {
                    name = prefix;
                }
                valuesRaw = parts[1];
            } else {
                valuesRaw = parts[0];
            }

            JSONArray values = new JSONArray();
            for (String token : valuesRaw.split(",")) {
                Number value = toNumber(token);
                if (value != null) {
                    values.put(value);
                }
            }
            if (values.length() == 0) {
                continue;
            }

            JSONObject entry = new JSONObject();
            try {
                if (name != null) {
                    entry.put("name", name);
                }
                if (color != null) {
                    entry.put("color", color);
                    JSONArray colors = splitColors(color);
                    if (colors.length() > 1) {
                        entry.put("colors", colors);
                    }
                }
                entry.put("values", values);
            } catch (Exception e) {
                continue;
            }
            series.put(entry);
        }

        return series.toString();
    }

    /**
     * Splits the colour slot into its individual colours.
     */
    @SuppressWarnings("deprecation")
    private static JSONArray splitColors(String raw) {
        JSONArray colors = new JSONArray();
        for (String token : raw.split("/")) {
            String color = StringUtils.trimToNull(token);
            if (color != null) {
                colors.put(color);
            }
        }
        return colors;
    }

    /**
     * Collects the renderer options the author actually set into a single JSON object.
     */
    @SuppressWarnings("deprecation")
    private static String buildConfigJson(ValueMap props) {
        JSONObject config = new JSONObject();
        try {
            putString(config, "unit", readString(props, "unit"), null);
            putNumber(config, "yMax", readString(props, "yMax"));
            putNumber(config, "total", readString(props, "total"));
            putString(config, "totalLabel", readString(props, "totalLabel"), null);
            putString(config, "centerLabel", readString(props, "centerLabel"), null);
            putString(config, "centerValue", readString(props, "centerValue"), null);
            putString(config, "valueFormat", readString(props, "valueFormat"), VALUE_FORMATS);
            putNumber(config, "decimals", readString(props, "decimals"));
            putBoolean(config, "showGrid", readString(props, "showGrid"));
            putBoolean(config, "showLegend", readString(props, "showLegend"));
            putBoolean(config, "showAxis", readString(props, "showAxis"));
            putString(config, "curve", readString(props, "curve"), CURVES);
            putString(config, "colorBy", readString(props, "colorBy"), COLOR_MODES);
        } catch (Exception e) {
            return EMPTY_OBJECT;
        }
        return config.toString();
    }

    @SuppressWarnings("deprecation")
    private static void putString(JSONObject config, String key, String value, Set<String> allowed) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        if (allowed != null && !allowed.contains(value)) {
            return;
        }
        config.put(key, value);
    }

    @SuppressWarnings("deprecation")
    private static void putNumber(JSONObject config, String key, String value) {
        Number number = toNumber(value);
        if (number != null) {
            config.put(key, number);
        }
    }

    @SuppressWarnings("deprecation")
    private static void putBoolean(JSONObject config, String key, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        config.put(key, Boolean.parseBoolean(value));
    }

    /**
     * Converts authored text to a JSON-safe number, preferring an integral representation so
     * whole numbers do not gain a spurious decimal part. Returns {@code null} when the input
     * cannot be read as a finite number.
     */
    private static Number toNumber(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        try {
            double parsed = Double.parseDouble(raw.trim());
            if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
                return null;
            }
            if (parsed == Math.rint(parsed) && Math.abs(parsed) < 1e15) {
                return Long.valueOf((long) parsed);
            }
            return Double.valueOf(parsed);
        } catch (NumberFormatException e) {
            return null;
        }
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
        return ariaLabel;
    }

    @Override
    public String getVariant() {
        return VARIANT_VALUES.contains(style) ? style : StringUtils.EMPTY;
    }

    @Override
    public String getGridClass() {
        String value = COLUMN_VALUES.contains(columns) ? columns : DEFAULT_COLUMNS;
        return "analytics-chart__grid--" + value;
    }

    @Override
    public List<ChartPanel> getChartPanels() {
        return chartPanels != null ? chartPanels : Collections.<ChartPanel>emptyList();
    }

    @Override
    public boolean hasPanels() {
        return chartPanels != null && !chartPanels.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasPanels();
    }

    public static class ChartPanelImpl implements ChartPanel {
        private final String panelTitle;
        private final String badge;
        private final String description;
        private final String chartType;
        private final String canvasLabel;
        private final String labelsJson;
        private final String seriesJson;
        private final String configJson;

        public ChartPanelImpl(String panelTitle, String badge, String description, String chartType,
                              String canvasLabel, String labelsJson, String seriesJson, String configJson) {
            this.panelTitle = panelTitle;
            this.badge = badge;
            this.description = description;
            this.chartType = chartType;
            this.canvasLabel = canvasLabel;
            this.labelsJson = labelsJson;
            this.seriesJson = seriesJson;
            this.configJson = configJson;
        }

        @Override
        public String getPanelTitle() {
            return panelTitle;
        }

        @Override
        public String getBadge() {
            return badge;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getChartType() {
            return chartType;
        }

        @Override
        public String getCanvasLabel() {
            return canvasLabel;
        }

        @Override
        public String getLabelsJson() {
            return labelsJson;
        }

        @Override
        public String getSeriesJson() {
            return seriesJson;
        }

        @Override
        public String getConfigJson() {
            return configJson;
        }
    }
}

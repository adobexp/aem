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

package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Analytics Chart component.
 */
public interface AnalyticsChartModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the accessible label of the section wrapper.
     */
    String getAriaLabel();

    /**
     * Returns the section style variant CSS class, e.g. {@code analytics-chart--with-bg}
     * or {@code analytics-chart--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    /**
     * Returns the grid modifier class, e.g. {@code analytics-chart__grid--2}.
     */
    String getGridClass();

    List<ChartPanel> getChartPanels();

    boolean hasPanels();

    boolean hasContent();

    interface ChartPanel {

        String getPanelTitle();

        String getBadge();

        String getDescription();

        /**
         * Returns the chart renderer key: one of {@code area}, {@code line}, {@code bar},
         * {@code hbar}, {@code stacked-bar}, {@code donut}, {@code gauge}, {@code sparkline}.
         */
        String getChartType();

        /**
         * Returns the accessible label of the chart canvas, falling back to the panel title.
         */
        String getCanvasLabel();

        /**
         * Returns the category labels as a JSON array string, e.g. {@code ["Mon","Tue"]}.
         */
        String getLabelsJson();

        /**
         * Returns the series as a JSON array string, e.g.
         * {@code [{"name":"Unique users","color":"#10b981","values":[12,18]}]}.
         * The {@code name} and {@code color} keys are omitted when not authored.
         */
        String getSeriesJson();

        /**
         * Returns the renderer options as a JSON object string holding only the keys the
         * author actually set, e.g. {@code {"valueFormat":"compact","showGrid":true}}.
         */
        String getConfigJson();
    }
}

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
package com.adobexp.aem.core.components.config.components;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-Aware Configuration for Analytics Chart component theme variables.
 */
@Configuration(
        label = "AdobeXP - Analytics Chart Theme Configuration",
        description = "Context-Aware Configuration for Analytics Chart component CSS variables"
)
public @interface AnalyticsChartThemeConfig {

    @Property(label = "Analytics Chart Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkAnalyticsChartBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Analytics Chart Background (Light)", description = "Section background (With Background style) in light theme")
    String lightAnalyticsChartBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Analytics Chart Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkAnalyticsChartMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Analytics Chart Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightAnalyticsChartMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Analytics Chart Panel Background (Dark)", description = "Chart panel card background in dark theme")
    String darkAnalyticsChartPanelBg() default "#2a2a2a";

    @Property(label = "Analytics Chart Panel Background (Light)", description = "Chart panel card background in light theme")
    String lightAnalyticsChartPanelBg() default "#ffffff";

    @Property(label = "Analytics Chart Panel Border (Dark)", description = "Chart panel card border in dark theme")
    String darkAnalyticsChartPanelBorder() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Analytics Chart Panel Border (Light)", description = "Chart panel card border in light theme")
    String lightAnalyticsChartPanelBorder() default "rgba(0, 0, 0, 0.08)";

    @Property(label = "Analytics Chart Panel Shadow (Dark)", description = "Chart panel resting shadow in dark theme")
    String darkAnalyticsChartPanelShadow() default "0 6px 24px rgba(0, 0, 0, 0.28)";

    @Property(label = "Analytics Chart Panel Shadow (Light)", description = "Chart panel resting shadow in light theme")
    String lightAnalyticsChartPanelShadow() default "0 4px 18px rgba(0, 0, 0, 0.07)";

    @Property(label = "Analytics Chart Panel Shadow Hover (Dark)", description = "Chart panel hover shadow in dark theme")
    String darkAnalyticsChartPanelShadowHover() default "0 16px 44px rgba(0, 0, 0, 0.38)";

    @Property(label = "Analytics Chart Panel Shadow Hover (Light)", description = "Chart panel hover shadow in light theme")
    String lightAnalyticsChartPanelShadowHover() default "0 14px 38px rgba(0, 0, 0, 0.12)";

    @Property(label = "Analytics Chart Grid Color (Dark)", description = "Chart gridline colour in dark theme")
    String darkAnalyticsChartGridColor() default "rgba(255, 255, 255, 0.09)";

    @Property(label = "Analytics Chart Grid Color (Light)", description = "Chart gridline colour in light theme")
    String lightAnalyticsChartGridColor() default "rgba(0, 0, 0, 0.09)";

    @Property(label = "Analytics Chart Tick Color (Dark)", description = "Axis tick label colour in dark theme")
    String darkAnalyticsChartTickColor() default "rgba(255, 255, 255, 0.45)";

    @Property(label = "Analytics Chart Tick Color (Light)", description = "Axis tick label colour in light theme")
    String lightAnalyticsChartTickColor() default "rgba(0, 0, 0, 0.5)";

    @Property(label = "Analytics Chart Track Color (Dark)", description = "Bar/gauge track colour in dark theme")
    String darkAnalyticsChartTrackColor() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Analytics Chart Track Color (Light)", description = "Bar/gauge track colour in light theme")
    String lightAnalyticsChartTrackColor() default "rgba(0, 0, 0, 0.08)";

    @Property(label = "Analytics Chart Badge Background (Dark)", description = "Panel type badge background in dark theme")
    String darkAnalyticsChartBadgeBg() default "rgba(74, 222, 128, 0.14)";

    @Property(label = "Analytics Chart Badge Background (Light)", description = "Panel type badge background in light theme")
    String lightAnalyticsChartBadgeBg() default "rgba(5, 150, 105, 0.1)";

    @Property(label = "Analytics Chart Badge Border (Dark)", description = "Panel type badge border in dark theme")
    String darkAnalyticsChartBadgeBorder() default "rgba(74, 222, 128, 0.3)";

    @Property(label = "Analytics Chart Badge Border (Light)", description = "Panel type badge border in light theme")
    String lightAnalyticsChartBadgeBorder() default "rgba(5, 150, 105, 0.25)";

    @Property(label = "Analytics Chart Badge Text (Dark)", description = "Panel type badge text colour in dark theme")
    String darkAnalyticsChartBadgeText() default "#4ade80";

    @Property(label = "Analytics Chart Badge Text (Light)", description = "Panel type badge text colour in light theme")
    String lightAnalyticsChartBadgeText() default "#047857";

    @Property(
            label = "Analytics Chart Category Colours (Dark)",
            description = "Comma-separated palette giving each category of a donut, stacked bar or ranked bar chart "
                    + "its own colour in dark theme. Colours are cycled when a chart has more categories than colours."
    )
    String darkAnalyticsChartCategoryColors()
            default "#f4c15e, #5b9dff, #4ecdc4, #f2789f, #a78bfa, #ff9f5a, #7ddf7d, #d9d24f";

    @Property(
            label = "Analytics Chart Category Colours (Light)",
            description = "Comma-separated palette giving each category of a donut, stacked bar or ranked bar chart "
                    + "its own colour in light theme. Colours are cycled when a chart has more categories than colours."
    )
    String lightAnalyticsChartCategoryColors()
            default "#b8860b, #2f6bd8, #12897d, #c2456f, #6d4bd8, #cf5f18, #2f8f3f, #86811c";
}

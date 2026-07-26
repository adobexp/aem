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
 * Context-Aware Configuration for Metric Tiles component theme variables.
 */
@Configuration(
        label = "AdobeXP - Metric Tiles Theme Configuration",
        description = "Context-Aware Configuration for Metric Tiles component CSS variables"
)
public @interface MetricTilesThemeConfig {

    @Property(label = "Metric Tiles Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkMetricTilesBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Metric Tiles Background (Light)", description = "Section background (With Background style) in light theme")
    String lightMetricTilesBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Metric Tiles Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkMetricTilesMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Metric Tiles Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightMetricTilesMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Metric Tiles Card Background (Dark)", description = "KPI tile card background in dark theme")
    String darkMetricTilesCardBg() default "#2a2a2a";

    @Property(label = "Metric Tiles Card Background (Light)", description = "KPI tile card background in light theme")
    String lightMetricTilesCardBg() default "#ffffff";

    @Property(label = "Metric Tiles Card Border (Dark)", description = "KPI tile card border in dark theme")
    String darkMetricTilesCardBorder() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Metric Tiles Card Border (Light)", description = "KPI tile card border in light theme")
    String lightMetricTilesCardBorder() default "rgba(0, 0, 0, 0.08)";

    @Property(label = "Metric Tiles Face Background (Dark)", description = "Gradient face behind the tile icon in dark theme")
    String darkMetricTilesFaceBg() default "linear-gradient(115deg, #f8fafc 0%, #dbeafe 45%, var(--metric-tiles-face-accent, #3b82f6) 100%)";

    @Property(label = "Metric Tiles Face Background (Light)", description = "Gradient face behind the tile icon in light theme")
    String lightMetricTilesFaceBg() default "linear-gradient(115deg, #ffffff 0%, #eff6ff 45%, var(--metric-tiles-face-accent, #2563eb) 100%)";

    @Property(label = "Metric Tiles Face Accent (Dark)", description = "Default tile accent colour in dark theme (per-tile override available in the dialog)")
    String darkMetricTilesFaceAccent() default "#3b82f6";

    @Property(label = "Metric Tiles Face Accent (Light)", description = "Default tile accent colour in light theme (per-tile override available in the dialog)")
    String lightMetricTilesFaceAccent() default "#2563eb";

    @Property(label = "Metric Tiles Face Text (Dark)", description = "Icon/glyph colour on the gradient face in dark theme")
    String darkMetricTilesFaceText() default "#0f172a";

    @Property(label = "Metric Tiles Face Text (Light)", description = "Icon/glyph colour on the gradient face in light theme")
    String lightMetricTilesFaceText() default "#0f172a";
}

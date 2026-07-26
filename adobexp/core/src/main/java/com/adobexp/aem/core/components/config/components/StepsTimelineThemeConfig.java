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
 * Context-Aware Configuration for Steps Timeline component theme variables.
 */
@Configuration(
        label = "AdobeXP - Steps Timeline Theme Configuration",
        description = "Context-Aware Configuration for Steps Timeline component CSS variables"
)
public @interface StepsTimelineThemeConfig {

    @Property(label = "Steps Timeline Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkStepsTimelineBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Steps Timeline Background (Light)", description = "Section background (With Background style) in light theme")
    String lightStepsTimelineBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Steps Timeline Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkStepsTimelineMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Steps Timeline Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightStepsTimelineMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Steps Timeline Rail Background (Dark)", description = "Unfilled vertical rail colour in dark theme")
    String darkStepsTimelineRailBg() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Steps Timeline Rail Background (Light)", description = "Unfilled vertical rail colour in light theme")
    String lightStepsTimelineRailBg() default "rgba(0, 0, 0, 0.1)";

    @Property(label = "Steps Timeline Rail Fill (Dark)", description = "Scroll progress rail fill in dark theme")
    String darkStepsTimelineRailFill() default "var(--main-theme-color, #e3a002)";

    @Property(label = "Steps Timeline Rail Fill (Light)", description = "Scroll progress rail fill in light theme")
    String lightStepsTimelineRailFill() default "var(--main-theme-color, #42f4fd)";

    @Property(label = "Steps Timeline Marker Background (Dark)", description = "Step number marker background in dark theme")
    String darkStepsTimelineMarkerBg() default "#2a2a2a";

    @Property(label = "Steps Timeline Marker Background (Light)", description = "Step number marker background in light theme")
    String lightStepsTimelineMarkerBg() default "#ffffff";

    @Property(label = "Steps Timeline Marker Border (Dark)", description = "Step number marker border in dark theme")
    String darkStepsTimelineMarkerBorder() default "rgba(255, 255, 255, 0.14)";

    @Property(label = "Steps Timeline Marker Border (Light)", description = "Step number marker border in light theme")
    String lightStepsTimelineMarkerBorder() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Steps Timeline Marker Active Border (Dark)", description = "Revealed step marker border in dark theme")
    String darkStepsTimelineMarkerActiveBorder() default "var(--main-theme-color, #e3a002)";

    @Property(label = "Steps Timeline Marker Active Border (Light)", description = "Revealed step marker border in light theme")
    String lightStepsTimelineMarkerActiveBorder() default "var(--main-theme-color, #42f4fd)";

    @Property(label = "Steps Timeline Marker Glow (Dark)", description = "Revealed step marker glow in dark theme")
    String darkStepsTimelineMarkerGlow() default "rgba(227, 160, 2, 0.18)";

    @Property(label = "Steps Timeline Marker Glow (Light)", description = "Revealed step marker glow in light theme")
    String lightStepsTimelineMarkerGlow() default "rgba(66, 244, 253, 0.2)";

    @Property(label = "Steps Timeline Meta Background (Dark)", description = "Step meta chip background in dark theme")
    String darkStepsTimelineMetaBg() default "rgba(74, 222, 128, 0.13)";

    @Property(label = "Steps Timeline Meta Background (Light)", description = "Step meta chip background in light theme")
    String lightStepsTimelineMetaBg() default "rgba(5, 150, 105, 0.1)";

    @Property(label = "Steps Timeline Meta Text (Dark)", description = "Step meta chip text colour in dark theme")
    String darkStepsTimelineMetaText() default "#4ade80";

    @Property(label = "Steps Timeline Meta Text (Light)", description = "Step meta chip text colour in light theme")
    String lightStepsTimelineMetaText() default "#047857";

    @Property(label = "Steps Timeline Inline Code Background (Dark)", description = "Inline code chip background inside step copy in dark theme")
    String darkStepsTimelineCodeBg() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Steps Timeline Inline Code Background (Light)", description = "Inline code chip background inside step copy in light theme")
    String lightStepsTimelineCodeBg() default "rgba(0, 0, 0, 0.06)";
}

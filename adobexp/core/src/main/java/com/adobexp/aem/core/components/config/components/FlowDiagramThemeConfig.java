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
 * Context-Aware Configuration for Flow Diagram component theme variables.
 */
@Configuration(
        label = "AdobeXP - Flow Diagram Theme Configuration",
        description = "Context-Aware Configuration for Flow Diagram component CSS variables"
)
public @interface FlowDiagramThemeConfig {

    @Property(label = "Flow Diagram Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkFlowDiagramBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Flow Diagram Background (Light)", description = "Section background (With Background style) in light theme")
    String lightFlowDiagramBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Flow Diagram Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkFlowDiagramMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Flow Diagram Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightFlowDiagramMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Flow Diagram Node Background (Dark)", description = "Pipeline node card background in dark theme")
    String darkFlowDiagramNodeBg() default "#2a2a2a";

    @Property(label = "Flow Diagram Node Background (Light)", description = "Pipeline node card background in light theme")
    String lightFlowDiagramNodeBg() default "#ffffff";

    @Property(label = "Flow Diagram Node Border (Dark)", description = "Pipeline node card border in dark theme")
    String darkFlowDiagramNodeBorder() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Flow Diagram Node Border (Light)", description = "Pipeline node card border in light theme")
    String lightFlowDiagramNodeBorder() default "rgba(0, 0, 0, 0.09)";

    @Property(label = "Flow Diagram Node Hover Border (Dark)", description = "Pipeline node hover border in dark theme")
    String darkFlowDiagramNodeHoverBorder() default "var(--main-theme-color, #e3a002)";

    @Property(label = "Flow Diagram Node Hover Border (Light)", description = "Pipeline node hover border in light theme")
    String lightFlowDiagramNodeHoverBorder() default "var(--main-theme-color, #42f4fd)";

    @Property(label = "Flow Diagram Node Shadow (Dark)", description = "Pipeline node resting shadow in dark theme")
    String darkFlowDiagramNodeShadow() default "0 6px 22px rgba(0, 0, 0, 0.3)";

    @Property(label = "Flow Diagram Node Shadow (Light)", description = "Pipeline node resting shadow in light theme")
    String lightFlowDiagramNodeShadow() default "0 4px 18px rgba(0, 0, 0, 0.07)";

    @Property(label = "Flow Diagram Node Shadow Hover (Dark)", description = "Pipeline node hover shadow in dark theme")
    String darkFlowDiagramNodeShadowHover() default "0 14px 36px rgba(0, 0, 0, 0.4)";

    @Property(label = "Flow Diagram Node Shadow Hover (Light)", description = "Pipeline node hover shadow in light theme")
    String lightFlowDiagramNodeShadowHover() default "0 12px 32px rgba(0, 0, 0, 0.12)";

    @Property(label = "Flow Diagram Link Color (Dark)", description = "Connector line colour in dark theme")
    String darkFlowDiagramLinkColor() default "rgba(255, 255, 255, 0.16)";

    @Property(label = "Flow Diagram Link Color (Light)", description = "Connector line colour in light theme")
    String lightFlowDiagramLinkColor() default "rgba(0, 0, 0, 0.16)";

    @Property(label = "Flow Diagram Pulse Color (Dark)", description = "Animated flowing dash colour in dark theme")
    String darkFlowDiagramPulseColor() default "var(--main-theme-color, #e3a002)";

    @Property(label = "Flow Diagram Pulse Color (Light)", description = "Animated flowing dash colour in light theme")
    String lightFlowDiagramPulseColor() default "var(--main-theme-color, #42f4fd)";

    @Property(label = "Flow Diagram Tag Background (Dark)", description = "Stage label chip background in dark theme")
    String darkFlowDiagramTagBg() default "rgba(74, 222, 128, 0.13)";

    @Property(label = "Flow Diagram Tag Background (Light)", description = "Stage label chip background in light theme")
    String lightFlowDiagramTagBg() default "rgba(5, 150, 105, 0.1)";

    @Property(label = "Flow Diagram Tag Text (Dark)", description = "Stage label chip text colour in dark theme")
    String darkFlowDiagramTagText() default "#4ade80";

    @Property(label = "Flow Diagram Tag Text (Light)", description = "Stage label chip text colour in light theme")
    String lightFlowDiagramTagText() default "#047857";
}

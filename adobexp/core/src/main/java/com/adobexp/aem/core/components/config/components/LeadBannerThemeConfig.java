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
 * Context-Aware Configuration for Lead Banner component theme variables.
 */
@Configuration(
        label = "AdobeXP - Lead Banner Theme Configuration",
        description = "Context-Aware Configuration for Lead Banner component CSS variables"
)
public @interface LeadBannerThemeConfig {

    @Property(label = "Lead Banner Height", description = "Lead banner height on desktop")
    String leadBannerHeight() default "600px";

    @Property(label = "Lead Banner Height (Mobile)", description = "Lead banner height on mobile")
    String leadBannerHeightMobile() default "460px";

    @Property(label = "Lead Banner Gradient Start (Dark)", description = "Lead banner gradient start color in dark theme")
    String darkLeadBannerGradientStart() default "#212020";

    @Property(label = "Lead Banner Gradient Start (Light)", description = "Lead banner gradient start color in light theme")
    String lightLeadBannerGradientStart() default "#ffffff";

    @Property(label = "Lead Banner Gradient Stop 25% (Dark)", description = "Lead banner gradient 25% stop color in dark theme")
    String darkLeadBannerGradientStop25() default "#aa7802";

    @Property(label = "Lead Banner Gradient Stop 25% (Light)", description = "Lead banner gradient 25% stop color in light theme")
    String lightLeadBannerGradientStop25() default "#aafbff";

    @Property(label = "Lead Banner Gradient Stop 50% (Dark)", description = "Lead banner gradient 50% stop color in dark theme")
    String darkLeadBannerGradientStop50() default "#e3a002";

    @Property(label = "Lead Banner Gradient Stop 50% (Light)", description = "Lead banner gradient 50% stop color in light theme")
    String lightLeadBannerGradientStop50() default "#42c2fd";

    @Property(label = "Lead Banner Gradient Stop 75% (Dark)", description = "Lead banner gradient 75% stop color in dark theme")
    String darkLeadBannerGradientStop75() default "#aa7802";

    @Property(label = "Lead Banner Gradient Stop 75% (Light)", description = "Lead banner gradient 75% stop color in light theme")
    String lightLeadBannerGradientStop75() default "#aafbff";

    @Property(label = "Lead Banner Gradient End (Dark)", description = "Lead banner gradient end color in dark theme")
    String darkLeadBannerGradientEnd() default "#212020";

    @Property(label = "Lead Banner Gradient End (Light)", description = "Lead banner gradient end color in light theme")
    String lightLeadBannerGradientEnd() default "#ffffff";

    @Property(label = "Lead Banner Text Primary (Dark)", description = "Lead banner primary text color in dark theme")
    String darkLeadBannerTextPrimary() default "#ffffff";

    @Property(label = "Lead Banner Text Primary (Light)", description = "Lead banner primary text color in light theme")
    String lightLeadBannerTextPrimary() default "#323232";

    @Property(label = "Lead Banner Text Secondary (Dark)", description = "Lead banner secondary text color in dark theme")
    String darkLeadBannerTextSecondary() default "#242424";

    @Property(label = "Lead Banner Text Secondary (Light)", description = "Lead banner secondary text color in light theme")
    String lightLeadBannerTextSecondary() default "#6e6e6e";

    @Property(label = "Lead Banner Secondary Text Color (Dark)", description = "Lead banner secondary text color (alternate) in dark theme")
    String darkLeadBannerSecondaryTextColor() default "#fffffa";

    @Property(label = "Lead Banner Secondary Text Color (Light)", description = "Lead banner secondary text color (alternate) in light theme")
    String lightLeadBannerSecondaryTextColor() default "#323232";

    @Property(label = "Lead Banner Char Fade Duration", description = "Character fade animation duration")
    String leadBannerCharFadeDuration() default "0.3s";
}

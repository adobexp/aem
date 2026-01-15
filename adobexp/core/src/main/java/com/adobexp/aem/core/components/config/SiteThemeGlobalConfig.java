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
package com.adobexp.aem.core.components.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-Aware Configuration for global theme variables.
 */
@Configuration(
        label = "AdobeXP - AEM Quickstart Global Theme Configuration",
        description = "Global Context-Aware Configuration for site-wide theme CSS variables"
)
public @interface SiteThemeGlobalConfig {

    // ==================== MAIN THEME COLOR ====================

    @Property(label = "Main Theme Color (Dark)", description = "Main theme color in dark theme")
    String darkMainThemeColor() default "#e3a002";

    @Property(label = "Main Theme Color (Light)", description = "Main theme color in light theme")
    String lightMainThemeColor() default "#42f4fd";

    // ==================== TEXT COLOR VARIABLES ====================

    @Property(label = "Primary Text Color (Dark)", description = "Primary text color in dark theme")
    String darkPrimaryTextColor() default "#ffc846";

    @Property(label = "Primary Text Color (Light)", description = "Primary text color in light theme")
    String lightPrimaryTextColor() default "#000000";

    @Property(label = "Secondary Text Color (Dark)", description = "Secondary text color in dark theme")
    String darkSecondaryTextColor() default "#ffedc2";

    @Property(label = "Secondary Text Color (Light)", description = "Secondary text color in light theme")
    String lightSecondaryTextColor() default "#4b5563";

    @Property(label = "Standard Primary Site Text Color (Dark)", description = "Standard primary site text color in dark theme")
    String darkStandardPrimarySiteTextColor() default "#ffffff";

    @Property(label = "Standard Primary Site Text Color (Light)", description = "Standard primary site text color in light theme")
    String lightStandardPrimarySiteTextColor() default "#111827";

    @Property(label = "Standard Secondary Site Text Color (Dark)", description = "Standard secondary site text color in dark theme")
    String darkStandardSecondarySiteTextColor() default "#a2a2a2";

    @Property(label = "Standard Secondary Site Text Color (Light)", description = "Standard secondary site text color in light theme")
    String lightStandardSecondarySiteTextColor() default "#4b5563";

    @Property(label = "Standard Site Font Size", description = "Standard font size for the site")
    String standardSiteFontSize() default "16px";

    @Property(label = "Standard Site Font Weight", description = "Standard font weight for the site")
    String standardSiteFontWeight() default "400";

    // ==================== BLOCKQUOTE VARIABLES ====================

    @Property(label = "Blockquote Border Color (Dark)", description = "Blockquote border color in dark theme")
    String darkBlockquoteBorderColor() default "var(--primary-text-color)";

    @Property(label = "Blockquote Border Color (Light)", description = "Blockquote border color in light theme")
    String lightBlockquoteBorderColor() default "var(--primary-text-color)";

    @Property(label = "Blockquote Background (Dark)", description = "Blockquote background color in dark theme")
    String darkBlockquoteBg() default "#2f2f2f";

    @Property(label = "Blockquote Background (Light)", description = "Blockquote background color in light theme")
    String lightBlockquoteBg() default "#d0fafc";

    @Property(label = "Blockquote Quote Color (Dark)", description = "Blockquote decorative quote color in dark theme")
    String darkBlockquoteQuoteColor() default "var(--primary-text-color)";

    @Property(label = "Blockquote Quote Color (Light)", description = "Blockquote decorative quote color in light theme")
    String lightBlockquoteQuoteColor() default "var(--primary-text-color)";

    // ==================== GLOBAL PAGE VARIABLES ====================

    @Property(label = "Site Body Background (Dark)", description = "Body background color in dark theme")
    String darkSiteBodyBg() default "#1e1e1e";

    @Property(label = "Site Body Background (Light)", description = "Body background color in light theme")
    String lightSiteBodyBg() default "#ffffff";

    @Property(label = "Site Overlay Background (Dark)", description = "Site overlay background in dark theme")
    String darkSiteOverlayBg() default "rgba(0, 0, 0, 0.466)";

    @Property(label = "Site Overlay Background (Light)", description = "Site overlay background in light theme")
    String lightSiteOverlayBg() default "rgba(255, 255, 255, 0.727)";
}

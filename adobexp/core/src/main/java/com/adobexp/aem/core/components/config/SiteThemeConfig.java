/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2024 Adobe
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
 * Context-Aware Configuration for site theme variables.
 * This configuration holds CSS custom property values for both dark and light themes.
 * 
 * The configuration can be set at the site level in /conf/[site]/_sling_configs/
 * and will be inherited by all pages within that site.
 */
@Configuration(label = "Site Theme Configuration", description = "Context-Aware Configuration for site theme CSS variables (dark and light themes)")
public @interface SiteThemeConfig {

    // ==================== HEADER THEME VARIABLES ====================

    @Property(label = "Header Background Color (Dark)", description = "Background color for header in dark theme")
    String darkHeaderBackgroundColor() default "#212020";

    @Property(label = "Header Background Color (Light)", description = "Background color for header in light theme")
    String lightHeaderBackgroundColor() default "#fdfeff";

    @Property(label = "Header Height", description = "Height of the header")
    String headerHeight() default "60px";

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

    // ==================== GLOBAL PAGE VARIABLES ====================

    @Property(label = "Site Body Background (Dark)", description = "Body background color in dark theme")
    String darkSiteBodyBg() default "#1e1e1e";

    @Property(label = "Site Body Background (Light)", description = "Body background color in light theme")
    String lightSiteBodyBg() default "#ffffff";

    // ==================== FOOTER VARIABLES ====================

    @Property(label = "Footer Background (Dark)", description = "Footer background color in dark theme")
    String darkFooterBg() default "#363535";

    @Property(label = "Footer Background (Light)", description = "Footer background color in light theme")
    String lightFooterBg() default "#f5f5f5";

    @Property(label = "Footer Curtain Height Offset", description = "Footer curtain height offset")
    String footerCurtainHeightOffset() default "-25px";

    // ==================== SERVICES VARIABLES ====================

    @Property(label = "Services Divider Color (Dark)", description = "Services divider color in dark theme")
    String darkServicesDividerColor() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Services Divider Color (Light)", description = "Services divider color in light theme")
    String lightServicesDividerColor() default "rgba(0, 0, 0, 0.12)";

    // ==================== BUTTON THEME VARIABLES ====================

    @Property(label = "Button Background (Dark)", description = "Button background in dark theme")
    String darkButtonBg() default "transparent";

    @Property(label = "Button Text Color (Dark)", description = "Button text color in dark theme")
    String darkButtonText() default "#ffffff";

    @Property(label = "Button Border (Dark)", description = "Button border color in dark theme")
    String darkButtonBorder() default "#ffffff";

    @Property(label = "Button Hover Background (Dark)", description = "Button hover background in dark theme")
    String darkButtonHoverBg() default "#ffffff";

    @Property(label = "Button Hover Text (Dark)", description = "Button hover text color in dark theme")
    String darkButtonHoverText() default "#000000";

    @Property(label = "Button Background (Light)", description = "Button background in light theme")
    String lightButtonBg() default "transparent";

    @Property(label = "Button Text Color (Light)", description = "Button text color in light theme")
    String lightButtonText() default "#000000";

    @Property(label = "Button Border (Light)", description = "Button border color in light theme")
    String lightButtonBorder() default "#000000";

    @Property(label = "Button Hover Background (Light)", description = "Button hover background in light theme")
    String lightButtonHoverBg() default "#000000";

    @Property(label = "Button Hover Text (Light)", description = "Button hover text color in light theme")
    String lightButtonHoverText() default "#ffffff";

    // ==================== ABOUT US VARIABLES ====================

    @Property(label = "About Us Background (Dark)", description = "About Us section background in dark theme")
    String darkAboutUsBg() default "#1e1e1e";

    @Property(label = "About Us Background (Light)", description = "About Us section background in light theme")
    String lightAboutUsBg() default "transparent";

    // ==================== LEAD BANNER VARIABLES ====================

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
    String lightLeadBannerGradientStop25() default "#b4e1f6";

    @Property(label = "Lead Banner Gradient Stop 50% (Dark)", description = "Lead banner gradient 50% stop color in dark theme")
    String darkLeadBannerGradientStop50() default "#e3a002";

    @Property(label = "Lead Banner Gradient Stop 50% (Light)", description = "Lead banner gradient 50% stop color in light theme")
    String lightLeadBannerGradientStop50() default "#42c2fd";

    @Property(label = "Lead Banner Gradient Stop 75% (Dark)", description = "Lead banner gradient 75% stop color in dark theme")
    String darkLeadBannerGradientStop75() default "#aa7802";

    @Property(label = "Lead Banner Gradient Stop 75% (Light)", description = "Lead banner gradient 75% stop color in light theme")
    String lightLeadBannerGradientStop75() default "#b4e1f6";

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

    // ==================== ARTICLE TILE VARIABLES ====================

    @Property(label = "Article Tile Overlay Background (Dark)", description = "Article tile overlay background in dark theme")
    String darkArticleTileOverlayBg() default "rgba(0, 0, 0, 0.8)";

    @Property(label = "Article Tile Overlay Background (Light)", description = "Article tile overlay background in light theme")
    String lightArticleTileOverlayBg() default "#77d0fac7";

    // ==================== HEADER OVERLAY VARIABLES ====================

    @Property(label = "Header Overlay Column Divider Color (Dark)", description = "Header overlay column divider color in dark theme")
    String darkHeaderOverlayColumnDividerColor() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Header Overlay Column Divider Color (Light)", description = "Header overlay column divider color in light theme")
    String lightHeaderOverlayColumnDividerColor() default "rgba(0, 0, 0, 0.18)";

    @Property(label = "Header Overlay Hover Background (Dark)", description = "Header overlay hover background in dark theme")
    String darkHeaderOverlayHoverBg() default "rgba(255, 255, 255, 0.10)";

    @Property(label = "Header Overlay Hover Background (Light)", description = "Header overlay hover background in light theme")
    String lightHeaderOverlayHoverBg() default "rgba(0, 0, 0, 0.08)";

    // ==================== SITE BANNER VARIABLES ====================

    @Property(label = "Site Banner Background (Dark)", description = "Site banner background color in dark theme")
    String darkSiteBannerBg() default "#363535";

    @Property(label = "Site Banner Background (Light)", description = "Site banner background color in light theme")
    String lightSiteBannerBg() default "#9adcfa";

    @Property(label = "Site Banner Marquee Duration (Dark)", description = "Site banner marquee animation duration in dark theme")
    String darkSiteBannerMarqueeDuration() default "5s";

    @Property(label = "Site Banner Marquee Duration (Light)", description = "Site banner marquee animation duration in light theme")
    String lightSiteBannerMarqueeDuration() default "10s";

    @Property(label = "Site Banner Cycle Duration (Dark)", description = "Site banner cycle duration in dark theme")
    String darkSiteBannerCycleDuration() default "10s";

    @Property(label = "Site Banner Cycle Duration (Light)", description = "Site banner cycle duration in light theme")
    String lightSiteBannerCycleDuration() default "20s";

    @Property(label = "Site Banner Font Size", description = "Site banner font size")
    String siteBannerFontSize() default "20px";

    // ==================== QUOTE VARIABLES ====================

    @Property(label = "Quote Background (Dark)", description = "Quote section background in dark theme")
    String darkQuoteBg() default "#363535";

    @Property(label = "Quote Background (Light)", description = "Quote section background in light theme (uses var(--site-body-bg) by default)")
    String lightQuoteBg() default "";

    @Property(label = "Quote Card Glow", description = "Quote card glow gradient effect")
    String quoteCardGlow() default "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)";

    // ==================== LOOPING CIRCLE GALLERY VARIABLES ====================

    @Property(label = "Looping Circle Gallery Overlay Background (Dark)", description = "Gallery overlay background in dark theme")
    String darkLoopingCircleGalleryOverlayBg() default "rgba(255, 255, 255, 0.5)";

    @Property(label = "Looping Circle Gallery Overlay Background (Light)", description = "Gallery overlay background in light theme")
    String lightLoopingCircleGalleryOverlayBg() default "rgba(0, 0, 0, 0.5)";

    @Property(label = "Looping Circle Gallery Overlay Text (Dark)", description = "Gallery overlay text color in dark theme")
    String darkLoopingCircleGalleryOverlayText() default "#000000";

    @Property(label = "Looping Circle Gallery Overlay Text (Light)", description = "Gallery overlay text color in light theme")
    String lightLoopingCircleGalleryOverlayText() default "#ffffff";
}


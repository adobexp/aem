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
 * Context-Aware Configuration for site theme variables.
 * This configuration holds CSS custom property values for both dark and light themes.
 * 
 * The configuration can be set at the site level in /conf/[site]/_sling_configs/
 * and will be inherited by all pages within that site.
 */
@Configuration(label = "Site Theme Configuration", description = "Context-Aware Configuration for site theme CSS variables (dark and light themes)")
public @interface SiteThemeConfig {

    // ==================== MAIN THEME COLOR ====================

    @Property(label = "Main Theme Color (Dark)", description = "Main theme color in dark theme")
    String darkMainThemeColor() default "#e3a002";

    @Property(label = "Main Theme Color (Light)", description = "Main theme color in light theme")
    String lightMainThemeColor() default "#42f4fd";

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

    @Property(label = "Services Background (Dark)", description = "Services background gradient in dark theme")
    String darkServicesBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Services Background (Light)", description = "Services background gradient in light theme")
    String lightServicesBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";

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

    // ==================== CTA PILL BUTTON VARIABLES ====================

    @Property(label = "CTA Pill Background (Dark)", description = "CTA pill button background in dark theme")
    String darkCtaPillBg() default "#ffffff";

    @Property(label = "CTA Pill Text (Dark)", description = "CTA pill button text color in dark theme")
    String darkCtaPillText() default "#000000";

    @Property(label = "CTA Pill Hover Background (Dark)", description = "CTA pill button hover background in dark theme")
    String darkCtaPillHoverBg() default "#e0e0e0";

    @Property(label = "CTA Pill Icon Background (Dark)", description = "CTA pill button icon background in dark theme")
    String darkCtaPillIconBg() default "#000000";

    @Property(label = "CTA Pill Icon Color (Dark)", description = "CTA pill button icon color in dark theme")
    String darkCtaPillIconColor() default "#ffffff";

    @Property(label = "CTA Pill Background (Light)", description = "CTA pill button background in light theme")
    String lightCtaPillBg() default "#000000";

    @Property(label = "CTA Pill Text (Light)", description = "CTA pill button text color in light theme")
    String lightCtaPillText() default "#ffffff";

    @Property(label = "CTA Pill Hover Background (Light)", description = "CTA pill button hover background in light theme")
    String lightCtaPillHoverBg() default "#333333";

    @Property(label = "CTA Pill Icon Background (Light)", description = "CTA pill button icon background in light theme")
    String lightCtaPillIconBg() default "#ffffff";

    @Property(label = "CTA Pill Icon Color (Light)", description = "CTA pill button icon color in light theme")
    String lightCtaPillIconColor() default "#000000";

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

    // ==================== ARTICLE TILE VARIABLES ====================

    @Property(label = "Article Tile Overlay Background (Dark)", description = "Article tile overlay background in dark theme")
    String darkArticleTileOverlayBg() default "rgba(0, 0, 0, 0.8)";

    @Property(label = "Article Tile Overlay Background (Light)", description = "Article tile overlay background in light theme")
    String lightArticleTileOverlayBg() default "#42f4fd75";

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

    @Property(label = "Quote Card Glow (Dark)", description = "Quote card glow gradient effect in dark theme")
    String darkQuoteCardGlow() default "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)";

    @Property(label = "Quote Card Glow (Light)", description = "Quote card glow gradient effect in light theme")
    String lightQuoteCardGlow() default "radial-gradient(closest-side at 82% 28%, var(--main-theme-color)ad, transparent 60%), radial-gradient(closest-side at 92% 10%, var(--main-theme-color)5e, transparent 58%)";

    // ==================== LOOPING CIRCLE GALLERY VARIABLES ====================

    @Property(label = "Looping Circle Gallery Overlay Background (Dark)", description = "Gallery overlay background in dark theme")
    String darkLoopingCircleGalleryOverlayBg() default "rgba(255, 255, 255, 0.5)";

    @Property(label = "Looping Circle Gallery Overlay Background (Light)", description = "Gallery overlay background in light theme")
    String lightLoopingCircleGalleryOverlayBg() default "linear-gradient(180deg, #ffffff53 0%, var(--main-theme-color) 50%, #ffffff53 100%)";

    @Property(label = "Looping Circle Gallery Overlay Text (Dark)", description = "Gallery overlay text color in dark theme")
    String darkLoopingCircleGalleryOverlayText() default "#000000";

    @Property(label = "Looping Circle Gallery Overlay Text (Light)", description = "Gallery overlay text color in light theme")
    String lightLoopingCircleGalleryOverlayText() default "#ffffff";

    // ==================== COUNT UP VARIABLES ====================

    @Property(label = "CountUp Background (Dark)", description = "CountUp section background in dark theme")
    String darkCountUpBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "CountUp Background (Light)", description = "CountUp section background in light theme")
    String lightCountUpBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "CountUp Card Background (Dark)", description = "CountUp card background in dark theme")
    String darkCountUpCardBg() default "#2a2a2a";

    @Property(label = "CountUp Card Background (Light)", description = "CountUp card background in light theme")
    String lightCountUpCardBg() default "#ffffff";

    // ==================== SUBSCRIPTION PLANS VARIABLES ====================

    @Property(label = "Subscription Plans Background (Dark)", description = "Subscription plans section background in dark theme")
    String darkSubscriptionPlansBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Subscription Plans Background (Light)", description = "Subscription plans section background in light theme")
    String lightSubscriptionPlansBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Subscription Plans Card Background (Dark)", description = "Subscription plans card background in dark theme")
    String darkSubscriptionPlansCardBg() default "#2a2a2a";

    @Property(label = "Subscription Plans Card Background (Light)", description = "Subscription plans card background in light theme")
    String lightSubscriptionPlansCardBg() default "#ffffff";

    @Property(label = "Subscription Plans Card Border (Dark)", description = "Subscription plans card border in dark theme")
    String darkSubscriptionPlansCardBorder() default "#404040";

    @Property(label = "Subscription Plans Card Border (Light)", description = "Subscription plans card border in light theme")
    String lightSubscriptionPlansCardBorder() default "#e5e7eb";

    @Property(label = "Subscription Plans Toggle Background (Dark)", description = "Subscription plans toggle background in dark theme")
    String darkSubscriptionPlansToggleBg() default "#374151";

    @Property(label = "Subscription Plans Toggle Background (Light)", description = "Subscription plans toggle background in light theme")
    String lightSubscriptionPlansToggleBg() default "#e5e7eb";

    @Property(label = "Subscription Plans Toggle Active Background (Light)", description = "Subscription plans toggle active background in light theme")
    String lightSubscriptionPlansToggleActiveBg() default "#3b82f6";

    @Property(label = "Subscription Plans Toggle Active Text (Dark)", description = "Subscription plans toggle active text in dark theme")
    String darkSubscriptionPlansToggleActiveText() default "#ffffff";

    @Property(label = "Subscription Plans Toggle Active Text (Light)", description = "Subscription plans toggle active text in light theme")
    String lightSubscriptionPlansToggleActiveText() default "#ffffff";

    @Property(label = "Subscription Plans Save Badge Background (Dark)", description = "Subscription plans save badge background in dark theme")
    String darkSubscriptionPlansSaveBadgeBg() default "rgba(34, 197, 94, 0.2)";

    @Property(label = "Subscription Plans Save Badge Background (Light)", description = "Subscription plans save badge background in light theme")
    String lightSubscriptionPlansSaveBadgeBg() default "#dcfce7";

    @Property(label = "Subscription Plans Save Badge Text (Dark)", description = "Subscription plans save badge text in dark theme")
    String darkSubscriptionPlansSaveBadgeText() default "#4ade80";

    @Property(label = "Subscription Plans Save Badge Text (Light)", description = "Subscription plans save badge text in light theme")
    String lightSubscriptionPlansSaveBadgeText() default "#16a34a";

    @Property(label = "Subscription Plans Price Color (Light)", description = "Subscription plans price color in light theme")
    String lightSubscriptionPlansPriceColor() default "#3b82f6";

    @Property(label = "Subscription Plans Highlight Border (Light)", description = "Subscription plans highlight border in light theme")
    String lightSubscriptionPlansHighlightBorder() default "#3b82f6";

    @Property(label = "Subscription Plans Divider (Dark)", description = "Subscription plans divider color in dark theme")
    String darkSubscriptionPlansDivider() default "#404040";

    @Property(label = "Subscription Plans Divider (Light)", description = "Subscription plans divider color in light theme")
    String lightSubscriptionPlansDivider() default "#e5e7eb";

    @Property(label = "Subscription Plans Credits Icon (Light)", description = "Subscription plans credits icon color in light theme")
    String lightSubscriptionPlansCreditsIcon() default "#3b82f6";

    @Property(label = "Subscription Plans Feature Check (Dark)", description = "Subscription plans feature check color in dark theme")
    String darkSubscriptionPlansFeatureCheck() default "#4ade80";

    @Property(label = "Subscription Plans Feature Check (Light)", description = "Subscription plans feature check color in light theme")
    String lightSubscriptionPlansFeatureCheck() default "#22c55e";

    @Property(label = "Subscription Plans CTA Primary Background (Dark)", description = "Subscription plans CTA primary background in dark theme")
    String darkSubscriptionPlansCtaPrimaryBg() default "#02c36f";

    @Property(label = "Subscription Plans CTA Primary Background (Light)", description = "Subscription plans CTA primary background in light theme")
    String lightSubscriptionPlansCtaPrimaryBg() default "#3b82f6";

    @Property(label = "Subscription Plans CTA Primary Hover (Dark)", description = "Subscription plans CTA primary hover in dark theme")
    String darkSubscriptionPlansCtaPrimaryHover() default "#02a25d";

    @Property(label = "Subscription Plans CTA Primary Hover (Light)", description = "Subscription plans CTA primary hover in light theme")
    String lightSubscriptionPlansCtaPrimaryHover() default "#2563eb";

    // ==================== COMPARE SUBSCRIPTION VARIABLES ====================

    @Property(label = "Compare Subscription Background (Dark)", description = "Compare subscription section background in dark theme")
    String darkCompareSubscriptionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Compare Subscription Background (Light)", description = "Compare subscription section background in light theme")
    String lightCompareSubscriptionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Compare Subscription Table Background (Dark)", description = "Compare subscription table background in dark theme")
    String darkCompareSubscriptionTableBg() default "#2a2a2a";

    @Property(label = "Compare Subscription Table Background (Light)", description = "Compare subscription table background in light theme")
    String lightCompareSubscriptionTableBg() default "#ffffff";

    @Property(label = "Compare Subscription Border (Dark)", description = "Compare subscription border in dark theme")
    String darkCompareSubscriptionBorder() default "#404040";

    @Property(label = "Compare Subscription Border (Light)", description = "Compare subscription border in light theme")
    String lightCompareSubscriptionBorder() default "#e5e7eb";

    @Property(label = "Compare Subscription Features Background (Dark)", description = "Compare subscription features background in dark theme")
    String darkCompareSubscriptionFeaturesBg() default "#1f1f1f";

    @Property(label = "Compare Subscription Features Background (Light)", description = "Compare subscription features background in light theme")
    String lightCompareSubscriptionFeaturesBg() default "#f9fafb";

    @Property(label = "Compare Subscription Row Alt Background (Dark)", description = "Compare subscription row alt background in dark theme")
    String darkCompareSubscriptionRowAltBg() default "rgba(255, 255, 255, 0.02)";

    @Property(label = "Compare Subscription Row Alt Background (Light)", description = "Compare subscription row alt background in light theme")
    String lightCompareSubscriptionRowAltBg() default "rgba(0, 0, 0, 0.02)";

    @Property(label = "Compare Subscription Section Background (Dark)", description = "Compare subscription section background in dark theme")
    String darkCompareSubscriptionSectionBg() default "#262626";

    @Property(label = "Compare Subscription Section Background (Light)", description = "Compare subscription section background in light theme")
    String lightCompareSubscriptionSectionBg() default "#f3f4f6";

    @Property(label = "Compare Subscription Highlight Background (Dark)", description = "Compare subscription highlight background in dark theme")
    String darkCompareSubscriptionHighlightBg() default "rgba(255, 200, 70, 0.08)";

    @Property(label = "Compare Subscription Highlight Background (Light)", description = "Compare subscription highlight background in light theme")
    String lightCompareSubscriptionHighlightBg() default "rgba(59, 130, 246, 0.05)";

    @Property(label = "Compare Subscription Highlight Accent (Light)", description = "Compare subscription highlight accent in light theme")
    String lightCompareSubscriptionHighlightAccent() default "#3b82f6";

    @Property(label = "Compare Subscription Price Color (Light)", description = "Compare subscription price color in light theme")
    String lightCompareSubscriptionPriceColor() default "#3b82f6";

    @Property(label = "Compare Subscription Check Color (Dark)", description = "Compare subscription check color in dark theme")
    String darkCompareSubscriptionCheckColor() default "#4ade80";

    @Property(label = "Compare Subscription Check Color (Light)", description = "Compare subscription check color in light theme")
    String lightCompareSubscriptionCheckColor() default "#22c55e";

    @Property(label = "Compare Subscription Cross Color (Dark)", description = "Compare subscription cross color in dark theme")
    String darkCompareSubscriptionCrossColor() default "#ef4444";

    @Property(label = "Compare Subscription Cross Color (Light)", description = "Compare subscription cross color in light theme")
    String lightCompareSubscriptionCrossColor() default "#ef4444";

    @Property(label = "Compare Subscription CTA Primary Background (Dark)", description = "Compare subscription CTA primary background in dark theme")
    String darkCompareSubscriptionCtaPrimaryBg() default "#02c36f";

    @Property(label = "Compare Subscription CTA Primary Background (Light)", description = "Compare subscription CTA primary background in light theme")
    String lightCompareSubscriptionCtaPrimaryBg() default "#3b82f6";

    @Property(label = "Compare Subscription CTA Primary Hover (Dark)", description = "Compare subscription CTA primary hover in dark theme")
    String darkCompareSubscriptionCtaPrimaryHover() default "#02a25d";

    @Property(label = "Compare Subscription CTA Primary Hover (Light)", description = "Compare subscription CTA primary hover in light theme")
    String lightCompareSubscriptionCtaPrimaryHover() default "#2563eb";

    // ==================== RATING VARIABLES ====================

    @Property(label = "Rating Background (Dark)", description = "Rating section background in dark theme")
    String darkRatingBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Rating Background (Light)", description = "Rating section background in light theme")
    String lightRatingBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Rating Avatar Border (Dark)", description = "Rating avatar border in dark theme")
    String darkRatingAvatarBorder() default "#2a2a2a";

    @Property(label = "Rating Avatar Border (Light)", description = "Rating avatar border in light theme")
    String lightRatingAvatarBorder() default "#ffffff";

    @Property(label = "Rating Avatar Background (Dark)", description = "Rating avatar background in dark theme")
    String darkRatingAvatarBg() default "#404040";

    @Property(label = "Rating Avatar Background (Light)", description = "Rating avatar background in light theme")
    String lightRatingAvatarBg() default "#e5e7eb";

    @Property(label = "Rating Star Color (Dark)", description = "Rating star color in dark theme")
    String darkRatingStarColor() default "#fbbf24";

    @Property(label = "Rating Star Color (Light)", description = "Rating star color in light theme")
    String lightRatingStarColor() default "#fbbf24";

    @Property(label = "Rating Star Empty Color (Dark)", description = "Rating star empty color in dark theme")
    String darkRatingStarEmptyColor() default "#525252";

    @Property(label = "Rating Star Empty Color (Light)", description = "Rating star empty color in light theme")
    String lightRatingStarEmptyColor() default "#d1d5db";

    @Property(label = "Rating CTA Background (Dark)", description = "Rating CTA background in dark theme")
    String darkRatingCtaBg() default "#ffffff";

    @Property(label = "Rating CTA Background (Light)", description = "Rating CTA background in light theme")
    String lightRatingCtaBg() default "#000000";

    @Property(label = "Rating CTA Text (Dark)", description = "Rating CTA text color in dark theme")
    String darkRatingCtaText() default "#000000";

    @Property(label = "Rating CTA Text (Light)", description = "Rating CTA text color in light theme")
    String lightRatingCtaText() default "#ffffff";

    @Property(label = "Rating CTA Hover Background (Dark)", description = "Rating CTA hover background in dark theme")
    String darkRatingCtaHoverBg() default "#e0e0e0";

    @Property(label = "Rating CTA Hover Background (Light)", description = "Rating CTA hover background in light theme")
    String lightRatingCtaHoverBg() default "#333333";

    @Property(label = "Rating CTA Icon Background (Dark)", description = "Rating CTA icon background in dark theme")
    String darkRatingCtaIconBg() default "#000000";

    @Property(label = "Rating CTA Icon Background (Light)", description = "Rating CTA icon background in light theme")
    String lightRatingCtaIconBg() default "#ffffff";

    @Property(label = "Rating CTA Icon Color (Dark)", description = "Rating CTA icon color in dark theme")
    String darkRatingCtaIconColor() default "#ffffff";

    @Property(label = "Rating CTA Icon Color (Light)", description = "Rating CTA icon color in light theme")
    String lightRatingCtaIconColor() default "#000000";

    // ==================== MARQUEE CAROUSEL VARIABLES ====================

    @Property(label = "Marquee Carousel Background (Dark)", description = "Marquee carousel section background in dark theme")
    String darkMarqueeCarouselBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Marquee Carousel Background (Light)", description = "Marquee carousel section background in light theme")
    String lightMarqueeCarouselBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Marquee Carousel Fade Color (Light)", description = "Marquee carousel fade color in light theme")
    String lightMarqueeCarouselFadeColor() default "#dbeeff";

    @Property(label = "Marquee Carousel Card Background (Dark)", description = "Marquee carousel card background in dark theme")
    String darkMarqueeCarouselCardBg() default "#404040";

    @Property(label = "Marquee Carousel Card Background (Light)", description = "Marquee carousel card background in light theme")
    String lightMarqueeCarouselCardBg() default "#e5e7eb";

    @Property(label = "Marquee Carousel CTA Background (Dark)", description = "Marquee carousel CTA background in dark theme")
    String darkMarqueeCarouselCtaBg() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Background (Light)", description = "Marquee carousel CTA background in light theme")
    String lightMarqueeCarouselCtaBg() default "#000000";

    @Property(label = "Marquee Carousel CTA Text (Dark)", description = "Marquee carousel CTA text color in dark theme")
    String darkMarqueeCarouselCtaText() default "#000000";

    @Property(label = "Marquee Carousel CTA Text (Light)", description = "Marquee carousel CTA text color in light theme")
    String lightMarqueeCarouselCtaText() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Hover Background (Dark)", description = "Marquee carousel CTA hover background in dark theme")
    String darkMarqueeCarouselCtaHoverBg() default "#e0e0e0";

    @Property(label = "Marquee Carousel CTA Hover Background (Light)", description = "Marquee carousel CTA hover background in light theme")
    String lightMarqueeCarouselCtaHoverBg() default "#333333";

    @Property(label = "Marquee Carousel CTA Icon Background (Dark)", description = "Marquee carousel CTA icon background in dark theme")
    String darkMarqueeCarouselCtaIconBg() default "#000000";

    @Property(label = "Marquee Carousel CTA Icon Background (Light)", description = "Marquee carousel CTA icon background in light theme")
    String lightMarqueeCarouselCtaIconBg() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Icon Color (Dark)", description = "Marquee carousel CTA icon color in dark theme")
    String darkMarqueeCarouselCtaIconColor() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Icon Color (Light)", description = "Marquee carousel CTA icon color in light theme")
    String lightMarqueeCarouselCtaIconColor() default "#000000";

    @Property(label = "Marquee Carousel Duration", description = "Marquee carousel animation duration")
    String marqueeCarouselDuration() default "35s";

    // ==================== MASONRY GALLERY VARIABLES ====================

    @Property(label = "Masonry Gallery Background (Dark)", description = "Masonry gallery section background in dark theme")
    String darkMasonryGalleryBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Masonry Gallery Background (Light)", description = "Masonry gallery section background in light theme")
    String lightMasonryGalleryBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Masonry Gallery Item Background (Dark)", description = "Masonry gallery item background in dark theme")
    String darkMasonryGalleryItemBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Masonry Gallery Item Background (Light)", description = "Masonry gallery item background in light theme")
    String lightMasonryGalleryItemBg() default "rgba(0, 0, 0, 0.03)";

    @Property(label = "Masonry Gallery Lightbox Background (Dark)", description = "Masonry gallery lightbox background in dark theme")
    String darkMasonryGalleryLightboxBg() default "rgba(10, 10, 10, 0.95)";

    @Property(label = "Masonry Gallery Lightbox Background (Light)", description = "Masonry gallery lightbox background in light theme")
    String lightMasonryGalleryLightboxBg() default "rgba(10, 10, 10, 0.95)";

    @Property(label = "Masonry Gallery Lightbox Title Color (Dark)", description = "Masonry gallery lightbox title color in dark theme")
    String darkMasonryGalleryLightboxTitleColor() default "#ffffff";

    @Property(label = "Masonry Gallery Lightbox Title Color (Light)", description = "Masonry gallery lightbox title color in light theme")
    String lightMasonryGalleryLightboxTitleColor() default "#ffffff";

    @Property(label = "Masonry Gallery Lightbox Title Background (Dark)", description = "Masonry gallery lightbox title background in dark theme")
    String darkMasonryGalleryLightboxTitleBg() default "rgba(0, 0, 0, 0.7)";

    @Property(label = "Masonry Gallery Lightbox Title Background (Light)", description = "Masonry gallery lightbox title background in light theme")
    String lightMasonryGalleryLightboxTitleBg() default "rgba(0, 0, 0, 0.7)";

    // ==================== COMPARISON VARIABLES ====================

    @Property(label = "Comparison Background (Dark)", description = "Comparison section background in dark theme")
    String darkComparisonBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Comparison Background (Light)", description = "Comparison section background in light theme")
    String lightComparisonBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";

    @Property(label = "Comparison Column Background (Dark)", description = "Comparison column background in dark theme")
    String darkComparisonColBg() default "#2a2a2a";

    @Property(label = "Comparison Column Background (Light)", description = "Comparison column background in light theme")
    String lightComparisonColBg() default "#ffffff";

    // ==================== FAQ VARIABLES ====================

    @Property(label = "FAQ Background (Dark)", description = "FAQ section background in dark theme")
    String darkFaqBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "FAQ Background (Light)", description = "FAQ section background in light theme")
    String lightFaqBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "FAQ Item Background (Dark)", description = "FAQ item background in dark theme")
    String darkFaqItemBg() default "#2a2a2a";

    @Property(label = "FAQ Item Background (Light)", description = "FAQ item background in light theme")
    String lightFaqItemBg() default "#f2fffd";

    @Property(label = "FAQ Item Hover Background (Dark)", description = "FAQ item hover background in dark theme")
    String darkFaqItemHoverBg() default "#333333";

    @Property(label = "FAQ Item Hover Background (Light)", description = "FAQ item hover background in light theme")
    String lightFaqItemHoverBg() default "#e5e7eb";

    // ==================== BLOB IMAGE SECTION VARIABLES ====================

    @Property(label = "Blob Image Section Background (Dark)", description = "Blob image section background in dark theme")
    String darkBlobImageSectionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Blob Image Section Background (Light)", description = "Blob image section background in light theme")
    String lightBlobImageSectionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";

    @Property(label = "Blob Image Section Card Background (Dark)", description = "Blob image section card background in dark theme")
    String darkBlobImageSectionCardBg() default "#2a2a2a";

    @Property(label = "Blob Image Section Card Background (Light)", description = "Blob image section card background in light theme")
    String lightBlobImageSectionCardBg() default "#f2fffd";

    @Property(label = "Blob Image Section Badge Background (Dark)", description = "Blob image section badge background in dark theme")
    String darkBlobImageSectionBadgeBg() default "#363636";

    @Property(label = "Blob Image Section Badge Background (Light)", description = "Blob image section badge background in light theme")
    String lightBlobImageSectionBadgeBg() default "#ffffff";

    @Property(label = "Blob Image Section Badge Border (Dark)", description = "Blob image section badge border in dark theme")
    String darkBlobImageSectionBadgeBorder() default "#404040";

    @Property(label = "Blob Image Section Badge Border (Light)", description = "Blob image section badge border in light theme")
    String lightBlobImageSectionBadgeBorder() default "#e5e7eb";

    @Property(label = "Blob Image Section Badge Text (Light)", description = "Blob image section badge text in light theme")
    String lightBlobImageSectionBadgeText() default "#3b82f6";

    @Property(label = "Blob Image Section Icon Badge Background (Dark)", description = "Blob image section icon badge background in dark theme")
    String darkBlobImageSectionIconBadgeBg() default "#8b5cf6";

    @Property(label = "Blob Image Section Icon Badge Background (Light)", description = "Blob image section icon badge background in light theme")
    String lightBlobImageSectionIconBadgeBg() default "#8b5cf6";

    @Property(label = "Blob Image Section Icon Badge Color (Dark)", description = "Blob image section icon badge color in dark theme")
    String darkBlobImageSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Blob Image Section Icon Badge Color (Light)", description = "Blob image section icon badge color in light theme")
    String lightBlobImageSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Blob Image Section Overlay Card Background (Dark)", description = "Blob image section overlay card background in dark theme")
    String darkBlobImageSectionOverlayCardBg() default "#363636";

    @Property(label = "Blob Image Section Overlay Card Background (Light)", description = "Blob image section overlay card background in light theme")
    String lightBlobImageSectionOverlayCardBg() default "#ffffff";

    // ==================== LEAD MEDIA SECTION VARIABLES ====================

    @Property(label = "Lead Media Section Background (Dark)", description = "Lead media section background in dark theme")
    String darkLeadMediaSectionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Lead Media Section Background (Light)", description = "Lead media section background in light theme")
    String lightLeadMediaSectionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Lead Media Section Card Background (Dark)", description = "Lead media section card background in dark theme")
    String darkLeadMediaSectionCardBg() default "#2a2a2a";

    @Property(label = "Lead Media Section Card Background (Light)", description = "Lead media section card background in light theme")
    String lightLeadMediaSectionCardBg() default "#f2fffd";

    @Property(label = "Lead Media Section Icon Badge Background (Dark)", description = "Lead media section icon badge background in dark theme")
    String darkLeadMediaSectionIconBadgeBg() default "#3a3a3a";

    @Property(label = "Lead Media Section Icon Badge Background (Light)", description = "Lead media section icon badge background in light theme")
    String lightLeadMediaSectionIconBadgeBg() default "#e5e7eb";

    @Property(label = "Lead Media Section Icon Badge Color (Dark)", description = "Lead media section icon badge color in dark theme")
    String darkLeadMediaSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Lead Media Section Icon Badge Color (Light)", description = "Lead media section icon badge color in light theme")
    String lightLeadMediaSectionIconBadgeColor() default "#374151";

    @Property(label = "Lead Media Section CTA Background (Dark)", description = "Lead media section CTA background in dark theme")
    String darkLeadMediaSectionCtaBg() default "#ffffff";

    @Property(label = "Lead Media Section CTA Background (Light)", description = "Lead media section CTA background in light theme")
    String lightLeadMediaSectionCtaBg() default "#000000";

    @Property(label = "Lead Media Section CTA Text (Dark)", description = "Lead media section CTA text color in dark theme")
    String darkLeadMediaSectionCtaText() default "#000000";

    @Property(label = "Lead Media Section CTA Text (Light)", description = "Lead media section CTA text color in light theme")
    String lightLeadMediaSectionCtaText() default "#ffffff";

    @Property(label = "Lead Media Section CTA Hover Background (Dark)", description = "Lead media section CTA hover background in dark theme")
    String darkLeadMediaSectionCtaHoverBg() default "#e0e0e0";

    @Property(label = "Lead Media Section CTA Hover Background (Light)", description = "Lead media section CTA hover background in light theme")
    String lightLeadMediaSectionCtaHoverBg() default "#333333";

    @Property(label = "Lead Media Section CTA Icon Background (Dark)", description = "Lead media section CTA icon background in dark theme")
    String darkLeadMediaSectionCtaIconBg() default "#3b82f6";

    @Property(label = "Lead Media Section CTA Icon Background (Light)", description = "Lead media section CTA icon background in light theme")
    String lightLeadMediaSectionCtaIconBg() default "#3b82f6";

    @Property(label = "Lead Media Section CTA Icon Color (Dark)", description = "Lead media section CTA icon color in dark theme")
    String darkLeadMediaSectionCtaIconColor() default "#ffffff";

    @Property(label = "Lead Media Section CTA Icon Color (Light)", description = "Lead media section CTA icon color in light theme")
    String lightLeadMediaSectionCtaIconColor() default "#ffffff";

    @Property(label = "Lead Media Section Media Background (Dark)", description = "Lead media section media background in dark theme")
    String darkLeadMediaSectionMediaBg() default "#1a1a1a";

    @Property(label = "Lead Media Section Media Background (Light)", description = "Lead media section media background in light theme")
    String lightLeadMediaSectionMediaBg() default "#e5e7eb";

    @Property(label = "Lead Media Section Media Border (Dark)", description = "Lead media section media border in dark theme")
    String darkLeadMediaSectionMediaBorder() default "rgba(180, 180, 180, 0.4)";

    @Property(label = "Lead Media Section Media Border (Light)", description = "Lead media section media border in light theme")
    String lightLeadMediaSectionMediaBorder() default "rgba(180, 180, 180, 0.5)";

    // ==================== GRID CONTROL VARIABLES ====================

    @Property(label = "Grid Control Background (Dark)", description = "Grid control component background in dark theme")
    String darkGridControlBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Background (Light)", description = "Grid control component background in light theme")
    String lightGridControlBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Column Background (Dark)", description = "Grid control column background in dark theme")
    String darkGridControlColumnBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Column Background (Light)", description = "Grid control column background in light theme")
    String lightGridControlColumnBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";
}

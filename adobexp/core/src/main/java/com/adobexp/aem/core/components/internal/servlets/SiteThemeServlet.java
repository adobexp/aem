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
package com.adobexp.aem.core.components.internal.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.ConfigurationResolver;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.config.SiteThemeConfig;

/**
 * Servlet that generates CSS containing theme variables from Context-Aware Configuration.
 * 
 * This servlet is bound to page resource types and responds to the "theme-variables.css" selector.
 * 
 * Usage in HTL:
 * <link rel="stylesheet" href="${currentPage.path}.theme-variables.css" type="text/css">
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = {
                "adobexp/components/global/pages/page/v1/page",
                "adobexp/components/global/pages/rootpage/v1/rootpage",
                "cq/experience-fragments/components/xfpage"
        },
        methods = HttpConstants.METHOD_GET,
        selectors = "theme-variables",
        extensions = "css"
)
public class SiteThemeServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SiteThemeServlet.class);
    private static final String CONTENT_TYPE_CSS = "text/css;charset=UTF-8";

    @Reference
    private transient ConfigurationResolver configurationResolver;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws IOException {
        
        response.setContentType(CONTENT_TYPE_CSS);
        response.setCharacterEncoding("UTF-8");
        
        // Enable caching for 1 hour
        response.setHeader("Cache-Control", "public, max-age=3600");
        
        Resource resource = request.getResource();
        SiteThemeConfig config = null;
        
        if (configurationResolver != null && resource != null) {
            ConfigurationBuilder configBuilder = configurationResolver.get(resource);
            config = configBuilder.as(SiteThemeConfig.class);
        }
        
        PrintWriter writer = response.getWriter();
        generateThemeCss(writer, config);
    }

    private void generateThemeCss(PrintWriter writer, SiteThemeConfig config) {
        // Dark Theme
        writer.println(".theme-dark {");
        writer.println("  /* Main theme color */");
        writer.println("  --main-theme-color: " + getOrDefault(config, c -> c.darkMainThemeColor(), "#e3a002") + ";");
        writer.println();
        writer.println("  /* Header theme variables */");
        writer.println("  --header-background-color: " + getOrDefault(config, c -> c.darkHeaderBackgroundColor(), "#212020") + ";");
        writer.println("  --header-height: " + getOrDefault(config, c -> c.headerHeight(), "60px") + ";");
        writer.println();
        writer.println("  /* Text color variables - Dark theme */");
        writer.println("  --primary-text-color: " + getOrDefault(config, c -> c.darkPrimaryTextColor(), "#ffc846") + ";");
        writer.println("  --secondary-text-color: " + getOrDefault(config, c -> c.darkSecondaryTextColor(), "#ffedc2") + ";");
        writer.println("  --standard-primary-site-text-color: " + getOrDefault(config, c -> c.darkStandardPrimarySiteTextColor(), "#ffffff") + ";");
        writer.println("  --standard-secondary-site-text-color: " + getOrDefault(config, c -> c.darkStandardSecondarySiteTextColor(), "#a2a2a2") + ";");
        writer.println("  --standard-site-font-size: " + getOrDefault(config, c -> c.standardSiteFontSize(), "16px") + ";");
        writer.println("  --standard-site-font-weight: " + getOrDefault(config, c -> c.standardSiteFontWeight(), "400") + ";");
        writer.println();
        writer.println("  /* Global page */");
        writer.println("  --site-body-bg: " + getOrDefault(config, c -> c.darkSiteBodyBg(), "#1e1e1e") + ";");
        writer.println("  --site-body-text: var(--standard-primary-site-text-color);");
        writer.println("  --site-overlay-bg: " + getOrDefault(config, c -> c.darkSiteOverlayBg(), "rgba(0, 0, 0, 0.466)") + ";");
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(config, c -> c.darkFooterBg(), "#363535") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(config, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(config, c -> c.darkServicesDividerColor(), "rgba(255, 255, 255, 0.12)") + ";");
        writer.println("  --services-bg: " + getOrDefault(config, c -> c.darkServicesBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Dark theme */");
        writer.println("  --button-theme-dark-bg: " + getOrDefault(config, c -> c.darkButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-dark-text: " + getOrDefault(config, c -> c.darkButtonText(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-border: " + getOrDefault(config, c -> c.darkButtonBorder(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-bg: " + getOrDefault(config, c -> c.darkButtonHoverBg(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-text: " + getOrDefault(config, c -> c.darkButtonHoverText(), "#000000") + ";");
        writer.println();
        writer.println("  /* CTA Pill Button - Dark theme */");
        writer.println("  --cta-pill-dark-bg: " + getOrDefault(config, c -> c.darkCtaPillBg(), "#ffffff") + ";");
        writer.println("  --cta-pill-dark-text: " + getOrDefault(config, c -> c.darkCtaPillText(), "#000000") + ";");
        writer.println("  --cta-pill-dark-hover-bg: " + getOrDefault(config, c -> c.darkCtaPillHoverBg(), "#e0e0e0") + ";");
        writer.println("  --cta-pill-dark-icon-bg: " + getOrDefault(config, c -> c.darkCtaPillIconBg(), "#000000") + ";");
        writer.println("  --cta-pill-dark-icon-color: " + getOrDefault(config, c -> c.darkCtaPillIconColor(), "#ffffff") + ";");
        writer.println("  /* CTA Pill base variables (mapped from dark theme) */");
        writer.println("  --cta-pill-bg: var(--cta-pill-dark-bg);");
        writer.println("  --cta-pill-text: var(--cta-pill-dark-text);");
        writer.println("  --cta-pill-hover-bg: var(--cta-pill-dark-hover-bg);");
        writer.println("  --cta-pill-icon-bg: var(--cta-pill-dark-icon-bg);");
        writer.println("  --cta-pill-icon-color: var(--cta-pill-dark-icon-color);");
        writer.println();
        writer.println("  /* TwoToneTextTeaser */");
        writer.println("  --two-tone-text-teaser-bg: var(--site-body-bg);");
        writer.println();
        writer.println("  /* Lead Banner gradient variables - Dark theme */");
        writer.println("  --lead-banner-height: " + getOrDefault(config, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(config, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(config, c -> c.darkLeadBannerGradientStart(), "#212020") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(config, c -> c.darkLeadBannerGradientStop25(), "#aa7802") + ";");
        writer.println("  --lead-banner-gradient-stop-50: var(--main-theme-color);");
        writer.println("  --lead-banner-gradient-stop-75: " + getOrDefault(config, c -> c.darkLeadBannerGradientStop75(), "#aa7802") + ";");
        writer.println("  --lead-banner-gradient-end: " + getOrDefault(config, c -> c.darkLeadBannerGradientEnd(), "#212020") + ";");
        writer.println("  --lead-banner-text-primary: " + getOrDefault(config, c -> c.darkLeadBannerTextPrimary(), "#ffffff") + ";");
        writer.println("  --lead-banner-text-secondary: " + getOrDefault(config, c -> c.darkLeadBannerTextSecondary(), "#242424") + ";");
        writer.println("  --lead-banner-secondary-text-color: " + getOrDefault(config, c -> c.darkLeadBannerSecondaryTextColor(), "#fffffa") + ";");
        writer.println("  --lead-banner-char-fade-duration: " + getOrDefault(config, c -> c.leadBannerCharFadeDuration(), "0.3s") + ";");
        writer.println();
        writer.println("  /* Article tiles (Header overlay) */");
        writer.println("  --article-tile-overlay-bg: " + getOrDefault(config, c -> c.darkArticleTileOverlayBg(), "rgba(0, 0, 0, 0.8)") + ";");
        writer.println();
        writer.println("  /* Header overlay */");
        writer.println("  --header-overlay-column-divider-color: " + getOrDefault(config, c -> c.darkHeaderOverlayColumnDividerColor(), "rgba(255, 255, 255, 0.12)") + ";");
        writer.println("  --header-overlay-hover-bg: " + getOrDefault(config, c -> c.darkHeaderOverlayHoverBg(), "rgba(255, 255, 255, 0.10)") + ";");
        writer.println();
        writer.println("  /* Site banner */");
        writer.println("  --site-banner-bg: " + getOrDefault(config, c -> c.darkSiteBannerBg(), "#363535") + ";");
        writer.println("  --site-banner-text-color: var(--primary-text-color);");
        writer.println("  --site-banner-marquee-duration: " + getOrDefault(config, c -> c.darkSiteBannerMarqueeDuration(), "5s") + ";");
        writer.println("  --site-banner-cycle-duration: " + getOrDefault(config, c -> c.darkSiteBannerCycleDuration(), "10s") + ";");
        writer.println("  --site-banner-font-size: " + getOrDefault(config, c -> c.siteBannerFontSize(), "20px") + ";");
        writer.println();
        writer.println("  /* Quote */");
        writer.println("  --quote-bg: " + getOrDefault(config, c -> c.darkQuoteBg(), "#363535") + ";");
        writer.println("  --quote-card-glow: " + getOrDefault(config, c -> c.darkQuoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(config, c -> c.darkLoopingCircleGalleryOverlayBg(), "rgba(255, 255, 255, 0.5)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: " + getOrDefault(config, c -> c.darkLoopingCircleGalleryOverlayText(), "#000000") + ";");
        writer.println();
        writer.println("  /* CountUp */");
        writer.println("  --count-up-bg: " + getOrDefault(config, c -> c.darkCountUpBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --count-up-card-bg: " + getOrDefault(config, c -> c.darkCountUpCardBg(), "#2a2a2a") + ";");
        writer.println();
        writer.println("  /* SubscriptionPlans */");
        writer.println("  --subscription-plans-bg: " + getOrDefault(config, c -> c.darkSubscriptionPlansBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --subscription-plans-card-bg: " + getOrDefault(config, c -> c.darkSubscriptionPlansCardBg(), "#2a2a2a") + ";");
        writer.println("  --subscription-plans-card-border: " + getOrDefault(config, c -> c.darkSubscriptionPlansCardBorder(), "#404040") + ";");
        writer.println("  --subscription-plans-toggle-bg: " + getOrDefault(config, c -> c.darkSubscriptionPlansToggleBg(), "#374151") + ";");
        writer.println("  --subscription-plans-toggle-active-bg: var(--primary-text-color);");
        writer.println("  --subscription-plans-toggle-active-text: " + getOrDefault(config, c -> c.darkSubscriptionPlansToggleActiveText(), "#ffffff") + ";");
        writer.println("  --subscription-plans-save-badge-bg: " + getOrDefault(config, c -> c.darkSubscriptionPlansSaveBadgeBg(), "rgba(34, 197, 94, 0.2)") + ";");
        writer.println("  --subscription-plans-save-badge-text: " + getOrDefault(config, c -> c.darkSubscriptionPlansSaveBadgeText(), "#4ade80") + ";");
        writer.println("  --subscription-plans-price-color: var(--primary-text-color);");
        writer.println("  --subscription-plans-highlight-border: var(--primary-text-color);");
        writer.println("  --subscription-plans-divider: " + getOrDefault(config, c -> c.darkSubscriptionPlansDivider(), "#404040") + ";");
        writer.println("  --subscription-plans-credits-icon: var(--primary-text-color);");
        writer.println("  --subscription-plans-feature-check: " + getOrDefault(config, c -> c.darkSubscriptionPlansFeatureCheck(), "#4ade80") + ";");
        writer.println("  --subscription-plans-cta-primary-bg: " + getOrDefault(config, c -> c.darkSubscriptionPlansCtaPrimaryBg(), "#02c36f") + ";");
        writer.println("  --subscription-plans-cta-primary-hover: " + getOrDefault(config, c -> c.darkSubscriptionPlansCtaPrimaryHover(), "#02a25d") + ";");
        writer.println();
        writer.println("  /* CompareSubscription */");
        writer.println("  --compare-subscription-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --compare-subscription-table-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionTableBg(), "#2a2a2a") + ";");
        writer.println("  --compare-subscription-border: " + getOrDefault(config, c -> c.darkCompareSubscriptionBorder(), "#404040") + ";");
        writer.println("  --compare-subscription-features-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionFeaturesBg(), "#1f1f1f") + ";");
        writer.println("  --compare-subscription-row-alt-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionRowAltBg(), "rgba(255, 255, 255, 0.02)") + ";");
        writer.println("  --compare-subscription-section-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionSectionBg(), "#262626") + ";");
        writer.println("  --compare-subscription-highlight-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionHighlightBg(), "rgba(255, 200, 70, 0.08)") + ";");
        writer.println("  --compare-subscription-highlight-accent: var(--primary-text-color);");
        writer.println("  --compare-subscription-price-color: var(--primary-text-color);");
        writer.println("  --compare-subscription-check-color: " + getOrDefault(config, c -> c.darkCompareSubscriptionCheckColor(), "#4ade80") + ";");
        writer.println("  --compare-subscription-cross-color: " + getOrDefault(config, c -> c.darkCompareSubscriptionCrossColor(), "#ef4444") + ";");
        writer.println("  --compare-subscription-cta-primary-bg: " + getOrDefault(config, c -> c.darkCompareSubscriptionCtaPrimaryBg(), "#02c36f") + ";");
        writer.println("  --compare-subscription-cta-primary-hover: " + getOrDefault(config, c -> c.darkCompareSubscriptionCtaPrimaryHover(), "#02a25d") + ";");
        writer.println();
        writer.println("  /* Rating */");
        writer.println("  --rating-bg: " + getOrDefault(config, c -> c.darkRatingBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --rating-avatar-border: " + getOrDefault(config, c -> c.darkRatingAvatarBorder(), "#2a2a2a") + ";");
        writer.println("  --rating-avatar-bg: " + getOrDefault(config, c -> c.darkRatingAvatarBg(), "#404040") + ";");
        writer.println("  --rating-star-color: " + getOrDefault(config, c -> c.darkRatingStarColor(), "#fbbf24") + ";");
        writer.println("  --rating-star-empty-color: " + getOrDefault(config, c -> c.darkRatingStarEmptyColor(), "#525252") + ";");
        writer.println("  --rating-cta-bg: " + getOrDefault(config, c -> c.darkRatingCtaBg(), "#ffffff") + ";");
        writer.println("  --rating-cta-text: " + getOrDefault(config, c -> c.darkRatingCtaText(), "#000000") + ";");
        writer.println("  --rating-cta-hover-bg: " + getOrDefault(config, c -> c.darkRatingCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --rating-cta-icon-bg: " + getOrDefault(config, c -> c.darkRatingCtaIconBg(), "#000000") + ";");
        writer.println("  --rating-cta-icon-color: " + getOrDefault(config, c -> c.darkRatingCtaIconColor(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* MarqueeCarousel */");
        writer.println("  --marquee-carousel-bg: " + getOrDefault(config, c -> c.darkMarqueeCarouselBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --marquee-carousel-fade-color: var(--site-body-bg);");
        writer.println("  --marquee-carousel-card-bg: " + getOrDefault(config, c -> c.darkMarqueeCarouselCardBg(), "#404040") + ";");
        writer.println("  --marquee-carousel-cta-bg: " + getOrDefault(config, c -> c.darkMarqueeCarouselCtaBg(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-text: " + getOrDefault(config, c -> c.darkMarqueeCarouselCtaText(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-hover-bg: " + getOrDefault(config, c -> c.darkMarqueeCarouselCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --marquee-carousel-cta-icon-bg: " + getOrDefault(config, c -> c.darkMarqueeCarouselCtaIconBg(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-icon-color: " + getOrDefault(config, c -> c.darkMarqueeCarouselCtaIconColor(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-duration: " + getOrDefault(config, c -> c.marqueeCarouselDuration(), "35s") + ";");
        writer.println();
        writer.println("  /* MasonryGallery */");
        writer.println("  --masonry-gallery-bg: " + getOrDefault(config, c -> c.darkMasonryGalleryBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --masonry-gallery-item-bg: " + getOrDefault(config, c -> c.darkMasonryGalleryItemBg(), "rgba(255, 255, 255, 0.05)") + ";");
        writer.println("  --masonry-gallery-lightbox-bg: " + getOrDefault(config, c -> c.darkMasonryGalleryLightboxBg(), "rgba(10, 10, 10, 0.95)") + ";");
        writer.println("  --masonry-gallery-lightbox-title-color: " + getOrDefault(config, c -> c.darkMasonryGalleryLightboxTitleColor(), "#ffffff") + ";");
        writer.println("  --masonry-gallery-lightbox-title-bg: " + getOrDefault(config, c -> c.darkMasonryGalleryLightboxTitleBg(), "rgba(0, 0, 0, 0.7)") + ";");
        writer.println();
        writer.println("  /* Comparison */");
        writer.println("  --comparison-bg: " + getOrDefault(config, c -> c.darkComparisonBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --comparison-col-bg: " + getOrDefault(config, c -> c.darkComparisonColBg(), "#2a2a2a") + ";");
        writer.println();
        writer.println("  /* FAQ */");
        writer.println("  --faq-bg: " + getOrDefault(config, c -> c.darkFaqBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --faq-item-bg: " + getOrDefault(config, c -> c.darkFaqItemBg(), "#2a2a2a") + ";");
        writer.println("  --faq-item-hover-bg: " + getOrDefault(config, c -> c.darkFaqItemHoverBg(), "#333333") + ";");
        writer.println();
        writer.println("  /* BlobImageSection */");
        writer.println("  --blob-image-section-bg: " + getOrDefault(config, c -> c.darkBlobImageSectionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --blob-image-section-card-bg: " + getOrDefault(config, c -> c.darkBlobImageSectionCardBg(), "#2a2a2a") + ";");
        writer.println("  --blob-image-section-badge-bg: " + getOrDefault(config, c -> c.darkBlobImageSectionBadgeBg(), "#363636") + ";");
        writer.println("  --blob-image-section-badge-border: " + getOrDefault(config, c -> c.darkBlobImageSectionBadgeBorder(), "#404040") + ";");
        writer.println("  --blob-image-section-badge-text: var(--primary-text-color);");
        writer.println("  --blob-image-section-icon-badge-bg: " + getOrDefault(config, c -> c.darkBlobImageSectionIconBadgeBg(), "#8b5cf6") + ";");
        writer.println("  --blob-image-section-icon-badge-color: " + getOrDefault(config, c -> c.darkBlobImageSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --blob-image-section-overlay-card-bg: " + getOrDefault(config, c -> c.darkBlobImageSectionOverlayCardBg(), "#363636") + ";");
        writer.println();
        writer.println("  /* LeadMediaSection */");
        writer.println("  --lead-media-section-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --lead-media-section-card-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionCardBg(), "#2a2a2a") + ";");
        writer.println("  --lead-media-section-icon-badge-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionIconBadgeBg(), "#3a3a3a") + ";");
        writer.println("  --lead-media-section-icon-badge-color: " + getOrDefault(config, c -> c.darkLeadMediaSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionCtaBg(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-text: " + getOrDefault(config, c -> c.darkLeadMediaSectionCtaText(), "#000000") + ";");
        writer.println("  --lead-media-section-cta-hover-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --lead-media-section-cta-icon-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionCtaIconBg(), "#3b82f6") + ";");
        writer.println("  --lead-media-section-cta-icon-color: " + getOrDefault(config, c -> c.darkLeadMediaSectionCtaIconColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-media-bg: " + getOrDefault(config, c -> c.darkLeadMediaSectionMediaBg(), "#1a1a1a") + ";");
        writer.println("  --lead-media-section-media-border: " + getOrDefault(config, c -> c.darkLeadMediaSectionMediaBorder(), "rgba(180, 180, 180, 0.4)") + ";");
        writer.println("}");
        writer.println();
        
        // Light Theme
        writer.println(".theme-light {");
        writer.println("  /* Main theme color */");
        writer.println("  --main-theme-color: " + getOrDefault(config, c -> c.lightMainThemeColor(), "#42f4fd") + ";");
        writer.println();
        writer.println("  /* Header theme variables */");
        writer.println("  --header-background-color: " + getOrDefault(config, c -> c.lightHeaderBackgroundColor(), "#fdfeff") + ";");
        writer.println("  --header-height: " + getOrDefault(config, c -> c.headerHeight(), "60px") + ";");
        writer.println();
        writer.println("  /* Text color variables - Light theme */");
        writer.println("  --primary-text-color: " + getOrDefault(config, c -> c.lightPrimaryTextColor(), "#000000") + ";");
        writer.println("  --secondary-text-color: " + getOrDefault(config, c -> c.lightSecondaryTextColor(), "#4b5563") + ";");
        writer.println("  --standard-primary-site-text-color: " + getOrDefault(config, c -> c.lightStandardPrimarySiteTextColor(), "#111827") + ";");
        writer.println("  --standard-secondary-site-text-color: " + getOrDefault(config, c -> c.lightStandardSecondarySiteTextColor(), "#4b5563") + ";");
        writer.println("  --standard-site-font-size: " + getOrDefault(config, c -> c.standardSiteFontSize(), "16px") + ";");
        writer.println("  --standard-site-font-weight: " + getOrDefault(config, c -> c.standardSiteFontWeight(), "400") + ";");
        writer.println();
        writer.println("  /* Global page */");
        writer.println("  --site-body-bg: " + getOrDefault(config, c -> c.lightSiteBodyBg(), "#ffffff") + ";");
        writer.println("  --site-body-text: var(--standard-primary-site-text-color);");
        writer.println("  --site-overlay-bg: " + getOrDefault(config, c -> c.lightSiteOverlayBg(), "rgba(255, 255, 255, 0.727)") + ";");
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(config, c -> c.lightFooterBg(), "#f5f5f5") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(config, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(config, c -> c.lightServicesDividerColor(), "rgba(0, 0, 0, 0.12)") + ";");
        writer.println("  --services-bg: " + getOrDefault(config, c -> c.lightServicesBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Light theme */");
        writer.println("  --button-theme-light-bg: " + getOrDefault(config, c -> c.lightButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-light-text: " + getOrDefault(config, c -> c.lightButtonText(), "#000000") + ";");
        writer.println("  --button-theme-light-border: " + getOrDefault(config, c -> c.lightButtonBorder(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-bg: " + getOrDefault(config, c -> c.lightButtonHoverBg(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-text: " + getOrDefault(config, c -> c.lightButtonHoverText(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* CTA Pill Button - Light theme */");
        writer.println("  --cta-pill-light-bg: " + getOrDefault(config, c -> c.lightCtaPillBg(), "#000000") + ";");
        writer.println("  --cta-pill-light-text: " + getOrDefault(config, c -> c.lightCtaPillText(), "#ffffff") + ";");
        writer.println("  --cta-pill-light-hover-bg: " + getOrDefault(config, c -> c.lightCtaPillHoverBg(), "#333333") + ";");
        writer.println("  --cta-pill-light-icon-bg: " + getOrDefault(config, c -> c.lightCtaPillIconBg(), "#ffffff") + ";");
        writer.println("  --cta-pill-light-icon-color: " + getOrDefault(config, c -> c.lightCtaPillIconColor(), "#000000") + ";");
        writer.println("  /* CTA Pill base variables (mapped from light theme) */");
        writer.println("  --cta-pill-bg: var(--cta-pill-light-bg);");
        writer.println("  --cta-pill-text: var(--cta-pill-light-text);");
        writer.println("  --cta-pill-hover-bg: var(--cta-pill-light-hover-bg);");
        writer.println("  --cta-pill-icon-bg: var(--cta-pill-light-icon-bg);");
        writer.println("  --cta-pill-icon-color: var(--cta-pill-light-icon-color);");
        writer.println();
        writer.println("  /* TwoToneTextTeaser */");
        writer.println("  --two-tone-text-teaser-bg: var(--site-body-bg);");
        writer.println();
        writer.println("  /* Lead Banner gradient variables - Light theme */");
        writer.println("  --lead-banner-height: " + getOrDefault(config, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(config, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(config, c -> c.lightLeadBannerGradientStart(), "#ffffff") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(config, c -> c.lightLeadBannerGradientStop25(), "#aafbff") + ";");
        writer.println("  --lead-banner-gradient-stop-50: var(--main-theme-color);");
        writer.println("  --lead-banner-gradient-stop-75: " + getOrDefault(config, c -> c.lightLeadBannerGradientStop75(), "#aafbff") + ";");
        writer.println("  --lead-banner-gradient-end: " + getOrDefault(config, c -> c.lightLeadBannerGradientEnd(), "#ffffff") + ";");
        writer.println("  --lead-banner-text-primary: " + getOrDefault(config, c -> c.lightLeadBannerTextPrimary(), "#323232") + ";");
        writer.println("  --lead-banner-text-secondary: " + getOrDefault(config, c -> c.lightLeadBannerTextSecondary(), "#6e6e6e") + ";");
        writer.println("  --lead-banner-secondary-text-color: " + getOrDefault(config, c -> c.lightLeadBannerSecondaryTextColor(), "#323232") + ";");
        writer.println("  --lead-banner-char-fade-duration: " + getOrDefault(config, c -> c.leadBannerCharFadeDuration(), "0.3s") + ";");
        writer.println();
        writer.println("  /* Article tiles (Header overlay) */");
        writer.println("  --article-tile-overlay-bg: " + getOrDefault(config, c -> c.lightArticleTileOverlayBg(), "#42f4fd75") + ";");
        writer.println();
        writer.println("  /* Header overlay */");
        writer.println("  --header-overlay-column-divider-color: " + getOrDefault(config, c -> c.lightHeaderOverlayColumnDividerColor(), "rgba(0, 0, 0, 0.18)") + ";");
        writer.println("  --header-overlay-hover-bg: " + getOrDefault(config, c -> c.lightHeaderOverlayHoverBg(), "rgba(0, 0, 0, 0.08)") + ";");
        writer.println();
        writer.println("  /* Site banner */");
        writer.println("  --site-banner-bg: var(--main-theme-color);");
        writer.println("  --site-banner-text-color: var(--primary-text-color);");
        writer.println("  --site-banner-marquee-duration: " + getOrDefault(config, c -> c.lightSiteBannerMarqueeDuration(), "10s") + ";");
        writer.println("  --site-banner-cycle-duration: " + getOrDefault(config, c -> c.lightSiteBannerCycleDuration(), "20s") + ";");
        writer.println("  --site-banner-font-size: " + getOrDefault(config, c -> c.siteBannerFontSize(), "20px") + ";");
        writer.println();
        writer.println("  /* Quote */");
        writer.println("  --quote-bg: var(--site-body-bg);");
        writer.println("  --quote-card-glow: " + getOrDefault(config, c -> c.lightQuoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, var(--main-theme-color)ad, transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, var(--main-theme-color)5e, transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(config, c -> c.lightLoopingCircleGalleryOverlayBg(), "linear-gradient(180deg, #ffffff53 0%, var(--main-theme-color) 50%, #ffffff53 100%)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: var(--primary-text-color);");
        writer.println();
        writer.println("  /* CountUp */");
        writer.println("  --count-up-bg: " + getOrDefault(config, c -> c.lightCountUpBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --count-up-card-bg: " + getOrDefault(config, c -> c.lightCountUpCardBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* SubscriptionPlans */");
        writer.println("  --subscription-plans-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --subscription-plans-card-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansCardBg(), "#ffffff") + ";");
        writer.println("  --subscription-plans-card-border: " + getOrDefault(config, c -> c.lightSubscriptionPlansCardBorder(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-toggle-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansToggleBg(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-toggle-active-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansToggleActiveBg(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-toggle-active-text: " + getOrDefault(config, c -> c.lightSubscriptionPlansToggleActiveText(), "#ffffff") + ";");
        writer.println("  --subscription-plans-save-badge-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansSaveBadgeBg(), "#dcfce7") + ";");
        writer.println("  --subscription-plans-save-badge-text: " + getOrDefault(config, c -> c.lightSubscriptionPlansSaveBadgeText(), "#16a34a") + ";");
        writer.println("  --subscription-plans-price-color: " + getOrDefault(config, c -> c.lightSubscriptionPlansPriceColor(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-highlight-border: " + getOrDefault(config, c -> c.lightSubscriptionPlansHighlightBorder(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-divider: " + getOrDefault(config, c -> c.lightSubscriptionPlansDivider(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-credits-icon: " + getOrDefault(config, c -> c.lightSubscriptionPlansCreditsIcon(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-feature-check: " + getOrDefault(config, c -> c.lightSubscriptionPlansFeatureCheck(), "#22c55e") + ";");
        writer.println("  --subscription-plans-cta-primary-bg: " + getOrDefault(config, c -> c.lightSubscriptionPlansCtaPrimaryBg(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-cta-primary-hover: " + getOrDefault(config, c -> c.lightSubscriptionPlansCtaPrimaryHover(), "#2563eb") + ";");
        writer.println();
        writer.println("  /* CompareSubscription */");
        writer.println("  --compare-subscription-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --compare-subscription-table-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionTableBg(), "#ffffff") + ";");
        writer.println("  --compare-subscription-border: " + getOrDefault(config, c -> c.lightCompareSubscriptionBorder(), "#e5e7eb") + ";");
        writer.println("  --compare-subscription-features-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionFeaturesBg(), "#f9fafb") + ";");
        writer.println("  --compare-subscription-row-alt-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionRowAltBg(), "rgba(0, 0, 0, 0.02)") + ";");
        writer.println("  --compare-subscription-section-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionSectionBg(), "#f3f4f6") + ";");
        writer.println("  --compare-subscription-highlight-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionHighlightBg(), "rgba(59, 130, 246, 0.05)") + ";");
        writer.println("  --compare-subscription-highlight-accent: " + getOrDefault(config, c -> c.lightCompareSubscriptionHighlightAccent(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-price-color: " + getOrDefault(config, c -> c.lightCompareSubscriptionPriceColor(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-check-color: " + getOrDefault(config, c -> c.lightCompareSubscriptionCheckColor(), "#22c55e") + ";");
        writer.println("  --compare-subscription-cross-color: " + getOrDefault(config, c -> c.lightCompareSubscriptionCrossColor(), "#ef4444") + ";");
        writer.println("  --compare-subscription-cta-primary-bg: " + getOrDefault(config, c -> c.lightCompareSubscriptionCtaPrimaryBg(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-cta-primary-hover: " + getOrDefault(config, c -> c.lightCompareSubscriptionCtaPrimaryHover(), "#2563eb") + ";");
        writer.println();
        writer.println("  /* Rating */");
        writer.println("  --rating-bg: " + getOrDefault(config, c -> c.lightRatingBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --rating-avatar-border: " + getOrDefault(config, c -> c.lightRatingAvatarBorder(), "#ffffff") + ";");
        writer.println("  --rating-avatar-bg: " + getOrDefault(config, c -> c.lightRatingAvatarBg(), "#e5e7eb") + ";");
        writer.println("  --rating-star-color: " + getOrDefault(config, c -> c.lightRatingStarColor(), "#fbbf24") + ";");
        writer.println("  --rating-star-empty-color: " + getOrDefault(config, c -> c.lightRatingStarEmptyColor(), "#d1d5db") + ";");
        writer.println("  --rating-cta-bg: " + getOrDefault(config, c -> c.lightRatingCtaBg(), "#000000") + ";");
        writer.println("  --rating-cta-text: " + getOrDefault(config, c -> c.lightRatingCtaText(), "#ffffff") + ";");
        writer.println("  --rating-cta-hover-bg: " + getOrDefault(config, c -> c.lightRatingCtaHoverBg(), "#333333") + ";");
        writer.println("  --rating-cta-icon-bg: " + getOrDefault(config, c -> c.lightRatingCtaIconBg(), "#ffffff") + ";");
        writer.println("  --rating-cta-icon-color: " + getOrDefault(config, c -> c.lightRatingCtaIconColor(), "#000000") + ";");
        writer.println();
        writer.println("  /* MarqueeCarousel */");
        writer.println("  --marquee-carousel-bg: " + getOrDefault(config, c -> c.lightMarqueeCarouselBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --marquee-carousel-fade-color: " + getOrDefault(config, c -> c.lightMarqueeCarouselFadeColor(), "#dbeeff") + ";");
        writer.println("  --marquee-carousel-card-bg: " + getOrDefault(config, c -> c.lightMarqueeCarouselCardBg(), "#e5e7eb") + ";");
        writer.println("  --marquee-carousel-cta-bg: " + getOrDefault(config, c -> c.lightMarqueeCarouselCtaBg(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-text: " + getOrDefault(config, c -> c.lightMarqueeCarouselCtaText(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-hover-bg: " + getOrDefault(config, c -> c.lightMarqueeCarouselCtaHoverBg(), "#333333") + ";");
        writer.println("  --marquee-carousel-cta-icon-bg: " + getOrDefault(config, c -> c.lightMarqueeCarouselCtaIconBg(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-icon-color: " + getOrDefault(config, c -> c.lightMarqueeCarouselCtaIconColor(), "#000000") + ";");
        writer.println("  --marquee-carousel-duration: " + getOrDefault(config, c -> c.marqueeCarouselDuration(), "35s") + ";");
        writer.println();
        writer.println("  /* MasonryGallery */");
        writer.println("  --masonry-gallery-bg: " + getOrDefault(config, c -> c.lightMasonryGalleryBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --masonry-gallery-item-bg: " + getOrDefault(config, c -> c.lightMasonryGalleryItemBg(), "rgba(0, 0, 0, 0.03)") + ";");
        writer.println("  --masonry-gallery-lightbox-bg: " + getOrDefault(config, c -> c.lightMasonryGalleryLightboxBg(), "rgba(10, 10, 10, 0.95)") + ";");
        writer.println("  --masonry-gallery-lightbox-title-color: " + getOrDefault(config, c -> c.lightMasonryGalleryLightboxTitleColor(), "#ffffff") + ";");
        writer.println("  --masonry-gallery-lightbox-title-bg: " + getOrDefault(config, c -> c.lightMasonryGalleryLightboxTitleBg(), "rgba(0, 0, 0, 0.7)") + ";");
        writer.println();
        writer.println("  /* Comparison */");
        writer.println("  --comparison-bg: " + getOrDefault(config, c -> c.lightComparisonBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --comparison-col-bg: " + getOrDefault(config, c -> c.lightComparisonColBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* FAQ */");
        writer.println("  --faq-bg: " + getOrDefault(config, c -> c.lightFaqBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --faq-item-bg: " + getOrDefault(config, c -> c.lightFaqItemBg(), "#f2fffd") + ";");
        writer.println("  --faq-item-hover-bg: " + getOrDefault(config, c -> c.lightFaqItemHoverBg(), "#e5e7eb") + ";");
        writer.println();
        writer.println("  /* BlobImageSection */");
        writer.println("  --blob-image-section-bg: " + getOrDefault(config, c -> c.lightBlobImageSectionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --blob-image-section-card-bg: " + getOrDefault(config, c -> c.lightBlobImageSectionCardBg(), "#f2fffd") + ";");
        writer.println("  --blob-image-section-badge-bg: " + getOrDefault(config, c -> c.lightBlobImageSectionBadgeBg(), "#ffffff") + ";");
        writer.println("  --blob-image-section-badge-border: " + getOrDefault(config, c -> c.lightBlobImageSectionBadgeBorder(), "#e5e7eb") + ";");
        writer.println("  --blob-image-section-badge-text: " + getOrDefault(config, c -> c.lightBlobImageSectionBadgeText(), "#3b82f6") + ";");
        writer.println("  --blob-image-section-icon-badge-bg: " + getOrDefault(config, c -> c.lightBlobImageSectionIconBadgeBg(), "#8b5cf6") + ";");
        writer.println("  --blob-image-section-icon-badge-color: " + getOrDefault(config, c -> c.lightBlobImageSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --blob-image-section-overlay-card-bg: " + getOrDefault(config, c -> c.lightBlobImageSectionOverlayCardBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* LeadMediaSection */");
        writer.println("  --lead-media-section-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --lead-media-section-card-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionCardBg(), "#f2fffd") + ";");
        writer.println("  --lead-media-section-icon-badge-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionIconBadgeBg(), "#e5e7eb") + ";");
        writer.println("  --lead-media-section-icon-badge-color: " + getOrDefault(config, c -> c.lightLeadMediaSectionIconBadgeColor(), "#374151") + ";");
        writer.println("  --lead-media-section-cta-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionCtaBg(), "#000000") + ";");
        writer.println("  --lead-media-section-cta-text: " + getOrDefault(config, c -> c.lightLeadMediaSectionCtaText(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-hover-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionCtaHoverBg(), "#333333") + ";");
        writer.println("  --lead-media-section-cta-icon-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionCtaIconBg(), "#3b82f6") + ";");
        writer.println("  --lead-media-section-cta-icon-color: " + getOrDefault(config, c -> c.lightLeadMediaSectionCtaIconColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-media-bg: " + getOrDefault(config, c -> c.lightLeadMediaSectionMediaBg(), "#e5e7eb") + ";");
        writer.println("  --lead-media-section-media-border: " + getOrDefault(config, c -> c.lightLeadMediaSectionMediaBorder(), "rgba(180, 180, 180, 0.5)") + ";");
        writer.println("}");
    }

    @FunctionalInterface
    private interface ConfigGetter {
        String get(SiteThemeConfig config);
    }

    private String getOrDefault(SiteThemeConfig config, ConfigGetter getter, String defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        try {
            String value = getter.get(config);
            return (value != null && !value.isEmpty()) ? value : defaultValue;
        } catch (Exception e) {
            LOG.debug("Error getting config value, using default", e);
            return defaultValue;
        }
    }
}

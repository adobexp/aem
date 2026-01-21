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
package com.adobexp.aem.core.components.internal.models;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.ConfigurationResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.config.SiteThemeGlobalConfig;
import com.adobexp.aem.core.components.config.components.ArticleTileThemeConfig;
import com.adobexp.aem.core.components.config.components.BlobImageSectionThemeConfig;
import com.adobexp.aem.core.components.config.components.ButtonThemeConfig;
import com.adobexp.aem.core.components.config.components.CompareSubscriptionThemeConfig;
import com.adobexp.aem.core.components.config.components.ComparisonThemeConfig;
import com.adobexp.aem.core.components.config.components.CountUpThemeConfig;
import com.adobexp.aem.core.components.config.components.CtaPillThemeConfig;
import com.adobexp.aem.core.components.config.components.FaqThemeConfig;
import com.adobexp.aem.core.components.config.components.FooterThemeConfig;
import com.adobexp.aem.core.components.config.components.GridControlThemeConfig;
import com.adobexp.aem.core.components.config.components.HeaderOverlayThemeConfig;
import com.adobexp.aem.core.components.config.components.HeaderThemeConfig;
import com.adobexp.aem.core.components.config.components.LeadBannerThemeConfig;
import com.adobexp.aem.core.components.config.components.LeadMediaSectionThemeConfig;
import com.adobexp.aem.core.components.config.components.LoopingCircleGalleryThemeConfig;
import com.adobexp.aem.core.components.config.components.MarqueeCarouselThemeConfig;
import com.adobexp.aem.core.components.config.components.MasonryGalleryThemeConfig;
import com.adobexp.aem.core.components.config.components.QuoteThemeConfig;
import com.adobexp.aem.core.components.config.components.RatingThemeConfig;
import com.adobexp.aem.core.components.config.components.ServicesThemeConfig;
import com.adobexp.aem.core.components.config.components.SiteBannerThemeConfig;
import com.adobexp.aem.core.components.config.components.SubscriptionPlansThemeConfig;

/**
 * Sling Model that calculates a version hash based on Context-Aware Configuration values.
 * This hash is used for cache-busting the theme CSS URL.
 * 
 * When any CA configuration value changes, the hash changes, which generates a new URL
 * and bypasses the CDN cache automatically.
 * 
 * Compatible with both AEMaaCS and On-Premise AEM environments.
 * 
 * Usage in HTL:
 * {@code 
 *   <sly data-sly-use.themeVersion="com.adobexp.aem.core.components.internal.models.ThemeVersionModel"/>
 *   <link rel="stylesheet" href="${currentPage.path}.theme-variables.${themeVersion.version}.css" type="text/css">
 * }
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ThemeVersionModel {

    private static final Logger LOG = LoggerFactory.getLogger(ThemeVersionModel.class);
    private static final String DEFAULT_VERSION = "v1";

    @SlingObject
    private Resource resource;

    @OSGiService
    private ConfigurationResolver configurationResolver;

    private String version;

    @PostConstruct
    protected void init() {
        if (configurationResolver == null || resource == null) {
            LOG.debug("ConfigurationResolver or Resource is null, using default version");
            this.version = DEFAULT_VERSION;
            return;
        }

        try {
            ConfigurationBuilder configBuilder = configurationResolver.get(resource);
            StringBuilder configFingerprint = new StringBuilder();

            // Collect all configuration values into a fingerprint string
            appendGlobalConfig(configFingerprint, configBuilder.as(SiteThemeGlobalConfig.class));
            appendHeaderConfig(configFingerprint, configBuilder.as(HeaderThemeConfig.class));
            appendFooterConfig(configFingerprint, configBuilder.as(FooterThemeConfig.class));
            appendServicesConfig(configFingerprint, configBuilder.as(ServicesThemeConfig.class));
            appendButtonConfig(configFingerprint, configBuilder.as(ButtonThemeConfig.class));
            appendCtaPillConfig(configFingerprint, configBuilder.as(CtaPillThemeConfig.class));
            appendLeadBannerConfig(configFingerprint, configBuilder.as(LeadBannerThemeConfig.class));
            appendArticleTileConfig(configFingerprint, configBuilder.as(ArticleTileThemeConfig.class));
            appendHeaderOverlayConfig(configFingerprint, configBuilder.as(HeaderOverlayThemeConfig.class));
            appendSiteBannerConfig(configFingerprint, configBuilder.as(SiteBannerThemeConfig.class));
            appendQuoteConfig(configFingerprint, configBuilder.as(QuoteThemeConfig.class));
            appendLoopingCircleGalleryConfig(configFingerprint, configBuilder.as(LoopingCircleGalleryThemeConfig.class));
            appendCountUpConfig(configFingerprint, configBuilder.as(CountUpThemeConfig.class));
            appendSubscriptionPlansConfig(configFingerprint, configBuilder.as(SubscriptionPlansThemeConfig.class));
            appendCompareSubscriptionConfig(configFingerprint, configBuilder.as(CompareSubscriptionThemeConfig.class));
            appendRatingConfig(configFingerprint, configBuilder.as(RatingThemeConfig.class));
            appendMarqueeCarouselConfig(configFingerprint, configBuilder.as(MarqueeCarouselThemeConfig.class));
            appendMasonryGalleryConfig(configFingerprint, configBuilder.as(MasonryGalleryThemeConfig.class));
            appendComparisonConfig(configFingerprint, configBuilder.as(ComparisonThemeConfig.class));
            appendFaqConfig(configFingerprint, configBuilder.as(FaqThemeConfig.class));
            appendBlobImageSectionConfig(configFingerprint, configBuilder.as(BlobImageSectionThemeConfig.class));
            appendLeadMediaSectionConfig(configFingerprint, configBuilder.as(LeadMediaSectionThemeConfig.class));
            appendGridControlConfig(configFingerprint, configBuilder.as(GridControlThemeConfig.class));

            // Generate a short hash from the fingerprint
            this.version = generateShortHash(configFingerprint.toString());
            LOG.debug("Generated theme version hash: {}", this.version);

        } catch (Exception e) {
            LOG.warn("Error calculating theme version hash, using default", e);
            this.version = DEFAULT_VERSION;
        }
    }

    /**
     * Returns the calculated version hash for use in the theme CSS URL.
     * @return version hash string (8 characters)
     */
    public String getVersion() {
        return version;
    }

    /**
     * Generates a short hash from the input string using MD5.
     * Returns first 8 characters of the hex hash for URL-friendliness.
     */
    private String generateShortHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // Return first 8 characters for a shorter, URL-friendly version
            return hexString.substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("MD5 algorithm not available", e);
            return String.valueOf(Math.abs(input.hashCode()));
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private <T> void appendConfigValue(StringBuilder sb, T config, Function<T, String> getter) {
        if (config != null) {
            try {
                sb.append(nullSafe(getter.apply(config)));
            } catch (Exception e) {
                // Ignore - value will not be appended
            }
        }
    }

    // ==================== Configuration Append Methods ====================

    private void appendGlobalConfig(StringBuilder sb, SiteThemeGlobalConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkMainThemeColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightMainThemeColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkPrimaryTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightPrimaryTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkSecondaryTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightSecondaryTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkStandardPrimarySiteTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightStandardPrimarySiteTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkStandardSecondarySiteTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightStandardSecondarySiteTextColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::standardSiteFontSize);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::standardSiteFontWeight);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkBlockquoteBorderColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightBlockquoteBorderColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkBlockquoteBg);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightBlockquoteBg);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkBlockquoteQuoteColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightBlockquoteQuoteColor);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkSiteBodyBg);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightSiteBodyBg);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::darkSiteOverlayBg);
        appendConfigValue(sb, config, SiteThemeGlobalConfig::lightSiteOverlayBg);
    }

    private void appendHeaderConfig(StringBuilder sb, HeaderThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, HeaderThemeConfig::darkHeaderBackgroundColor);
        appendConfigValue(sb, config, HeaderThemeConfig::lightHeaderBackgroundColor);
        appendConfigValue(sb, config, HeaderThemeConfig::headerHeight);
    }

    private void appendFooterConfig(StringBuilder sb, FooterThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, FooterThemeConfig::darkFooterBg);
        appendConfigValue(sb, config, FooterThemeConfig::lightFooterBg);
        appendConfigValue(sb, config, FooterThemeConfig::footerCurtainHeightOffset);
    }

    private void appendServicesConfig(StringBuilder sb, ServicesThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, ServicesThemeConfig::darkServicesDividerColor);
        appendConfigValue(sb, config, ServicesThemeConfig::lightServicesDividerColor);
        appendConfigValue(sb, config, ServicesThemeConfig::darkServicesBg);
        appendConfigValue(sb, config, ServicesThemeConfig::lightServicesBg);
    }

    private void appendButtonConfig(StringBuilder sb, ButtonThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, ButtonThemeConfig::darkButtonBg);
        appendConfigValue(sb, config, ButtonThemeConfig::darkButtonText);
        appendConfigValue(sb, config, ButtonThemeConfig::darkButtonBorder);
        appendConfigValue(sb, config, ButtonThemeConfig::darkButtonHoverBg);
        appendConfigValue(sb, config, ButtonThemeConfig::darkButtonHoverText);
        appendConfigValue(sb, config, ButtonThemeConfig::lightButtonBg);
        appendConfigValue(sb, config, ButtonThemeConfig::lightButtonText);
        appendConfigValue(sb, config, ButtonThemeConfig::lightButtonBorder);
        appendConfigValue(sb, config, ButtonThemeConfig::lightButtonHoverBg);
        appendConfigValue(sb, config, ButtonThemeConfig::lightButtonHoverText);
    }

    private void appendCtaPillConfig(StringBuilder sb, CtaPillThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, CtaPillThemeConfig::darkCtaPillBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::darkCtaPillText);
        appendConfigValue(sb, config, CtaPillThemeConfig::darkCtaPillHoverBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::darkCtaPillIconBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::darkCtaPillIconColor);
        appendConfigValue(sb, config, CtaPillThemeConfig::lightCtaPillBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::lightCtaPillText);
        appendConfigValue(sb, config, CtaPillThemeConfig::lightCtaPillHoverBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::lightCtaPillIconBg);
        appendConfigValue(sb, config, CtaPillThemeConfig::lightCtaPillIconColor);
    }

    private void appendLeadBannerConfig(StringBuilder sb, LeadBannerThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, LeadBannerThemeConfig::leadBannerHeight);
        appendConfigValue(sb, config, LeadBannerThemeConfig::leadBannerHeightMobile);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerGradientStart);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerGradientStop25);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerGradientStop50);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerGradientStop75);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerGradientEnd);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerTextPrimary);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerTextSecondary);
        appendConfigValue(sb, config, LeadBannerThemeConfig::darkLeadBannerSecondaryTextColor);
        appendConfigValue(sb, config, LeadBannerThemeConfig::leadBannerCharFadeDuration);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerGradientStart);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerGradientStop25);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerGradientStop50);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerGradientStop75);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerGradientEnd);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerTextPrimary);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerTextSecondary);
        appendConfigValue(sb, config, LeadBannerThemeConfig::lightLeadBannerSecondaryTextColor);
    }

    private void appendArticleTileConfig(StringBuilder sb, ArticleTileThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, ArticleTileThemeConfig::darkArticleTileOverlayBg);
        appendConfigValue(sb, config, ArticleTileThemeConfig::lightArticleTileOverlayBg);
    }

    private void appendHeaderOverlayConfig(StringBuilder sb, HeaderOverlayThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, HeaderOverlayThemeConfig::darkHeaderOverlayColumnDividerColor);
        appendConfigValue(sb, config, HeaderOverlayThemeConfig::darkHeaderOverlayHoverBg);
        appendConfigValue(sb, config, HeaderOverlayThemeConfig::lightHeaderOverlayColumnDividerColor);
        appendConfigValue(sb, config, HeaderOverlayThemeConfig::lightHeaderOverlayHoverBg);
    }

    private void appendSiteBannerConfig(StringBuilder sb, SiteBannerThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, SiteBannerThemeConfig::darkSiteBannerBg);
        appendConfigValue(sb, config, SiteBannerThemeConfig::darkSiteBannerMarqueeDuration);
        appendConfigValue(sb, config, SiteBannerThemeConfig::darkSiteBannerCycleDuration);
        appendConfigValue(sb, config, SiteBannerThemeConfig::siteBannerFontSize);
        appendConfigValue(sb, config, SiteBannerThemeConfig::lightSiteBannerMarqueeDuration);
        appendConfigValue(sb, config, SiteBannerThemeConfig::lightSiteBannerCycleDuration);
    }

    private void appendQuoteConfig(StringBuilder sb, QuoteThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, QuoteThemeConfig::darkQuoteBg);
        appendConfigValue(sb, config, QuoteThemeConfig::darkQuoteCardGlow);
        appendConfigValue(sb, config, QuoteThemeConfig::lightQuoteCardGlow);
    }

    private void appendLoopingCircleGalleryConfig(StringBuilder sb, LoopingCircleGalleryThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, LoopingCircleGalleryThemeConfig::darkLoopingCircleGalleryOverlayBg);
        appendConfigValue(sb, config, LoopingCircleGalleryThemeConfig::darkLoopingCircleGalleryOverlayText);
        appendConfigValue(sb, config, LoopingCircleGalleryThemeConfig::lightLoopingCircleGalleryOverlayBg);
    }

    private void appendCountUpConfig(StringBuilder sb, CountUpThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, CountUpThemeConfig::darkCountUpBg);
        appendConfigValue(sb, config, CountUpThemeConfig::darkCountUpCardBg);
        appendConfigValue(sb, config, CountUpThemeConfig::lightCountUpBg);
        appendConfigValue(sb, config, CountUpThemeConfig::lightCountUpCardBg);
    }

    private void appendSubscriptionPlansConfig(StringBuilder sb, SubscriptionPlansThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansCardBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansCardBorder);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansToggleBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansToggleActiveText);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansSaveBadgeBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansSaveBadgeText);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansDivider);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansFeatureCheck);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansCtaPrimaryBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::darkSubscriptionPlansCtaPrimaryHover);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansCardBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansCardBorder);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansToggleBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansToggleActiveBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansToggleActiveText);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansSaveBadgeBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansSaveBadgeText);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansPriceColor);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansHighlightBorder);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansDivider);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansCreditsIcon);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansFeatureCheck);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansCtaPrimaryBg);
        appendConfigValue(sb, config, SubscriptionPlansThemeConfig::lightSubscriptionPlansCtaPrimaryHover);
    }

    private void appendCompareSubscriptionConfig(StringBuilder sb, CompareSubscriptionThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionTableBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionBorder);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionFeaturesBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionRowAltBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionSectionBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionHighlightBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionCheckColor);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionCrossColor);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionCtaPrimaryBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::darkCompareSubscriptionCtaPrimaryHover);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionTableBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionBorder);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionFeaturesBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionRowAltBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionSectionBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionHighlightBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionHighlightAccent);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionPriceColor);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionCheckColor);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionCrossColor);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionCtaPrimaryBg);
        appendConfigValue(sb, config, CompareSubscriptionThemeConfig::lightCompareSubscriptionCtaPrimaryHover);
    }

    private void appendRatingConfig(StringBuilder sb, RatingThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingBg);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingAvatarBorder);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingAvatarBg);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingStarColor);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingStarEmptyColor);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingCtaBg);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingCtaText);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingCtaHoverBg);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingCtaIconBg);
        appendConfigValue(sb, config, RatingThemeConfig::darkRatingCtaIconColor);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingBg);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingAvatarBorder);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingAvatarBg);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingStarColor);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingStarEmptyColor);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingCtaBg);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingCtaText);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingCtaHoverBg);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingCtaIconBg);
        appendConfigValue(sb, config, RatingThemeConfig::lightRatingCtaIconColor);
    }

    private void appendMarqueeCarouselConfig(StringBuilder sb, MarqueeCarouselThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCardBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCtaBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCtaText);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCtaHoverBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCtaIconBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::darkMarqueeCarouselCtaIconColor);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::marqueeCarouselDuration);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselFadeColor);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCardBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCtaBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCtaText);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCtaHoverBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCtaIconBg);
        appendConfigValue(sb, config, MarqueeCarouselThemeConfig::lightMarqueeCarouselCtaIconColor);
    }

    private void appendMasonryGalleryConfig(StringBuilder sb, MasonryGalleryThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::darkMasonryGalleryBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::darkMasonryGalleryItemBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::darkMasonryGalleryLightboxBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::darkMasonryGalleryLightboxTitleColor);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::darkMasonryGalleryLightboxTitleBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::lightMasonryGalleryBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::lightMasonryGalleryItemBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::lightMasonryGalleryLightboxBg);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::lightMasonryGalleryLightboxTitleColor);
        appendConfigValue(sb, config, MasonryGalleryThemeConfig::lightMasonryGalleryLightboxTitleBg);
    }

    private void appendComparisonConfig(StringBuilder sb, ComparisonThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, ComparisonThemeConfig::darkComparisonBg);
        appendConfigValue(sb, config, ComparisonThemeConfig::darkComparisonColBg);
        appendConfigValue(sb, config, ComparisonThemeConfig::lightComparisonBg);
        appendConfigValue(sb, config, ComparisonThemeConfig::lightComparisonColBg);
    }

    private void appendFaqConfig(StringBuilder sb, FaqThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, FaqThemeConfig::darkFaqBg);
        appendConfigValue(sb, config, FaqThemeConfig::darkFaqItemBg);
        appendConfigValue(sb, config, FaqThemeConfig::darkFaqItemHoverBg);
        appendConfigValue(sb, config, FaqThemeConfig::lightFaqBg);
        appendConfigValue(sb, config, FaqThemeConfig::lightFaqItemBg);
        appendConfigValue(sb, config, FaqThemeConfig::lightFaqItemHoverBg);
    }

    private void appendBlobImageSectionConfig(StringBuilder sb, BlobImageSectionThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionCardBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionBadgeBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionBadgeBorder);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionIconBadgeBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionIconBadgeColor);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::darkBlobImageSectionOverlayCardBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionCardBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionBadgeBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionBadgeBorder);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionBadgeText);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionIconBadgeBg);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionIconBadgeColor);
        appendConfigValue(sb, config, BlobImageSectionThemeConfig::lightBlobImageSectionOverlayCardBg);
    }

    private void appendLeadMediaSectionConfig(StringBuilder sb, LeadMediaSectionThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCardBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionIconBadgeBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionIconBadgeColor);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCtaBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCtaText);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCtaHoverBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCtaIconBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionCtaIconColor);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionMediaBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::darkLeadMediaSectionMediaBorder);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCardBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionIconBadgeBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionIconBadgeColor);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCtaBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCtaText);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCtaHoverBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCtaIconBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionCtaIconColor);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionMediaBg);
        appendConfigValue(sb, config, LeadMediaSectionThemeConfig::lightLeadMediaSectionMediaBorder);
    }

    private void appendGridControlConfig(StringBuilder sb, GridControlThemeConfig config) {
        if (config == null) return;
        appendConfigValue(sb, config, GridControlThemeConfig::darkGridControlBg);
        appendConfigValue(sb, config, GridControlThemeConfig::darkGridControlColumnBg);
        appendConfigValue(sb, config, GridControlThemeConfig::lightGridControlBg);
        appendConfigValue(sb, config, GridControlThemeConfig::lightGridControlColumnBg);
    }
}

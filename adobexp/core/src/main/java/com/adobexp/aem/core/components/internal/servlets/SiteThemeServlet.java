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
package com.adobexp.aem.core.components.internal.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

import com.adobexp.aem.core.components.config.SiteThemeGlobalConfig;
import com.adobexp.aem.core.components.config.components.AnalyticsChartThemeConfig;
import com.adobexp.aem.core.components.config.components.ArticleTileThemeConfig;
import com.adobexp.aem.core.components.config.components.BlobImageSectionThemeConfig;
import com.adobexp.aem.core.components.config.components.ButtonThemeConfig;
import com.adobexp.aem.core.components.config.components.CallToActionThemeConfig;
import com.adobexp.aem.core.components.config.components.CardsThemeConfig;
import com.adobexp.aem.core.components.config.components.CodeSnippetThemeConfig;
import com.adobexp.aem.core.components.config.components.DataTableThemeConfig;
import com.adobexp.aem.core.components.config.components.FlowDiagramThemeConfig;
import com.adobexp.aem.core.components.config.components.MetricTilesThemeConfig;
import com.adobexp.aem.core.components.config.components.ScreenshotShowcaseThemeConfig;
import com.adobexp.aem.core.components.config.components.StepsTimelineThemeConfig;
import com.adobexp.aem.core.components.config.components.CompareSubscriptionThemeConfig;
import com.adobexp.aem.core.components.config.components.ComparisonThemeConfig;
import com.adobexp.aem.core.components.config.components.CountUpThemeConfig;
import com.adobexp.aem.core.components.config.components.CtaPillThemeConfig;
import com.adobexp.aem.core.components.config.components.FaqThemeConfig;
import com.adobexp.aem.core.components.config.components.FooterThemeConfig;
import com.adobexp.aem.core.components.config.components.HeroThemeConfig;
import com.adobexp.aem.core.components.config.components.FormBuilderThemeConfig;
import com.adobexp.aem.core.components.config.components.GridControlThemeConfig;
import com.adobexp.aem.core.components.config.components.VideoArticleGridThemeConfig;
import com.adobexp.aem.core.components.config.components.HeaderOverlayThemeConfig;
import com.adobexp.aem.core.components.config.components.HeaderThemeConfig;
import com.adobexp.aem.core.components.config.components.LeadBannerThemeConfig;
import com.adobexp.aem.core.components.config.components.LeadCarouselThemeConfig;
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
 * Servlet that generates CSS containing theme variables from Context-Aware Configuration.
 * 
 * This servlet is bound to page resource types and responds to the "theme-variables" selector.
 * It supports cache-busting via an optional version selector appended after "theme-variables".
 * 
 * Compatible with both AEMaaCS and On-Premise AEM environments.
 * 
 * Usage in HTL (with cache-busting version):
 * {@code 
 *   <sly data-sly-use.themeVersion="com.adobexp.aem.core.components.internal.models.ThemeVersionModel"/>
 *   <link rel="stylesheet" href="${currentPage.path}.theme-variables.${themeVersion.version}.css" type="text/css">
 * }
 * 
 * This generates URLs like: /content/mysite/page.theme-variables.a1b2c3d4.css
 * When the CA configuration changes, the version hash changes, generating a new URL
 * that bypasses CDN cache automatically.
 * 
 * Legacy usage (without version - not recommended as CDN will cache indefinitely):
 * {@code <link rel="stylesheet" href="${currentPage.path}.theme-variables.css" type="text/css">}
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = {
                "adobexp/components/global/pages/page/v1/page",
                "adobexp/components/global/pages/rootpage/v1/rootpage",
                "cq/experience-fragments/components/xfpage",
                // Tenant Admin / DAM Platform pages that host AdobeXP components
                "dam-platform/components/structure/page",
                "dam-platform/components/structure/xfpage"
        },
        methods = HttpConstants.METHOD_GET,
        selectors = "theme-variables",
        extensions = "css"
)
public class SiteThemeServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SiteThemeServlet.class);
    private static final String CONTENT_TYPE_CSS = "text/css;charset=UTF-8";

    /** Number of --analytics-chart-cat-N slots the chart client library looks for. */
    private static final int CATEGORY_COLOR_SLOTS = 8;
    private static final String DARK_CATEGORY_COLORS =
            "#f4c15e, #5b9dff, #4ecdc4, #f2789f, #a78bfa, #ff9f5a, #7ddf7d, #d9d24f";
    private static final String LIGHT_CATEGORY_COLORS =
            "#b8860b, #2f6bd8, #12897d, #c2456f, #6d4bd8, #cf5f18, #2f8f3f, #86811c";

    @Reference
    private transient ConfigurationResolver configurationResolver;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws IOException {
        
        response.setContentType(CONTENT_TYPE_CSS);
        response.setCharacterEncoding("UTF-8");
        
        // Extract version from selectors for ETag (e.g., "theme-variables.a1b2c3d4" -> "a1b2c3d4")
        String[] selectors = request.getRequestPathInfo().getSelectors();
        String version = extractVersionFromSelectors(selectors);
        
        // Set cache headers optimized for versioned URLs
        // When version is present, use immutable caching (1 year) since URL changes on config update
        // When version is absent (legacy usage), use shorter cache with must-revalidate
        if (version != null && !version.isEmpty()) {
            // Versioned URL: aggressive caching is safe because URL changes when config changes
            // 1 year max-age with immutable hint for CDN and browser caching
            response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            response.setHeader("ETag", "\"" + version + "\"");
            LOG.debug("Serving theme CSS with version: {}", version);
        } else {
            // Legacy URL without version: shorter cache, require revalidation
            response.setHeader("Cache-Control", "public, max-age=300, must-revalidate");
            LOG.debug("Serving theme CSS without version (legacy mode)");
        }
        
        Resource resource = request.getResource();
        ThemeConfigs configs = resolveConfigs(resource);

        PrintWriter writer = response.getWriter();
        generateThemeCss(writer, configs);
    }

    /**
     * Extracts the version hash from request selectors.
     * Expects selectors like ["theme-variables", "a1b2c3d4"] and returns "a1b2c3d4".
     * 
     * @param selectors array of request selectors
     * @return version string if present, null otherwise
     */
    private String extractVersionFromSelectors(String[] selectors) {
        if (selectors == null || selectors.length < 2) {
            return null;
        }
        // The version selector comes after "theme-variables"
        for (int i = 0; i < selectors.length - 1; i++) {
            if ("theme-variables".equals(selectors[i])) {
                return selectors[i + 1];
            }
        }
        return null;
    }

    private ThemeConfigs resolveConfigs(Resource resource) {
        if (configurationResolver == null || resource == null) {
            return ThemeConfigs.empty();
        }

        ConfigurationBuilder configBuilder = configurationResolver.get(resource);
        return new ThemeConfigs(
                configBuilder.as(SiteThemeGlobalConfig.class),
                configBuilder.as(HeaderThemeConfig.class),
                configBuilder.as(FooterThemeConfig.class),
                configBuilder.as(ServicesThemeConfig.class),
                configBuilder.as(ButtonThemeConfig.class),
                configBuilder.as(CtaPillThemeConfig.class),
                configBuilder.as(LeadBannerThemeConfig.class),
                configBuilder.as(ArticleTileThemeConfig.class),
                configBuilder.as(HeaderOverlayThemeConfig.class),
                configBuilder.as(SiteBannerThemeConfig.class),
                configBuilder.as(QuoteThemeConfig.class),
                configBuilder.as(LoopingCircleGalleryThemeConfig.class),
                configBuilder.as(CountUpThemeConfig.class),
                configBuilder.as(SubscriptionPlansThemeConfig.class),
                configBuilder.as(CompareSubscriptionThemeConfig.class),
                configBuilder.as(RatingThemeConfig.class),
                configBuilder.as(MarqueeCarouselThemeConfig.class),
                configBuilder.as(MasonryGalleryThemeConfig.class),
                configBuilder.as(ComparisonThemeConfig.class),
                configBuilder.as(FaqThemeConfig.class),
                configBuilder.as(HeroThemeConfig.class),
                configBuilder.as(BlobImageSectionThemeConfig.class),
                configBuilder.as(LeadMediaSectionThemeConfig.class),
                configBuilder.as(GridControlThemeConfig.class),
                configBuilder.as(FormBuilderThemeConfig.class),
                configBuilder.as(VideoArticleGridThemeConfig.class),
                configBuilder.as(CardsThemeConfig.class),
                configBuilder.as(CallToActionThemeConfig.class),
                configBuilder.as(DataTableThemeConfig.class),
                configBuilder.as(AnalyticsChartThemeConfig.class),
                configBuilder.as(MetricTilesThemeConfig.class),
                configBuilder.as(CodeSnippetThemeConfig.class),
                configBuilder.as(StepsTimelineThemeConfig.class),
                configBuilder.as(FlowDiagramThemeConfig.class),
                configBuilder.as(ScreenshotShowcaseThemeConfig.class),
                configBuilder.as(LeadCarouselThemeConfig.class)
        );
    }

    private void generateThemeCss(PrintWriter writer, ThemeConfigs configs) {
        // Dark Theme
        writer.println(".theme-dark {");
        writer.println("  /* Main theme color */");
        writer.println("  --main-theme-color: " + getOrDefault(configs.globalConfig, c -> c.darkMainThemeColor(), "#e3a002") + ";");
        writer.println();
        writer.println("  /* Header theme variables */");
        writer.println("  --header-background-color: " + getOrDefault(configs.headerConfig, c -> c.darkHeaderBackgroundColor(), "#212020") + ";");
        writer.println("  --header-height: " + getOrDefault(configs.headerConfig, c -> c.headerHeight(), "60px") + ";");
        writer.println();
        writer.println("  /* Text color variables - Dark theme */");
        writer.println("  --primary-text-color: " + getOrDefault(configs.globalConfig, c -> c.darkPrimaryTextColor(), "#ffc846") + ";");
        writer.println("  --secondary-text-color: " + getOrDefault(configs.globalConfig, c -> c.darkSecondaryTextColor(), "#ffedc2") + ";");
        writer.println("  --standard-primary-site-text-color: " + getOrDefault(configs.globalConfig, c -> c.darkStandardPrimarySiteTextColor(), "#ffffff") + ";");
        writer.println("  --standard-secondary-site-text-color: " + getOrDefault(configs.globalConfig, c -> c.darkStandardSecondarySiteTextColor(), "#a2a2a2") + ";");
        writer.println("  --standard-site-font-size: " + getOrDefault(configs.globalConfig, c -> c.standardSiteFontSize(), "16px") + ";");
        writer.println("  --standard-site-font-weight: " + getOrDefault(configs.globalConfig, c -> c.standardSiteFontWeight(), "400") + ";");
        writer.println("  --site-font-family: " + getOrDefault(configs.globalConfig, c -> c.siteFontFamily(), "\"Hubot Sans\", \"Framer Display\", \"Instrument Serif\", \"Segoe UI\", Arial, sans-serif") + ";");
        writer.println("  --site-heading-font-family: " + getOrDefault(configs.globalConfig, c -> c.headingFontFamily(), "\"Hubot Sans\", \"Framer Display\", \"Instrument Serif\", \"Segoe UI\", Arial, sans-serif") + ";");
        writer.println("  --blockquote-border-color: " + getOrDefault(configs.globalConfig, c -> c.darkBlockquoteBorderColor(), "var(--primary-text-color)") + ";");
        writer.println("  --blockquote-bg: " + getOrDefault(configs.globalConfig, c -> c.darkBlockquoteBg(), "#2f2f2f") + ";");
        writer.println("  --blockquote-quote-color: " + getOrDefault(configs.globalConfig, c -> c.darkBlockquoteQuoteColor(), "var(--primary-text-color)") + ";");
        writer.println();
        writer.println("  /* Global page */");
        writer.println("  --site-body-bg: " + getOrDefault(configs.globalConfig, c -> c.darkSiteBodyBg(), "#1e1e1e") + ";");
        writer.println("  --site-body-text: var(--standard-primary-site-text-color);");
        writer.println("  --site-overlay-bg: " + getOrDefault(configs.globalConfig, c -> c.darkSiteOverlayBg(), "rgba(0, 0, 0, 0.466)") + ";");
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(configs.footerConfig, c -> c.darkFooterBg(), "#363535") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(configs.footerConfig, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(configs.servicesConfig, c -> c.darkServicesDividerColor(), "rgba(255, 255, 255, 0.12)") + ";");
        writer.println("  --services-bg: " + getOrDefault(configs.servicesConfig, c -> c.darkServicesBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Dark theme */");
        writer.println("  --button-theme-dark-bg: " + getOrDefault(configs.buttonConfig, c -> c.darkButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-dark-text: " + getOrDefault(configs.buttonConfig, c -> c.darkButtonText(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-border: " + getOrDefault(configs.buttonConfig, c -> c.darkButtonBorder(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-bg: " + getOrDefault(configs.buttonConfig, c -> c.darkButtonHoverBg(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-text: " + getOrDefault(configs.buttonConfig, c -> c.darkButtonHoverText(), "#000000") + ";");
        writer.println();
        writer.println("  /* CTA Pill Button - Dark theme */");
        writer.println("  --cta-pill-dark-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.darkCtaPillBg(), "#ffffff") + ";");
        writer.println("  --cta-pill-dark-text: " + getOrDefault(configs.ctaPillConfig, c -> c.darkCtaPillText(), "#000000") + ";");
        writer.println("  --cta-pill-dark-hover-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.darkCtaPillHoverBg(), "#e0e0e0") + ";");
        writer.println("  --cta-pill-dark-icon-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.darkCtaPillIconBg(), "#000000") + ";");
        writer.println("  --cta-pill-dark-icon-color: " + getOrDefault(configs.ctaPillConfig, c -> c.darkCtaPillIconColor(), "#ffffff") + ";");
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
        writer.println("  --lead-banner-height: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerGradientStart(), "#212020") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerGradientStop25(), "#aa7802") + ";");
        writer.println("  --lead-banner-gradient-stop-50: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerGradientStop50(), "#e3a002") + ";");
        writer.println("  --lead-banner-gradient-stop-75: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerGradientStop75(), "#aa7802") + ";");
        writer.println("  --lead-banner-gradient-end: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerGradientEnd(), "#212020") + ";");
        writer.println("  --lead-banner-text-primary: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerTextPrimary(), "#ffffff") + ";");
        writer.println("  --lead-banner-text-secondary: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerTextSecondary(), "#242424") + ";");
        writer.println("  --lead-banner-secondary-text-color: " + getOrDefault(configs.leadBannerConfig, c -> c.darkLeadBannerSecondaryTextColor(), "#fffffa") + ";");
        writer.println("  --lead-banner-char-fade-duration: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerCharFadeDuration(), "0.3s") + ";");
        writer.println();
        writer.println("  /* Article tiles (Header overlay) */");
        writer.println("  --article-tile-overlay-bg: " + getOrDefault(configs.articleTileConfig, c -> c.darkArticleTileOverlayBg(), "rgba(0, 0, 0, 0.8)") + ";");
        writer.println();
        writer.println("  /* Header overlay */");
        writer.println("  --header-overlay-column-divider-color: " + getOrDefault(configs.headerOverlayConfig, c -> c.darkHeaderOverlayColumnDividerColor(), "rgba(255, 255, 255, 0.12)") + ";");
        writer.println("  --header-overlay-hover-bg: " + getOrDefault(configs.headerOverlayConfig, c -> c.darkHeaderOverlayHoverBg(), "rgba(255, 255, 255, 0.10)") + ";");
        writer.println();
        writer.println("  /* Site banner */");
        writer.println("  --site-banner-bg: " + getOrDefault(configs.siteBannerConfig, c -> c.darkSiteBannerBg(), "#363535") + ";");
        writer.println("  --site-banner-text-color: var(--primary-text-color);");
        writer.println("  --site-banner-marquee-duration: " + getOrDefault(configs.siteBannerConfig, c -> c.darkSiteBannerMarqueeDuration(), "5s") + ";");
        writer.println("  --site-banner-cycle-duration: " + getOrDefault(configs.siteBannerConfig, c -> c.darkSiteBannerCycleDuration(), "10s") + ";");
        writer.println("  --site-banner-font-size: " + getOrDefault(configs.siteBannerConfig, c -> c.siteBannerFontSize(), "20px") + ";");
        writer.println();
        writer.println("  /* Quote */");
        writer.println("  --quote-bg: " + getOrDefault(configs.quoteConfig, c -> c.darkQuoteBg(), "#363535") + ";");
        writer.println("  --quote-card-glow: " + getOrDefault(configs.quoteConfig, c -> c.darkQuoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(configs.loopingCircleGalleryConfig, c -> c.darkLoopingCircleGalleryOverlayBg(), "rgba(255, 255, 255, 0.5)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: " + getOrDefault(configs.loopingCircleGalleryConfig, c -> c.darkLoopingCircleGalleryOverlayText(), "#000000") + ";");
        writer.println();
        writer.println("  /* CountUp */");
        writer.println("  --count-up-bg: " + getOrDefault(configs.countUpConfig, c -> c.darkCountUpBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --count-up-card-bg: " + getOrDefault(configs.countUpConfig, c -> c.darkCountUpCardBg(), "#2a2a2a") + ";");
        writer.println();
        writer.println("  /* SubscriptionPlans */");
        writer.println("  --subscription-plans-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --subscription-plans-card-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansCardBg(), "#2a2a2a") + ";");
        writer.println("  --subscription-plans-card-border: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansCardBorder(), "#404040") + ";");
        writer.println("  --subscription-plans-toggle-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansToggleBg(), "#374151") + ";");
        writer.println("  --subscription-plans-toggle-active-bg: var(--primary-text-color);");
        writer.println("  --subscription-plans-toggle-active-text: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansToggleActiveText(), "#ffffff") + ";");
        writer.println("  --subscription-plans-save-badge-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansSaveBadgeBg(), "rgba(34, 197, 94, 0.2)") + ";");
        writer.println("  --subscription-plans-save-badge-text: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansSaveBadgeText(), "#4ade80") + ";");
        writer.println("  --subscription-plans-price-color: var(--primary-text-color);");
        writer.println("  --subscription-plans-highlight-border: var(--primary-text-color);");
        writer.println("  --subscription-plans-divider: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansDivider(), "#404040") + ";");
        writer.println("  --subscription-plans-credits-icon: var(--primary-text-color);");
        writer.println("  --subscription-plans-feature-check: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansFeatureCheck(), "#4ade80") + ";");
        writer.println("  --subscription-plans-cta-primary-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansCtaPrimaryBg(), "#02c36f") + ";");
        writer.println("  --subscription-plans-cta-primary-hover: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.darkSubscriptionPlansCtaPrimaryHover(), "#02a25d") + ";");
        writer.println();
        writer.println("  /* CompareSubscription */");
        writer.println("  --compare-subscription-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --compare-subscription-table-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionTableBg(), "#2a2a2a") + ";");
        writer.println("  --compare-subscription-border: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionBorder(), "#404040") + ";");
        writer.println("  --compare-subscription-features-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionFeaturesBg(), "#1f1f1f") + ";");
        writer.println("  --compare-subscription-row-alt-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionRowAltBg(), "rgba(255, 255, 255, 0.02)") + ";");
        writer.println("  --compare-subscription-section-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionSectionBg(), "#262626") + ";");
        writer.println("  --compare-subscription-highlight-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionHighlightBg(), "rgba(255, 200, 70, 0.08)") + ";");
        writer.println("  --compare-subscription-highlight-accent: var(--primary-text-color);");
        writer.println("  --compare-subscription-price-color: var(--primary-text-color);");
        writer.println("  --compare-subscription-check-color: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionCheckColor(), "#4ade80") + ";");
        writer.println("  --compare-subscription-cross-color: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionCrossColor(), "#ef4444") + ";");
        writer.println("  --compare-subscription-cta-primary-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionCtaPrimaryBg(), "#02c36f") + ";");
        writer.println("  --compare-subscription-cta-primary-hover: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.darkCompareSubscriptionCtaPrimaryHover(), "#02a25d") + ";");
        writer.println();
        writer.println("  /* Rating */");
        writer.println("  --rating-bg: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --rating-avatar-border: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingAvatarBorder(), "#2a2a2a") + ";");
        writer.println("  --rating-avatar-bg: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingAvatarBg(), "#404040") + ";");
        writer.println("  --rating-star-color: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingStarColor(), "#fbbf24") + ";");
        writer.println("  --rating-star-empty-color: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingStarEmptyColor(), "#525252") + ";");
        writer.println("  --rating-cta-bg: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingCtaBg(), "#ffffff") + ";");
        writer.println("  --rating-cta-text: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingCtaText(), "#000000") + ";");
        writer.println("  --rating-cta-hover-bg: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --rating-cta-icon-bg: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingCtaIconBg(), "#000000") + ";");
        writer.println("  --rating-cta-icon-color: " + getOrDefault(configs.ratingConfig, c -> c.darkRatingCtaIconColor(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* MarqueeCarousel */");
        writer.println("  --marquee-carousel-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --marquee-carousel-fade-color: var(--site-body-bg);");
        writer.println("  --marquee-carousel-card-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCardBg(), "#404040") + ";");
        writer.println("  --marquee-carousel-cta-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCtaBg(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-text: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCtaText(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-hover-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --marquee-carousel-cta-icon-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCtaIconBg(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-icon-color: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.darkMarqueeCarouselCtaIconColor(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-duration: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.marqueeCarouselDuration(), "35s") + ";");
        writer.println();
        writer.println("  /* MasonryGallery */");
        writer.println("  --masonry-gallery-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.darkMasonryGalleryBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --masonry-gallery-item-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.darkMasonryGalleryItemBg(), "rgba(255, 255, 255, 0.05)") + ";");
        writer.println("  --masonry-gallery-lightbox-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.darkMasonryGalleryLightboxBg(), "rgba(10, 10, 10, 0.95)") + ";");
        writer.println("  --masonry-gallery-lightbox-title-color: " + getOrDefault(configs.masonryGalleryConfig, c -> c.darkMasonryGalleryLightboxTitleColor(), "#ffffff") + ";");
        writer.println("  --masonry-gallery-lightbox-title-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.darkMasonryGalleryLightboxTitleBg(), "rgba(0, 0, 0, 0.7)") + ";");
        writer.println();
        writer.println("  /* Comparison */");
        writer.println("  --comparison-bg: " + getOrDefault(configs.comparisonConfig, c -> c.darkComparisonBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --comparison-col-bg: " + getOrDefault(configs.comparisonConfig, c -> c.darkComparisonColBg(), "#2a2a2a") + ";");
        writer.println();
        writer.println("  /* FAQ */");
        writer.println("  --faq-bg: " + getOrDefault(configs.faqConfig, c -> c.darkFaqBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --faq-item-bg: " + getOrDefault(configs.faqConfig, c -> c.darkFaqItemBg(), "#2a2a2a") + ";");
        writer.println("  --faq-item-hover-bg: " + getOrDefault(configs.faqConfig, c -> c.darkFaqItemHoverBg(), "#333333") + ";");
        writer.println();
        writer.println("  /* Cards */");
        writer.println("  --cards-bg: " + getOrDefault(configs.cardsConfig, c -> c.darkCardsBg(), "var(--site-body-bg, #1e1e1e)") + ";");
        writer.println("  --cards-muted-bg: " + getOrDefault(configs.cardsConfig, c -> c.darkCardsMutedBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --cards-item-bg: " + getOrDefault(configs.cardsConfig, c -> c.darkCardsItemBg(), "#2a2a2a") + ";");
        writer.println("  --cards-item-border: " + getOrDefault(configs.cardsConfig, c -> c.darkCardsItemBorder(), "rgba(255, 255, 255, 0.08)") + ";");
        writer.println("  --cards-item-icon-bg: " + getOrDefault(configs.cardsConfig, c -> c.darkCardsItemIconBg(), "rgba(255, 255, 255, 0.06)") + ";");
        writer.println();
        writer.println("  /* Call to Action */");
        writer.println("  --call-to-action-bg: " + getOrDefault(configs.callToActionConfig, c -> c.darkCallToActionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Data Table */");
        writer.println("  --data-table-bg: " + getOrDefault(configs.dataTableConfig, c -> c.darkDataTableBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --data-table-header-bg: " + getOrDefault(configs.dataTableConfig, c -> c.darkDataTableHeaderBg(), "rgba(255, 255, 255, 0.06)") + ";");
        writer.println("  --data-table-cell-border: " + getOrDefault(configs.dataTableConfig, c -> c.darkDataTableCellBorder(), "rgba(255, 255, 255, 0.1)") + ";");
        writer.println("  --data-table-row-hover-bg: " + getOrDefault(configs.dataTableConfig, c -> c.darkDataTableRowHoverBg(), "rgba(255, 255, 255, 0.04)") + ";");
        writer.println();
        writer.println("  /* Hero */");
        writer.println("  --hero-bg: " + getOrDefault(configs.heroConfig, c -> c.darkHeroBg(), "linear-gradient(135deg, var(--lead-banner-gradient-start, #212020) 0%, var(--lead-banner-gradient-stop-25, #aa7802) 50%, var(--lead-banner-gradient-end, #212020) 100%)") + ";");
        writer.println();
        writeLeadCarouselTheme(writer, configs.leadCarouselConfig, true);
        writer.println();
        writer.println("  /* BlobImageSection */");
        writer.println("  --blob-image-section-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --blob-image-section-card-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionCardBg(), "#2a2a2a") + ";");
        writer.println("  --blob-image-section-badge-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionBadgeBg(), "#363636") + ";");
        writer.println("  --blob-image-section-badge-border: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionBadgeBorder(), "#404040") + ";");
        writer.println("  --blob-image-section-badge-text: var(--primary-text-color);");
        writer.println("  --blob-image-section-icon-badge-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionIconBadgeBg(), "#8b5cf6") + ";");
        writer.println("  --blob-image-section-icon-badge-color: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --blob-image-section-overlay-card-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.darkBlobImageSectionOverlayCardBg(), "#363636") + ";");
        writer.println();
        writer.println("  /* LeadMediaSection */");
        writer.println("  --lead-media-section-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --lead-media-section-card-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCardBg(), "#2a2a2a") + ";");
        writer.println("  --lead-media-section-icon-badge-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionIconBadgeBg(), "#3a3a3a") + ";");
        writer.println("  --lead-media-section-icon-badge-color: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCtaBg(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-text: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCtaText(), "#000000") + ";");
        writer.println("  --lead-media-section-cta-hover-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCtaHoverBg(), "#e0e0e0") + ";");
        writer.println("  --lead-media-section-cta-icon-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCtaIconBg(), "#3b82f6") + ";");
        writer.println("  --lead-media-section-cta-icon-color: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionCtaIconColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-media-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionMediaBg(), "#1a1a1a") + ";");
        writer.println("  --lead-media-section-media-border: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.darkLeadMediaSectionMediaBorder(), "rgba(180, 180, 180, 0.4)") + ";");
        writer.println();
        writer.println("  /* GridControl */");
        writer.println("  --grid-control-bg: " + getOrDefault(configs.gridControlConfig, c -> c.darkGridControlBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --grid-control-column-bg: " + getOrDefault(configs.gridControlConfig, c -> c.darkGridControlColumnBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* FormBuilder */");
        writer.println("  --form-builder-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormBuilderBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --form-field-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormFieldBg(), "rgba(255, 255, 255, 0.05)") + ";");
        writer.println("  --form-field-border: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormFieldBorder(), "rgba(255, 255, 255, 0.15)") + ";");
        writer.println("  --form-field-hover-border: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormFieldHoverBorder(), "rgba(255, 255, 255, 0.3)") + ";");
        writer.println("  --form-field-focus-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormFieldFocusBg(), "rgba(255, 255, 255, 0.08)") + ";");
        writer.println("  --form-dropdown-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormDropdownBg(), "#2a2a2a") + ";");
        writer.println("  --form-builder-card-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.darkFormBuilderCardBg(), "#2a2a2a") + ";");
        writer.println();
        writeVideoArticleGridTheme(writer, configs.videoArticleGridConfig, true);
        writer.println();
        writeAnalyticsChartTheme(writer, configs.analyticsChartConfig, true);
        writer.println();
        writeMetricTilesTheme(writer, configs.metricTilesConfig, true);
        writer.println();
        writeCodeSnippetTheme(writer, configs.codeSnippetConfig, true);
        writer.println();
        writeStepsTimelineTheme(writer, configs.stepsTimelineConfig, true);
        writer.println();
        writeFlowDiagramTheme(writer, configs.flowDiagramConfig, true);
        writer.println();
        writeScreenshotShowcaseTheme(writer, configs.screenshotShowcaseConfig, true);
        writer.println("}");
        writer.println();
        
        // Light Theme
        writer.println(".theme-light {");
        writer.println("  /* Main theme color */");
        writer.println("  --main-theme-color: " + getOrDefault(configs.globalConfig, c -> c.lightMainThemeColor(), "#42f4fd") + ";");
        writer.println();
        writer.println("  /* Header theme variables */");
        writer.println("  --header-background-color: " + getOrDefault(configs.headerConfig, c -> c.lightHeaderBackgroundColor(), "#fdfeff") + ";");
        writer.println("  --header-height: " + getOrDefault(configs.headerConfig, c -> c.headerHeight(), "60px") + ";");
        writer.println();
        writer.println("  /* Text color variables - Light theme */");
        writer.println("  --primary-text-color: " + getOrDefault(configs.globalConfig, c -> c.lightPrimaryTextColor(), "#000000") + ";");
        writer.println("  --secondary-text-color: " + getOrDefault(configs.globalConfig, c -> c.lightSecondaryTextColor(), "#4b5563") + ";");
        writer.println("  --standard-primary-site-text-color: " + getOrDefault(configs.globalConfig, c -> c.lightStandardPrimarySiteTextColor(), "#111827") + ";");
        writer.println("  --standard-secondary-site-text-color: " + getOrDefault(configs.globalConfig, c -> c.lightStandardSecondarySiteTextColor(), "#4b5563") + ";");
        writer.println("  --standard-site-font-size: " + getOrDefault(configs.globalConfig, c -> c.standardSiteFontSize(), "16px") + ";");
        writer.println("  --standard-site-font-weight: " + getOrDefault(configs.globalConfig, c -> c.standardSiteFontWeight(), "400") + ";");
        writer.println("  --site-font-family: " + getOrDefault(configs.globalConfig, c -> c.siteFontFamily(), "\"Hubot Sans\", \"Framer Display\", \"Instrument Serif\", \"Segoe UI\", Arial, sans-serif") + ";");
        writer.println("  --site-heading-font-family: " + getOrDefault(configs.globalConfig, c -> c.headingFontFamily(), "\"Hubot Sans\", \"Framer Display\", \"Instrument Serif\", \"Segoe UI\", Arial, sans-serif") + ";");
        writer.println("  --blockquote-border-color: " + getOrDefault(configs.globalConfig, c -> c.lightBlockquoteBorderColor(), "var(--primary-text-color)") + ";");
        writer.println("  --blockquote-bg: " + getOrDefault(configs.globalConfig, c -> c.lightBlockquoteBg(), "#d0fafc") + ";");
        writer.println("  --blockquote-quote-color: " + getOrDefault(configs.globalConfig, c -> c.lightBlockquoteQuoteColor(), "var(--primary-text-color)") + ";");
        writer.println();
        writer.println("  /* Global page */");
        writer.println("  --site-body-bg: " + getOrDefault(configs.globalConfig, c -> c.lightSiteBodyBg(), "#ffffff") + ";");
        writer.println("  --site-body-text: var(--standard-primary-site-text-color);");
        writer.println("  --site-overlay-bg: " + getOrDefault(configs.globalConfig, c -> c.lightSiteOverlayBg(), "rgba(255, 255, 255, 0.727)") + ";");
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(configs.footerConfig, c -> c.lightFooterBg(), "#f5f5f5") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(configs.footerConfig, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(configs.servicesConfig, c -> c.lightServicesDividerColor(), "rgba(0, 0, 0, 0.12)") + ";");
        writer.println("  --services-bg: " + getOrDefault(configs.servicesConfig, c -> c.lightServicesBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Light theme */");
        writer.println("  --button-theme-light-bg: " + getOrDefault(configs.buttonConfig, c -> c.lightButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-light-text: " + getOrDefault(configs.buttonConfig, c -> c.lightButtonText(), "#000000") + ";");
        writer.println("  --button-theme-light-border: " + getOrDefault(configs.buttonConfig, c -> c.lightButtonBorder(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-bg: " + getOrDefault(configs.buttonConfig, c -> c.lightButtonHoverBg(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-text: " + getOrDefault(configs.buttonConfig, c -> c.lightButtonHoverText(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* CTA Pill Button - Light theme */");
        writer.println("  --cta-pill-light-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.lightCtaPillBg(), "#000000") + ";");
        writer.println("  --cta-pill-light-text: " + getOrDefault(configs.ctaPillConfig, c -> c.lightCtaPillText(), "#ffffff") + ";");
        writer.println("  --cta-pill-light-hover-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.lightCtaPillHoverBg(), "#333333") + ";");
        writer.println("  --cta-pill-light-icon-bg: " + getOrDefault(configs.ctaPillConfig, c -> c.lightCtaPillIconBg(), "#ffffff") + ";");
        writer.println("  --cta-pill-light-icon-color: " + getOrDefault(configs.ctaPillConfig, c -> c.lightCtaPillIconColor(), "#000000") + ";");
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
        writer.println("  --lead-banner-height: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerGradientStart(), "#ffffff") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerGradientStop25(), "#aafbff") + ";");
        writer.println("  --lead-banner-gradient-stop-50: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerGradientStop50(), "#42c2fd") + ";");
        writer.println("  --lead-banner-gradient-stop-75: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerGradientStop75(), "#aafbff") + ";");
        writer.println("  --lead-banner-gradient-end: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerGradientEnd(), "#ffffff") + ";");
        writer.println("  --lead-banner-text-primary: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerTextPrimary(), "#323232") + ";");
        writer.println("  --lead-banner-text-secondary: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerTextSecondary(), "#6e6e6e") + ";");
        writer.println("  --lead-banner-secondary-text-color: " + getOrDefault(configs.leadBannerConfig, c -> c.lightLeadBannerSecondaryTextColor(), "#323232") + ";");
        writer.println("  --lead-banner-char-fade-duration: " + getOrDefault(configs.leadBannerConfig, c -> c.leadBannerCharFadeDuration(), "0.3s") + ";");
        writer.println();
        writer.println("  /* Article tiles (Header overlay) */");
        writer.println("  --article-tile-overlay-bg: " + getOrDefault(configs.articleTileConfig, c -> c.lightArticleTileOverlayBg(), "#42f4fd75") + ";");
        writer.println();
        writer.println("  /* Header overlay */");
        writer.println("  --header-overlay-column-divider-color: " + getOrDefault(configs.headerOverlayConfig, c -> c.lightHeaderOverlayColumnDividerColor(), "rgba(0, 0, 0, 0.18)") + ";");
        writer.println("  --header-overlay-hover-bg: " + getOrDefault(configs.headerOverlayConfig, c -> c.lightHeaderOverlayHoverBg(), "rgba(0, 0, 0, 0.08)") + ";");
        writer.println();
        writer.println("  /* Site banner */");
        writer.println("  --site-banner-bg: var(--main-theme-color);");
        writer.println("  --site-banner-text-color: var(--primary-text-color);");
        writer.println("  --site-banner-marquee-duration: " + getOrDefault(configs.siteBannerConfig, c -> c.lightSiteBannerMarqueeDuration(), "10s") + ";");
        writer.println("  --site-banner-cycle-duration: " + getOrDefault(configs.siteBannerConfig, c -> c.lightSiteBannerCycleDuration(), "20s") + ";");
        writer.println("  --site-banner-font-size: " + getOrDefault(configs.siteBannerConfig, c -> c.siteBannerFontSize(), "20px") + ";");
        writer.println();
        writer.println("  /* Quote */");
        writer.println("  --quote-bg: var(--site-body-bg);");
        writer.println("  --quote-card-glow: " + getOrDefault(configs.quoteConfig, c -> c.lightQuoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, var(--main-theme-color)ad, transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, var(--main-theme-color)5e, transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(configs.loopingCircleGalleryConfig, c -> c.lightLoopingCircleGalleryOverlayBg(), "linear-gradient(180deg, #ffffff53 0%, var(--main-theme-color) 50%, #ffffff53 100%)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: var(--primary-text-color);");
        writer.println();
        writer.println("  /* CountUp */");
        writer.println("  --count-up-bg: " + getOrDefault(configs.countUpConfig, c -> c.lightCountUpBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --count-up-card-bg: " + getOrDefault(configs.countUpConfig, c -> c.lightCountUpCardBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* SubscriptionPlans */");
        writer.println("  --subscription-plans-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --subscription-plans-card-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansCardBg(), "#ffffff") + ";");
        writer.println("  --subscription-plans-card-border: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansCardBorder(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-toggle-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansToggleBg(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-toggle-active-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansToggleActiveBg(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-toggle-active-text: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansToggleActiveText(), "#ffffff") + ";");
        writer.println("  --subscription-plans-save-badge-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansSaveBadgeBg(), "#dcfce7") + ";");
        writer.println("  --subscription-plans-save-badge-text: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansSaveBadgeText(), "#16a34a") + ";");
        writer.println("  --subscription-plans-price-color: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansPriceColor(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-highlight-border: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansHighlightBorder(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-divider: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansDivider(), "#e5e7eb") + ";");
        writer.println("  --subscription-plans-credits-icon: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansCreditsIcon(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-feature-check: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansFeatureCheck(), "#22c55e") + ";");
        writer.println("  --subscription-plans-cta-primary-bg: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansCtaPrimaryBg(), "#3b82f6") + ";");
        writer.println("  --subscription-plans-cta-primary-hover: " + getOrDefault(configs.subscriptionPlansConfig, c -> c.lightSubscriptionPlansCtaPrimaryHover(), "#2563eb") + ";");
        writer.println();
        writer.println("  /* CompareSubscription */");
        writer.println("  --compare-subscription-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --compare-subscription-table-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionTableBg(), "#ffffff") + ";");
        writer.println("  --compare-subscription-border: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionBorder(), "#e5e7eb") + ";");
        writer.println("  --compare-subscription-features-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionFeaturesBg(), "#f9fafb") + ";");
        writer.println("  --compare-subscription-row-alt-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionRowAltBg(), "rgba(0, 0, 0, 0.02)") + ";");
        writer.println("  --compare-subscription-section-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionSectionBg(), "#f3f4f6") + ";");
        writer.println("  --compare-subscription-highlight-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionHighlightBg(), "rgba(59, 130, 246, 0.05)") + ";");
        writer.println("  --compare-subscription-highlight-accent: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionHighlightAccent(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-price-color: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionPriceColor(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-check-color: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionCheckColor(), "#22c55e") + ";");
        writer.println("  --compare-subscription-cross-color: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionCrossColor(), "#ef4444") + ";");
        writer.println("  --compare-subscription-cta-primary-bg: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionCtaPrimaryBg(), "#3b82f6") + ";");
        writer.println("  --compare-subscription-cta-primary-hover: " + getOrDefault(configs.compareSubscriptionConfig, c -> c.lightCompareSubscriptionCtaPrimaryHover(), "#2563eb") + ";");
        writer.println();
        writer.println("  /* Rating */");
        writer.println("  --rating-bg: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --rating-avatar-border: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingAvatarBorder(), "#ffffff") + ";");
        writer.println("  --rating-avatar-bg: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingAvatarBg(), "#e5e7eb") + ";");
        writer.println("  --rating-star-color: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingStarColor(), "#fbbf24") + ";");
        writer.println("  --rating-star-empty-color: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingStarEmptyColor(), "#d1d5db") + ";");
        writer.println("  --rating-cta-bg: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingCtaBg(), "#000000") + ";");
        writer.println("  --rating-cta-text: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingCtaText(), "#ffffff") + ";");
        writer.println("  --rating-cta-hover-bg: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingCtaHoverBg(), "#333333") + ";");
        writer.println("  --rating-cta-icon-bg: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingCtaIconBg(), "#ffffff") + ";");
        writer.println("  --rating-cta-icon-color: " + getOrDefault(configs.ratingConfig, c -> c.lightRatingCtaIconColor(), "#000000") + ";");
        writer.println();
        writer.println("  /* MarqueeCarousel */");
        writer.println("  --marquee-carousel-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --marquee-carousel-fade-color: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselFadeColor(), "#dbeeff") + ";");
        writer.println("  --marquee-carousel-card-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCardBg(), "#e5e7eb") + ";");
        writer.println("  --marquee-carousel-cta-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCtaBg(), "#000000") + ";");
        writer.println("  --marquee-carousel-cta-text: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCtaText(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-hover-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCtaHoverBg(), "#333333") + ";");
        writer.println("  --marquee-carousel-cta-icon-bg: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCtaIconBg(), "#ffffff") + ";");
        writer.println("  --marquee-carousel-cta-icon-color: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.lightMarqueeCarouselCtaIconColor(), "#000000") + ";");
        writer.println("  --marquee-carousel-duration: " + getOrDefault(configs.marqueeCarouselConfig, c -> c.marqueeCarouselDuration(), "35s") + ";");
        writer.println();
        writer.println("  /* MasonryGallery */");
        writer.println("  --masonry-gallery-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.lightMasonryGalleryBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --masonry-gallery-item-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.lightMasonryGalleryItemBg(), "rgba(0, 0, 0, 0.03)") + ";");
        writer.println("  --masonry-gallery-lightbox-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.lightMasonryGalleryLightboxBg(), "rgba(10, 10, 10, 0.95)") + ";");
        writer.println("  --masonry-gallery-lightbox-title-color: " + getOrDefault(configs.masonryGalleryConfig, c -> c.lightMasonryGalleryLightboxTitleColor(), "#ffffff") + ";");
        writer.println("  --masonry-gallery-lightbox-title-bg: " + getOrDefault(configs.masonryGalleryConfig, c -> c.lightMasonryGalleryLightboxTitleBg(), "rgba(0, 0, 0, 0.7)") + ";");
        writer.println();
        writer.println("  /* Comparison */");
        writer.println("  --comparison-bg: " + getOrDefault(configs.comparisonConfig, c -> c.lightComparisonBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --comparison-col-bg: " + getOrDefault(configs.comparisonConfig, c -> c.lightComparisonColBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* FAQ */");
        writer.println("  --faq-bg: " + getOrDefault(configs.faqConfig, c -> c.lightFaqBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --faq-item-bg: " + getOrDefault(configs.faqConfig, c -> c.lightFaqItemBg(), "#f2fffd") + ";");
        writer.println("  --faq-item-hover-bg: " + getOrDefault(configs.faqConfig, c -> c.lightFaqItemHoverBg(), "#e5e7eb") + ";");
        writer.println();
        writer.println("  /* Cards */");
        writer.println("  --cards-bg: " + getOrDefault(configs.cardsConfig, c -> c.lightCardsBg(), "var(--site-body-bg, #ffffff)") + ";");
        writer.println("  --cards-muted-bg: " + getOrDefault(configs.cardsConfig, c -> c.lightCardsMutedBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --cards-item-bg: " + getOrDefault(configs.cardsConfig, c -> c.lightCardsItemBg(), "#ffffff") + ";");
        writer.println("  --cards-item-border: " + getOrDefault(configs.cardsConfig, c -> c.lightCardsItemBorder(), "rgba(0, 0, 0, 0.08)") + ";");
        writer.println("  --cards-item-icon-bg: " + getOrDefault(configs.cardsConfig, c -> c.lightCardsItemIconBg(), "rgba(0, 0, 0, 0.04)") + ";");
        writer.println();
        writer.println("  /* Call to Action */");
        writer.println("  --call-to-action-bg: " + getOrDefault(configs.callToActionConfig, c -> c.lightCallToActionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* Data Table */");
        writer.println("  --data-table-bg: " + getOrDefault(configs.dataTableConfig, c -> c.lightDataTableBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --data-table-header-bg: " + getOrDefault(configs.dataTableConfig, c -> c.lightDataTableHeaderBg(), "rgba(0, 0, 0, 0.04)") + ";");
        writer.println("  --data-table-cell-border: " + getOrDefault(configs.dataTableConfig, c -> c.lightDataTableCellBorder(), "rgba(0, 0, 0, 0.1)") + ";");
        writer.println("  --data-table-row-hover-bg: " + getOrDefault(configs.dataTableConfig, c -> c.lightDataTableRowHoverBg(), "rgba(0, 0, 0, 0.03)") + ";");
        writer.println();
        writer.println("  /* Hero */");
        writer.println("  --hero-bg: " + getOrDefault(configs.heroConfig, c -> c.lightHeroBg(), "linear-gradient(135deg, var(--lead-banner-gradient-start, #ffffff) 0%, var(--lead-banner-gradient-stop-25, #aafbff) 50%, var(--lead-banner-gradient-end, #ffffff) 100%)") + ";");
        writer.println();
        writeLeadCarouselTheme(writer, configs.leadCarouselConfig, false);
        writer.println();
        writer.println("  /* BlobImageSection */");
        writer.println("  --blob-image-section-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --blob-image-section-card-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionCardBg(), "#f2fffd") + ";");
        writer.println("  --blob-image-section-badge-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionBadgeBg(), "#ffffff") + ";");
        writer.println("  --blob-image-section-badge-border: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionBadgeBorder(), "#e5e7eb") + ";");
        writer.println("  --blob-image-section-badge-text: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionBadgeText(), "#3b82f6") + ";");
        writer.println("  --blob-image-section-icon-badge-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionIconBadgeBg(), "#8b5cf6") + ";");
        writer.println("  --blob-image-section-icon-badge-color: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionIconBadgeColor(), "#ffffff") + ";");
        writer.println("  --blob-image-section-overlay-card-bg: " + getOrDefault(configs.blobImageSectionConfig, c -> c.lightBlobImageSectionOverlayCardBg(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* LeadMediaSection */");
        writer.println("  --lead-media-section-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --lead-media-section-card-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCardBg(), "#f2fffd") + ";");
        writer.println("  --lead-media-section-icon-badge-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionIconBadgeBg(), "#e5e7eb") + ";");
        writer.println("  --lead-media-section-icon-badge-color: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionIconBadgeColor(), "#374151") + ";");
        writer.println("  --lead-media-section-cta-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCtaBg(), "#000000") + ";");
        writer.println("  --lead-media-section-cta-text: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCtaText(), "#ffffff") + ";");
        writer.println("  --lead-media-section-cta-hover-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCtaHoverBg(), "#333333") + ";");
        writer.println("  --lead-media-section-cta-icon-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCtaIconBg(), "#3b82f6") + ";");
        writer.println("  --lead-media-section-cta-icon-color: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionCtaIconColor(), "#ffffff") + ";");
        writer.println("  --lead-media-section-media-bg: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionMediaBg(), "#e5e7eb") + ";");
        writer.println("  --lead-media-section-media-border: " + getOrDefault(configs.leadMediaSectionConfig, c -> c.lightLeadMediaSectionMediaBorder(), "rgba(180, 180, 180, 0.5)") + ";");
        writer.println();
        writer.println("  /* GridControl */");
        writer.println("  --grid-control-bg: " + getOrDefault(configs.gridControlConfig, c -> c.lightGridControlBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --grid-control-column-bg: " + getOrDefault(configs.gridControlConfig, c -> c.lightGridControlColumnBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println();
        writer.println("  /* FormBuilder */");
        writer.println("  --form-builder-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormBuilderBg(), "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)") + ";");
        writer.println("  --form-field-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormFieldBg(), "rgba(0, 0, 0, 0.03)") + ";");
        writer.println("  --form-field-border: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormFieldBorder(), "rgba(0, 0, 0, 0.15)") + ";");
        writer.println("  --form-field-hover-border: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormFieldHoverBorder(), "rgba(0, 0, 0, 0.3)") + ";");
        writer.println("  --form-field-focus-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormFieldFocusBg(), "rgba(0, 0, 0, 0.05)") + ";");
        writer.println("  --form-dropdown-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormDropdownBg(), "#ffffff") + ";");
        writer.println("  --form-builder-card-bg: " + getOrDefault(configs.formBuilderConfig, c -> c.lightFormBuilderCardBg(), "#f2fffd") + ";");
        writer.println();
        writeVideoArticleGridTheme(writer, configs.videoArticleGridConfig, false);
        writer.println();
        writeAnalyticsChartTheme(writer, configs.analyticsChartConfig, false);
        writer.println();
        writeMetricTilesTheme(writer, configs.metricTilesConfig, false);
        writer.println();
        writeCodeSnippetTheme(writer, configs.codeSnippetConfig, false);
        writer.println();
        writeStepsTimelineTheme(writer, configs.stepsTimelineConfig, false);
        writer.println();
        writeFlowDiagramTheme(writer, configs.flowDiagramConfig, false);
        writer.println();
        writeScreenshotShowcaseTheme(writer, configs.screenshotShowcaseConfig, false);
        writer.println("}");
    }

    private void writeVideoArticleGridTheme(PrintWriter writer, VideoArticleGridThemeConfig config, boolean dark) {
        writer.println("  /* VideoArticleGrid */");
        if (dark) {
            writer.println("  --video-article-grid-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)") + ";");
            writer.println("  --video-article-grid-card-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridCardBg(), "#1a1a2e") + ";");
            writer.println("  --video-article-grid-card-border: " + getOrDefault(config, c -> c.darkVideoArticleGridCardBorder(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --video-article-grid-thumb-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridThumbBg(), "#0d0d1a") + ";");
            writer.println("  --video-article-grid-thumb-overlay: " + getOrDefault(config, c -> c.darkVideoArticleGridThumbOverlay(), "rgba(0, 0, 0, 0.15)") + ";");
            writer.println("  --video-article-grid-play-btn-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridPlayBtnBg(), "rgba(255, 255, 255, 0.1)") + ";");
            writer.println("  --video-article-grid-play-btn-border: " + getOrDefault(config, c -> c.darkVideoArticleGridPlayBtnBorder(), "rgba(255, 255, 255, 0.18)") + ";");
            writer.println("  --video-article-grid-play-btn-color: " + getOrDefault(config, c -> c.darkVideoArticleGridPlayBtnColor(), "rgba(255, 255, 255, 0.6)") + ";");
            writer.println("  --video-article-grid-play-btn-hover-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridPlayBtnHoverBg(), "rgba(255, 255, 255, 0.2)") + ";");
            writer.println("  --video-article-grid-play-btn-hover-color: " + getOrDefault(config, c -> c.darkVideoArticleGridPlayBtnHoverColor(), "#ffffff") + ";");
            writer.println("  --video-article-grid-badge-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridBadgeBg(), "rgba(255, 255, 255, 0.06)") + ";");
            writer.println("  --video-article-grid-badge-text: " + getOrDefault(config, c -> c.darkVideoArticleGridBadgeText(), "#9ca3af") + ";");
            writer.println("  --video-article-grid-badge-border: " + getOrDefault(config, c -> c.darkVideoArticleGridBadgeBorder(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --video-article-grid-search-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridSearchBg(), "rgba(255, 255, 255, 0.05)") + ";");
            writer.println("  --video-article-grid-search-border: " + getOrDefault(config, c -> c.darkVideoArticleGridSearchBorder(), "rgba(255, 255, 255, 0.1)") + ";");
            writer.println("  --video-article-grid-search-focus-border: " + getOrDefault(config, c -> c.darkVideoArticleGridSearchFocusBorder(), "rgba(255, 255, 255, 0.28)") + ";");
            writer.println("  --video-article-grid-search-focus-ring: " + getOrDefault(config, c -> c.darkVideoArticleGridSearchFocusRing(), "rgba(255, 255, 255, 0.06)") + ";");
            writer.println("  --video-article-grid-page-btn-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridPageBtnBg(), "transparent") + ";");
            writer.println("  --video-article-grid-page-btn-text: " + getOrDefault(config, c -> c.darkVideoArticleGridPageBtnText(), "#ffffff") + ";");
            writer.println("  --video-article-grid-page-btn-border: " + getOrDefault(config, c -> c.darkVideoArticleGridPageBtnBorder(), "#ffffff") + ";");
            writer.println("  --video-article-grid-page-btn-hover-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridPageBtnHoverBg(), "#ffffff") + ";");
            writer.println("  --video-article-grid-page-btn-hover-text: " + getOrDefault(config, c -> c.darkVideoArticleGridPageBtnHoverText(), "#000000") + ";");
            writer.println("  --video-article-grid-dropdown-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridDropdownBg(), "rgba(255, 255, 255, 0.06)") + ";");
            writer.println("  --video-article-grid-dropdown-border: " + getOrDefault(config, c -> c.darkVideoArticleGridDropdownBorder(), "rgba(255, 255, 255, 0.1)") + ";");
            writer.println("  --video-article-grid-dropdown-focus-border: " + getOrDefault(config, c -> c.darkVideoArticleGridDropdownFocusBorder(), "rgba(255, 255, 255, 0.28)") + ";");
            writer.println("  --video-article-grid-dropdown-focus-ring: " + getOrDefault(config, c -> c.darkVideoArticleGridDropdownFocusRing(), "rgba(255, 255, 255, 0.06)") + ";");
            writer.println("  --video-article-grid-dropdown-option-bg: " + getOrDefault(config, c -> c.darkVideoArticleGridDropdownOptionBg(), "#1e1e1e") + ";");
        } else {
            writer.println("  --video-article-grid-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridBg(), "radial-gradient(circle, var(--main-theme-color) 0%, var(--site-body-bg) 70%)") + ";");
            writer.println("  --video-article-grid-card-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridCardBg(), "#ffffff") + ";");
            writer.println("  --video-article-grid-card-border: " + getOrDefault(config, c -> c.lightVideoArticleGridCardBorder(), "#e5e7eb") + ";");
            writer.println("  --video-article-grid-thumb-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridThumbBg(), "#e8f4f8") + ";");
            writer.println("  --video-article-grid-thumb-overlay: " + getOrDefault(config, c -> c.lightVideoArticleGridThumbOverlay(), "rgba(0, 0, 0, 0.04)") + ";");
            writer.println("  --video-article-grid-play-btn-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridPlayBtnBg(), "rgba(0, 0, 0, 0.06)") + ";");
            writer.println("  --video-article-grid-play-btn-border: " + getOrDefault(config, c -> c.lightVideoArticleGridPlayBtnBorder(), "rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --video-article-grid-play-btn-color: " + getOrDefault(config, c -> c.lightVideoArticleGridPlayBtnColor(), "rgba(0, 0, 0, 0.45)") + ";");
            writer.println("  --video-article-grid-play-btn-hover-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridPlayBtnHoverBg(), "rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --video-article-grid-play-btn-hover-color: " + getOrDefault(config, c -> c.lightVideoArticleGridPlayBtnHoverColor(), "#000000") + ";");
            writer.println("  --video-article-grid-badge-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridBadgeBg(), "#f3f4f6") + ";");
            writer.println("  --video-article-grid-badge-text: " + getOrDefault(config, c -> c.lightVideoArticleGridBadgeText(), "#6b7280") + ";");
            writer.println("  --video-article-grid-badge-border: " + getOrDefault(config, c -> c.lightVideoArticleGridBadgeBorder(), "#e5e7eb") + ";");
            writer.println("  --video-article-grid-search-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridSearchBg(), "rgba(0, 0, 0, 0.03)") + ";");
            writer.println("  --video-article-grid-search-border: " + getOrDefault(config, c -> c.lightVideoArticleGridSearchBorder(), "rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --video-article-grid-search-focus-border: " + getOrDefault(config, c -> c.lightVideoArticleGridSearchFocusBorder(), "rgba(0, 0, 0, 0.3)") + ";");
            writer.println("  --video-article-grid-search-focus-ring: " + getOrDefault(config, c -> c.lightVideoArticleGridSearchFocusRing(), "rgba(0, 0, 0, 0.06)") + ";");
            writer.println("  --video-article-grid-page-btn-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridPageBtnBg(), "transparent") + ";");
            writer.println("  --video-article-grid-page-btn-text: " + getOrDefault(config, c -> c.lightVideoArticleGridPageBtnText(), "#000000") + ";");
            writer.println("  --video-article-grid-page-btn-border: " + getOrDefault(config, c -> c.lightVideoArticleGridPageBtnBorder(), "#000000") + ";");
            writer.println("  --video-article-grid-page-btn-hover-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridPageBtnHoverBg(), "#000000") + ";");
            writer.println("  --video-article-grid-page-btn-hover-text: " + getOrDefault(config, c -> c.lightVideoArticleGridPageBtnHoverText(), "#ffffff") + ";");
            writer.println("  --video-article-grid-dropdown-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridDropdownBg(), "rgba(0, 0, 0, 0.03)") + ";");
            writer.println("  --video-article-grid-dropdown-border: " + getOrDefault(config, c -> c.lightVideoArticleGridDropdownBorder(), "rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --video-article-grid-dropdown-focus-border: " + getOrDefault(config, c -> c.lightVideoArticleGridDropdownFocusBorder(), "rgba(0, 0, 0, 0.3)") + ";");
            writer.println("  --video-article-grid-dropdown-focus-ring: " + getOrDefault(config, c -> c.lightVideoArticleGridDropdownFocusRing(), "rgba(0, 0, 0, 0.06)") + ";");
            writer.println("  --video-article-grid-dropdown-option-bg: " + getOrDefault(config, c -> c.lightVideoArticleGridDropdownOptionBg(), "#ffffff") + ";");
        }
    }

    private void writeAnalyticsChartTheme(PrintWriter writer, AnalyticsChartThemeConfig config, boolean dark) {
        writer.println("  /* AnalyticsChart */");
        if (dark) {
            writer.println("  --analytics-chart-bg: " + getOrDefault(config, c -> c.darkAnalyticsChartBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --analytics-chart-muted-bg: " + getOrDefault(config, c -> c.darkAnalyticsChartMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --analytics-chart-panel-bg: " + getOrDefault(config, c -> c.darkAnalyticsChartPanelBg(), "#2a2a2a") + ";");
            writer.println("  --analytics-chart-panel-border: " + getOrDefault(config, c -> c.darkAnalyticsChartPanelBorder(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --analytics-chart-panel-shadow: " + getOrDefault(config, c -> c.darkAnalyticsChartPanelShadow(), "0 6px 24px rgba(0, 0, 0, 0.28)") + ";");
            writer.println("  --analytics-chart-panel-shadow-hover: " + getOrDefault(config, c -> c.darkAnalyticsChartPanelShadowHover(), "0 16px 44px rgba(0, 0, 0, 0.38)") + ";");
            writer.println("  --analytics-chart-grid-color: " + getOrDefault(config, c -> c.darkAnalyticsChartGridColor(), "rgba(255, 255, 255, 0.09)") + ";");
            writer.println("  --analytics-chart-tick-color: " + getOrDefault(config, c -> c.darkAnalyticsChartTickColor(), "rgba(255, 255, 255, 0.45)") + ";");
            writer.println("  --analytics-chart-track-color: " + getOrDefault(config, c -> c.darkAnalyticsChartTrackColor(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --analytics-chart-badge-bg: " + getOrDefault(config, c -> c.darkAnalyticsChartBadgeBg(), "rgba(74, 222, 128, 0.14)") + ";");
            writer.println("  --analytics-chart-badge-border: " + getOrDefault(config, c -> c.darkAnalyticsChartBadgeBorder(), "rgba(74, 222, 128, 0.3)") + ";");
            writer.println("  --analytics-chart-badge-text: " + getOrDefault(config, c -> c.darkAnalyticsChartBadgeText(), "#4ade80") + ";");
            writeCategoryColors(writer, getOrDefault(config, c -> c.darkAnalyticsChartCategoryColors(),
                    DARK_CATEGORY_COLORS), DARK_CATEGORY_COLORS);
        } else {
            writer.println("  --analytics-chart-bg: " + getOrDefault(config, c -> c.lightAnalyticsChartBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --analytics-chart-muted-bg: " + getOrDefault(config, c -> c.lightAnalyticsChartMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --analytics-chart-panel-bg: " + getOrDefault(config, c -> c.lightAnalyticsChartPanelBg(), "#ffffff") + ";");
            writer.println("  --analytics-chart-panel-border: " + getOrDefault(config, c -> c.lightAnalyticsChartPanelBorder(), "rgba(0, 0, 0, 0.08)") + ";");
            writer.println("  --analytics-chart-panel-shadow: " + getOrDefault(config, c -> c.lightAnalyticsChartPanelShadow(), "0 4px 18px rgba(0, 0, 0, 0.07)") + ";");
            writer.println("  --analytics-chart-panel-shadow-hover: " + getOrDefault(config, c -> c.lightAnalyticsChartPanelShadowHover(), "0 14px 38px rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --analytics-chart-grid-color: " + getOrDefault(config, c -> c.lightAnalyticsChartGridColor(), "rgba(0, 0, 0, 0.09)") + ";");
            writer.println("  --analytics-chart-tick-color: " + getOrDefault(config, c -> c.lightAnalyticsChartTickColor(), "rgba(0, 0, 0, 0.5)") + ";");
            writer.println("  --analytics-chart-track-color: " + getOrDefault(config, c -> c.lightAnalyticsChartTrackColor(), "rgba(0, 0, 0, 0.08)") + ";");
            writer.println("  --analytics-chart-badge-bg: " + getOrDefault(config, c -> c.lightAnalyticsChartBadgeBg(), "rgba(5, 150, 105, 0.1)") + ";");
            writer.println("  --analytics-chart-badge-border: " + getOrDefault(config, c -> c.lightAnalyticsChartBadgeBorder(), "rgba(5, 150, 105, 0.25)") + ";");
            writer.println("  --analytics-chart-badge-text: " + getOrDefault(config, c -> c.lightAnalyticsChartBadgeText(), "#047857") + ";");
            writeCategoryColors(writer, getOrDefault(config, c -> c.lightAnalyticsChartCategoryColors(),
                    LIGHT_CATEGORY_COLORS), LIGHT_CATEGORY_COLORS);
        }
    }

    /**
     * Writes the categorical chart palette as --analytics-chart-cat-1..N. The
     * component asks for a fixed number of slots and cycles the configured
     * colours to fill them, so a shorter palette still colours every category.
     */
    private void writeCategoryColors(PrintWriter writer, String configured, String fallback) {
        List<String> colors = splitTopLevel(configured);
        if (colors.isEmpty()) {
            colors = splitTopLevel(fallback);
        }
        for (int slot = 0; slot < CATEGORY_COLOR_SLOTS; slot++) {
            writer.println("  --analytics-chart-cat-" + (slot + 1) + ": "
                    + colors.get(slot % colors.size()) + ";");
        }
    }

    /**
     * Splits a comma-separated CSS value list, ignoring commas nested inside
     * functional notation such as {@code rgba(0, 0, 0, 0.5)}.
     */
    private static List<String> splitTopLevel(String raw) {
        List<String> parts = new ArrayList<>();
        if (raw == null) {
            return parts;
        }
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth = Math.max(depth - 1, 0);
            }
            if (ch == ',' && depth == 0) {
                addTrimmed(parts, current);
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        addTrimmed(parts, current);
        return parts;
    }

    private static void addTrimmed(List<String> parts, StringBuilder value) {
        String trimmed = value.toString().trim();
        if (!trimmed.isEmpty()) {
            parts.add(trimmed);
        }
    }

    private void writeMetricTilesTheme(PrintWriter writer, MetricTilesThemeConfig config, boolean dark) {
        writer.println("  /* MetricTiles */");
        if (dark) {
            writer.println("  --metric-tiles-bg: " + getOrDefault(config, c -> c.darkMetricTilesBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --metric-tiles-muted-bg: " + getOrDefault(config, c -> c.darkMetricTilesMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --metric-tiles-card-bg: " + getOrDefault(config, c -> c.darkMetricTilesCardBg(), "#2a2a2a") + ";");
            writer.println("  --metric-tiles-card-border: " + getOrDefault(config, c -> c.darkMetricTilesCardBorder(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --metric-tiles-face-bg: " + getOrDefault(config, c -> c.darkMetricTilesFaceBg(), "linear-gradient(115deg, #f8fafc 0%, #dbeafe 45%, var(--metric-tiles-face-accent, #3b82f6) 100%)") + ";");
            writer.println("  --metric-tiles-face-accent: " + getOrDefault(config, c -> c.darkMetricTilesFaceAccent(), "#3b82f6") + ";");
            writer.println("  --metric-tiles-face-text: " + getOrDefault(config, c -> c.darkMetricTilesFaceText(), "#0f172a") + ";");
        } else {
            writer.println("  --metric-tiles-bg: " + getOrDefault(config, c -> c.lightMetricTilesBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --metric-tiles-muted-bg: " + getOrDefault(config, c -> c.lightMetricTilesMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --metric-tiles-card-bg: " + getOrDefault(config, c -> c.lightMetricTilesCardBg(), "#ffffff") + ";");
            writer.println("  --metric-tiles-card-border: " + getOrDefault(config, c -> c.lightMetricTilesCardBorder(), "rgba(0, 0, 0, 0.08)") + ";");
            writer.println("  --metric-tiles-face-bg: " + getOrDefault(config, c -> c.lightMetricTilesFaceBg(), "linear-gradient(115deg, #ffffff 0%, #eff6ff 45%, var(--metric-tiles-face-accent, #2563eb) 100%)") + ";");
            writer.println("  --metric-tiles-face-accent: " + getOrDefault(config, c -> c.lightMetricTilesFaceAccent(), "#2563eb") + ";");
            writer.println("  --metric-tiles-face-text: " + getOrDefault(config, c -> c.lightMetricTilesFaceText(), "#0f172a") + ";");
        }
    }

    private void writeCodeSnippetTheme(PrintWriter writer, CodeSnippetThemeConfig config, boolean dark) {
        writer.println("  /* CodeSnippet */");
        if (dark) {
            writer.println("  --code-snippet-bg: " + getOrDefault(config, c -> c.darkCodeSnippetBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --code-snippet-muted-bg: " + getOrDefault(config, c -> c.darkCodeSnippetMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --code-snippet-shell-bg: " + getOrDefault(config, c -> c.darkCodeSnippetShellBg(), "#16161d") + ";");
            writer.println("  --code-snippet-shell-border: " + getOrDefault(config, c -> c.darkCodeSnippetShellBorder(), "rgba(255, 255, 255, 0.1)") + ";");
            writer.println("  --code-snippet-bar-bg: " + getOrDefault(config, c -> c.darkCodeSnippetBarBg(), "rgba(255, 255, 255, 0.05)") + ";");
            writer.println("  --code-snippet-text: " + getOrDefault(config, c -> c.darkCodeSnippetText(), "#e6e6ef") + ";");
            writer.println("  --code-snippet-tab-text: " + getOrDefault(config, c -> c.darkCodeSnippetTabText(), "rgba(255, 255, 255, 0.6)") + ";");
            writer.println("  --code-snippet-tab-hover-bg: " + getOrDefault(config, c -> c.darkCodeSnippetTabHoverBg(), "rgba(255, 255, 255, 0.07)") + ";");
            writer.println("  --code-snippet-tab-active-bg: " + getOrDefault(config, c -> c.darkCodeSnippetTabActiveBg(), "rgba(255, 255, 255, 0.13)") + ";");
            writer.println("  --code-snippet-tab-active-text: " + getOrDefault(config, c -> c.darkCodeSnippetTabActiveText(), "#ffffff") + ";");
            writer.println("  --code-snippet-copy-bg: " + getOrDefault(config, c -> c.darkCodeSnippetCopyBg(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --code-snippet-copy-hover-bg: " + getOrDefault(config, c -> c.darkCodeSnippetCopyHoverBg(), "rgba(255, 255, 255, 0.16)") + ";");
            writer.println("  --code-snippet-copied-color: " + getOrDefault(config, c -> c.darkCodeSnippetCopiedColor(), "#4ade80") + ";");
            writer.println("  --code-snippet-gutter-color: " + getOrDefault(config, c -> c.darkCodeSnippetGutterColor(), "rgba(255, 255, 255, 0.25)") + ";");
            writer.println("  --code-snippet-tok-key: " + getOrDefault(config, c -> c.darkCodeSnippetTokKey(), "#c792ea") + ";");
            writer.println("  --code-snippet-tok-str: " + getOrDefault(config, c -> c.darkCodeSnippetTokStr(), "#a5e075") + ";");
            writer.println("  --code-snippet-tok-num: " + getOrDefault(config, c -> c.darkCodeSnippetTokNum(), "#f78c6c") + ";");
            writer.println("  --code-snippet-tok-comment: " + getOrDefault(config, c -> c.darkCodeSnippetTokComment(), "#6b7789") + ";");
            writer.println("  --code-snippet-tok-tag: " + getOrDefault(config, c -> c.darkCodeSnippetTokTag(), "#7fd1f7") + ";");
            writer.println("  --code-snippet-tok-attr: " + getOrDefault(config, c -> c.darkCodeSnippetTokAttr(), "#ffcb6b") + ";");
        } else {
            writer.println("  --code-snippet-bg: " + getOrDefault(config, c -> c.lightCodeSnippetBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --code-snippet-muted-bg: " + getOrDefault(config, c -> c.lightCodeSnippetMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --code-snippet-shell-bg: " + getOrDefault(config, c -> c.lightCodeSnippetShellBg(), "#1b1b25") + ";");
            writer.println("  --code-snippet-shell-border: " + getOrDefault(config, c -> c.lightCodeSnippetShellBorder(), "rgba(0, 0, 0, 0.14)") + ";");
            writer.println("  --code-snippet-bar-bg: " + getOrDefault(config, c -> c.lightCodeSnippetBarBg(), "rgba(255, 255, 255, 0.05)") + ";");
            writer.println("  --code-snippet-text: " + getOrDefault(config, c -> c.lightCodeSnippetText(), "#e6e6ef") + ";");
            writer.println("  --code-snippet-tab-text: " + getOrDefault(config, c -> c.lightCodeSnippetTabText(), "rgba(255, 255, 255, 0.6)") + ";");
            writer.println("  --code-snippet-tab-hover-bg: " + getOrDefault(config, c -> c.lightCodeSnippetTabHoverBg(), "rgba(255, 255, 255, 0.07)") + ";");
            writer.println("  --code-snippet-tab-active-bg: " + getOrDefault(config, c -> c.lightCodeSnippetTabActiveBg(), "rgba(255, 255, 255, 0.13)") + ";");
            writer.println("  --code-snippet-tab-active-text: " + getOrDefault(config, c -> c.lightCodeSnippetTabActiveText(), "#ffffff") + ";");
            writer.println("  --code-snippet-copy-bg: " + getOrDefault(config, c -> c.lightCodeSnippetCopyBg(), "rgba(255, 255, 255, 0.08)") + ";");
            writer.println("  --code-snippet-copy-hover-bg: " + getOrDefault(config, c -> c.lightCodeSnippetCopyHoverBg(), "rgba(255, 255, 255, 0.16)") + ";");
            writer.println("  --code-snippet-copied-color: " + getOrDefault(config, c -> c.lightCodeSnippetCopiedColor(), "#4ade80") + ";");
            writer.println("  --code-snippet-gutter-color: " + getOrDefault(config, c -> c.lightCodeSnippetGutterColor(), "rgba(255, 255, 255, 0.25)") + ";");
            writer.println("  --code-snippet-tok-key: " + getOrDefault(config, c -> c.lightCodeSnippetTokKey(), "#c792ea") + ";");
            writer.println("  --code-snippet-tok-str: " + getOrDefault(config, c -> c.lightCodeSnippetTokStr(), "#a5e075") + ";");
            writer.println("  --code-snippet-tok-num: " + getOrDefault(config, c -> c.lightCodeSnippetTokNum(), "#f78c6c") + ";");
            writer.println("  --code-snippet-tok-comment: " + getOrDefault(config, c -> c.lightCodeSnippetTokComment(), "#6b7789") + ";");
            writer.println("  --code-snippet-tok-tag: " + getOrDefault(config, c -> c.lightCodeSnippetTokTag(), "#7fd1f7") + ";");
            writer.println("  --code-snippet-tok-attr: " + getOrDefault(config, c -> c.lightCodeSnippetTokAttr(), "#ffcb6b") + ";");
        }
    }

    private void writeStepsTimelineTheme(PrintWriter writer, StepsTimelineThemeConfig config, boolean dark) {
        writer.println("  /* StepsTimeline */");
        if (dark) {
            writer.println("  --steps-timeline-bg: " + getOrDefault(config, c -> c.darkStepsTimelineBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --steps-timeline-muted-bg: " + getOrDefault(config, c -> c.darkStepsTimelineMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --steps-timeline-rail-bg: " + getOrDefault(config, c -> c.darkStepsTimelineRailBg(), "rgba(255, 255, 255, 0.12)") + ";");
            writer.println("  --steps-timeline-rail-fill: " + getOrDefault(config, c -> c.darkStepsTimelineRailFill(), "var(--main-theme-color, #e3a002)") + ";");
            writer.println("  --steps-timeline-marker-bg: " + getOrDefault(config, c -> c.darkStepsTimelineMarkerBg(), "#2a2a2a") + ";");
            writer.println("  --steps-timeline-marker-border: " + getOrDefault(config, c -> c.darkStepsTimelineMarkerBorder(), "rgba(255, 255, 255, 0.14)") + ";");
            writer.println("  --steps-timeline-marker-active-border: " + getOrDefault(config, c -> c.darkStepsTimelineMarkerActiveBorder(), "var(--main-theme-color, #e3a002)") + ";");
            writer.println("  --steps-timeline-marker-glow: " + getOrDefault(config, c -> c.darkStepsTimelineMarkerGlow(), "rgba(227, 160, 2, 0.18)") + ";");
            writer.println("  --steps-timeline-meta-bg: " + getOrDefault(config, c -> c.darkStepsTimelineMetaBg(), "rgba(74, 222, 128, 0.13)") + ";");
            writer.println("  --steps-timeline-meta-text: " + getOrDefault(config, c -> c.darkStepsTimelineMetaText(), "#4ade80") + ";");
            writer.println("  --steps-timeline-code-bg: " + getOrDefault(config, c -> c.darkStepsTimelineCodeBg(), "rgba(255, 255, 255, 0.08)") + ";");
        } else {
            writer.println("  --steps-timeline-bg: " + getOrDefault(config, c -> c.lightStepsTimelineBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --steps-timeline-muted-bg: " + getOrDefault(config, c -> c.lightStepsTimelineMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --steps-timeline-rail-bg: " + getOrDefault(config, c -> c.lightStepsTimelineRailBg(), "rgba(0, 0, 0, 0.1)") + ";");
            writer.println("  --steps-timeline-rail-fill: " + getOrDefault(config, c -> c.lightStepsTimelineRailFill(), "var(--main-theme-color, #42f4fd)") + ";");
            writer.println("  --steps-timeline-marker-bg: " + getOrDefault(config, c -> c.lightStepsTimelineMarkerBg(), "#ffffff") + ";");
            writer.println("  --steps-timeline-marker-border: " + getOrDefault(config, c -> c.lightStepsTimelineMarkerBorder(), "rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --steps-timeline-marker-active-border: " + getOrDefault(config, c -> c.lightStepsTimelineMarkerActiveBorder(), "var(--main-theme-color, #42f4fd)") + ";");
            writer.println("  --steps-timeline-marker-glow: " + getOrDefault(config, c -> c.lightStepsTimelineMarkerGlow(), "rgba(66, 244, 253, 0.2)") + ";");
            writer.println("  --steps-timeline-meta-bg: " + getOrDefault(config, c -> c.lightStepsTimelineMetaBg(), "rgba(5, 150, 105, 0.1)") + ";");
            writer.println("  --steps-timeline-meta-text: " + getOrDefault(config, c -> c.lightStepsTimelineMetaText(), "#047857") + ";");
            writer.println("  --steps-timeline-code-bg: " + getOrDefault(config, c -> c.lightStepsTimelineCodeBg(), "rgba(0, 0, 0, 0.06)") + ";");
        }
    }

    private void writeFlowDiagramTheme(PrintWriter writer, FlowDiagramThemeConfig config, boolean dark) {
        writer.println("  /* FlowDiagram */");
        if (dark) {
            writer.println("  --flow-diagram-bg: " + getOrDefault(config, c -> c.darkFlowDiagramBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --flow-diagram-muted-bg: " + getOrDefault(config, c -> c.darkFlowDiagramMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --flow-diagram-node-bg: " + getOrDefault(config, c -> c.darkFlowDiagramNodeBg(), "#2a2a2a") + ";");
            writer.println("  --flow-diagram-node-border: " + getOrDefault(config, c -> c.darkFlowDiagramNodeBorder(), "rgba(255, 255, 255, 0.1)") + ";");
            writer.println("  --flow-diagram-node-hover-border: " + getOrDefault(config, c -> c.darkFlowDiagramNodeHoverBorder(), "var(--main-theme-color, #e3a002)") + ";");
            writer.println("  --flow-diagram-node-shadow: " + getOrDefault(config, c -> c.darkFlowDiagramNodeShadow(), "0 6px 22px rgba(0, 0, 0, 0.3)") + ";");
            writer.println("  --flow-diagram-node-shadow-hover: " + getOrDefault(config, c -> c.darkFlowDiagramNodeShadowHover(), "0 14px 36px rgba(0, 0, 0, 0.4)") + ";");
            writer.println("  --flow-diagram-link-color: " + getOrDefault(config, c -> c.darkFlowDiagramLinkColor(), "rgba(255, 255, 255, 0.16)") + ";");
            writer.println("  --flow-diagram-pulse-color: " + getOrDefault(config, c -> c.darkFlowDiagramPulseColor(), "var(--main-theme-color, #e3a002)") + ";");
            writer.println("  --flow-diagram-tag-bg: " + getOrDefault(config, c -> c.darkFlowDiagramTagBg(), "rgba(74, 222, 128, 0.13)") + ";");
            writer.println("  --flow-diagram-tag-text: " + getOrDefault(config, c -> c.darkFlowDiagramTagText(), "#4ade80") + ";");
        } else {
            writer.println("  --flow-diagram-bg: " + getOrDefault(config, c -> c.lightFlowDiagramBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --flow-diagram-muted-bg: " + getOrDefault(config, c -> c.lightFlowDiagramMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --flow-diagram-node-bg: " + getOrDefault(config, c -> c.lightFlowDiagramNodeBg(), "#ffffff") + ";");
            writer.println("  --flow-diagram-node-border: " + getOrDefault(config, c -> c.lightFlowDiagramNodeBorder(), "rgba(0, 0, 0, 0.09)") + ";");
            writer.println("  --flow-diagram-node-hover-border: " + getOrDefault(config, c -> c.lightFlowDiagramNodeHoverBorder(), "var(--main-theme-color, #42f4fd)") + ";");
            writer.println("  --flow-diagram-node-shadow: " + getOrDefault(config, c -> c.lightFlowDiagramNodeShadow(), "0 4px 18px rgba(0, 0, 0, 0.07)") + ";");
            writer.println("  --flow-diagram-node-shadow-hover: " + getOrDefault(config, c -> c.lightFlowDiagramNodeShadowHover(), "0 12px 32px rgba(0, 0, 0, 0.12)") + ";");
            writer.println("  --flow-diagram-link-color: " + getOrDefault(config, c -> c.lightFlowDiagramLinkColor(), "rgba(0, 0, 0, 0.16)") + ";");
            writer.println("  --flow-diagram-pulse-color: " + getOrDefault(config, c -> c.lightFlowDiagramPulseColor(), "var(--main-theme-color, #42f4fd)") + ";");
            writer.println("  --flow-diagram-tag-bg: " + getOrDefault(config, c -> c.lightFlowDiagramTagBg(), "rgba(5, 150, 105, 0.1)") + ";");
            writer.println("  --flow-diagram-tag-text: " + getOrDefault(config, c -> c.lightFlowDiagramTagText(), "#047857") + ";");
        }
    }

    private void writeScreenshotShowcaseTheme(PrintWriter writer, ScreenshotShowcaseThemeConfig config, boolean dark) {
        writer.println("  /* ScreenshotShowcase */");
        if (dark) {
            writer.println("  --screenshot-showcase-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseBg(), "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)") + ";");
            writer.println("  --screenshot-showcase-muted-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseMutedBg(), "var(--site-body-bg, #1e1e1e)") + ";");
            writer.println("  --screenshot-showcase-frame-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseFrameBg(), "#16161d") + ";");
            writer.println("  --screenshot-showcase-frame-border: " + getOrDefault(config, c -> c.darkScreenshotShowcaseFrameBorder(), "rgba(255, 255, 255, 0.12)") + ";");
            writer.println("  --screenshot-showcase-frame-shadow: " + getOrDefault(config, c -> c.darkScreenshotShowcaseFrameShadow(), "0 24px 70px rgba(0, 0, 0, 0.45)") + ";");
            writer.println("  --screenshot-showcase-frame-shadow-hover: " + getOrDefault(config, c -> c.darkScreenshotShowcaseFrameShadowHover(), "0 30px 90px rgba(0, 0, 0, 0.55)") + ";");
            writer.println("  --screenshot-showcase-chrome-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseChromeBg(), "rgba(255, 255, 255, 0.06)") + ";");
            writer.println("  --screenshot-showcase-dot-color: " + getOrDefault(config, c -> c.darkScreenshotShowcaseDotColor(), "rgba(255, 255, 255, 0.22)") + ";");
            writer.println("  --screenshot-showcase-url-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseUrlBg(), "rgba(0, 0, 0, 0.3)") + ";");
            writer.println("  --screenshot-showcase-url-text: " + getOrDefault(config, c -> c.darkScreenshotShowcaseUrlText(), "rgba(255, 255, 255, 0.6)") + ";");
            writer.println("  --screenshot-showcase-media-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseMediaBg(), "#0e0e14") + ";");
            writer.println("  --screenshot-showcase-badge-bg: " + getOrDefault(config, c -> c.darkScreenshotShowcaseBadgeBg(), "rgba(74, 222, 128, 0.13)") + ";");
            writer.println("  --screenshot-showcase-badge-text: " + getOrDefault(config, c -> c.darkScreenshotShowcaseBadgeText(), "#4ade80") + ";");
        } else {
            writer.println("  --screenshot-showcase-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseBg(), "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)") + ";");
            writer.println("  --screenshot-showcase-muted-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseMutedBg(), "var(--site-body-bg, #ffffff)") + ";");
            writer.println("  --screenshot-showcase-frame-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseFrameBg(), "#ffffff") + ";");
            writer.println("  --screenshot-showcase-frame-border: " + getOrDefault(config, c -> c.lightScreenshotShowcaseFrameBorder(), "rgba(0, 0, 0, 0.1)") + ";");
            writer.println("  --screenshot-showcase-frame-shadow: " + getOrDefault(config, c -> c.lightScreenshotShowcaseFrameShadow(), "0 18px 50px rgba(0, 0, 0, 0.13)") + ";");
            writer.println("  --screenshot-showcase-frame-shadow-hover: " + getOrDefault(config, c -> c.lightScreenshotShowcaseFrameShadowHover(), "0 26px 70px rgba(0, 0, 0, 0.18)") + ";");
            writer.println("  --screenshot-showcase-chrome-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseChromeBg(), "rgba(0, 0, 0, 0.04)") + ";");
            writer.println("  --screenshot-showcase-dot-color: " + getOrDefault(config, c -> c.lightScreenshotShowcaseDotColor(), "rgba(0, 0, 0, 0.16)") + ";");
            writer.println("  --screenshot-showcase-url-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseUrlBg(), "rgba(0, 0, 0, 0.05)") + ";");
            writer.println("  --screenshot-showcase-url-text: " + getOrDefault(config, c -> c.lightScreenshotShowcaseUrlText(), "rgba(0, 0, 0, 0.55)") + ";");
            writer.println("  --screenshot-showcase-media-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseMediaBg(), "#f3f4f6") + ";");
            writer.println("  --screenshot-showcase-badge-bg: " + getOrDefault(config, c -> c.lightScreenshotShowcaseBadgeBg(), "rgba(5, 150, 105, 0.1)") + ";");
            writer.println("  --screenshot-showcase-badge-text: " + getOrDefault(config, c -> c.lightScreenshotShowcaseBadgeText(), "#047857") + ";");
        }
    }

    private void writeLeadCarouselTheme(PrintWriter writer, LeadCarouselThemeConfig config, boolean dark) {
        writer.println("  /* Lead Carousel */");
        if (dark) {
            writer.println("  --lead-carousel-bg: " + getOrDefault(config, c -> c.darkLeadCarouselBg(), "#111111") + ";");
            writer.println("  --lead-carousel-text: " + getOrDefault(config, c -> c.darkLeadCarouselText(), "#ffffff") + ";");
            writer.println("  --lead-carousel-text-muted: " + getOrDefault(config, c -> c.darkLeadCarouselTextMuted(), "rgba(255, 255, 255, 0.92)") + ";");
            writer.println("  --lead-carousel-promo-text: " + getOrDefault(config, c -> c.darkLeadCarouselPromoText(), "#ffffff") + ";");
            writer.println("  --lead-carousel-cta-bg: " + getOrDefault(config, c -> c.darkLeadCarouselCtaBg(), "#ffffff") + ";");
            writer.println("  --lead-carousel-cta-text: " + getOrDefault(config, c -> c.darkLeadCarouselCtaText(), "#111111") + ";");
            writer.println("  --lead-carousel-cta-hover-bg: " + getOrDefault(config, c -> c.darkLeadCarouselCtaHoverBg(), "#e8e8e8") + ";");
            writer.println("  --lead-carousel-controls: " + getOrDefault(config, c -> c.darkLeadCarouselControls(), "#ffffff") + ";");
            writer.println("  --lead-carousel-dot: " + getOrDefault(config, c -> c.darkLeadCarouselDot(), "rgba(255, 255, 255, 0.45)") + ";");
            writer.println("  --lead-carousel-dot-track: " + getOrDefault(config, c -> c.darkLeadCarouselDotTrack(), "rgba(255, 255, 255, 0.28)") + ";");
            writer.println("  --lead-carousel-dot-fill: " + getOrDefault(config, c -> c.darkLeadCarouselDotFill(), "#ffffff") + ";");
        } else {
            writer.println("  --lead-carousel-bg: " + getOrDefault(config, c -> c.lightLeadCarouselBg(), "#111111") + ";");
            writer.println("  --lead-carousel-text: " + getOrDefault(config, c -> c.lightLeadCarouselText(), "#ffffff") + ";");
            writer.println("  --lead-carousel-text-muted: " + getOrDefault(config, c -> c.lightLeadCarouselTextMuted(), "rgba(255, 255, 255, 0.92)") + ";");
            writer.println("  --lead-carousel-promo-text: " + getOrDefault(config, c -> c.lightLeadCarouselPromoText(), "#ffffff") + ";");
            writer.println("  --lead-carousel-cta-bg: " + getOrDefault(config, c -> c.lightLeadCarouselCtaBg(), "#ffffff") + ";");
            writer.println("  --lead-carousel-cta-text: " + getOrDefault(config, c -> c.lightLeadCarouselCtaText(), "#111111") + ";");
            writer.println("  --lead-carousel-cta-hover-bg: " + getOrDefault(config, c -> c.lightLeadCarouselCtaHoverBg(), "#e8e8e8") + ";");
            writer.println("  --lead-carousel-controls: " + getOrDefault(config, c -> c.lightLeadCarouselControls(), "#ffffff") + ";");
            writer.println("  --lead-carousel-dot: " + getOrDefault(config, c -> c.lightLeadCarouselDot(), "rgba(255, 255, 255, 0.45)") + ";");
            writer.println("  --lead-carousel-dot-track: " + getOrDefault(config, c -> c.lightLeadCarouselDotTrack(), "rgba(255, 255, 255, 0.28)") + ";");
            writer.println("  --lead-carousel-dot-fill: " + getOrDefault(config, c -> c.lightLeadCarouselDotFill(), "#ffffff") + ";");
        }
    }

    private <T> String getOrDefault(T config, Function<T, String> getter, String defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        try {
            String value = getter.apply(config);
            return (value != null && !value.isEmpty()) ? value : defaultValue;
        } catch (Exception e) {
            LOG.debug("Error getting config value, using default", e);
            return defaultValue;
        }
    }

    private static final class ThemeConfigs {
        private final SiteThemeGlobalConfig globalConfig;
        private final HeaderThemeConfig headerConfig;
        private final FooterThemeConfig footerConfig;
        private final ServicesThemeConfig servicesConfig;
        private final ButtonThemeConfig buttonConfig;
        private final CtaPillThemeConfig ctaPillConfig;
        private final LeadBannerThemeConfig leadBannerConfig;
        private final ArticleTileThemeConfig articleTileConfig;
        private final HeaderOverlayThemeConfig headerOverlayConfig;
        private final SiteBannerThemeConfig siteBannerConfig;
        private final QuoteThemeConfig quoteConfig;
        private final LoopingCircleGalleryThemeConfig loopingCircleGalleryConfig;
        private final CountUpThemeConfig countUpConfig;
        private final SubscriptionPlansThemeConfig subscriptionPlansConfig;
        private final CompareSubscriptionThemeConfig compareSubscriptionConfig;
        private final RatingThemeConfig ratingConfig;
        private final MarqueeCarouselThemeConfig marqueeCarouselConfig;
        private final MasonryGalleryThemeConfig masonryGalleryConfig;
        private final ComparisonThemeConfig comparisonConfig;
        private final FaqThemeConfig faqConfig;
        private final HeroThemeConfig heroConfig;
        private final BlobImageSectionThemeConfig blobImageSectionConfig;
        private final LeadMediaSectionThemeConfig leadMediaSectionConfig;
        private final GridControlThemeConfig gridControlConfig;
        private final FormBuilderThemeConfig formBuilderConfig;
        private final VideoArticleGridThemeConfig videoArticleGridConfig;
        private final CardsThemeConfig cardsConfig;
        private final CallToActionThemeConfig callToActionConfig;
        private final DataTableThemeConfig dataTableConfig;
        private final AnalyticsChartThemeConfig analyticsChartConfig;
        private final MetricTilesThemeConfig metricTilesConfig;
        private final CodeSnippetThemeConfig codeSnippetConfig;
        private final StepsTimelineThemeConfig stepsTimelineConfig;
        private final FlowDiagramThemeConfig flowDiagramConfig;
        private final ScreenshotShowcaseThemeConfig screenshotShowcaseConfig;
        private final LeadCarouselThemeConfig leadCarouselConfig;

        private ThemeConfigs(
                SiteThemeGlobalConfig globalConfig,
                HeaderThemeConfig headerConfig,
                FooterThemeConfig footerConfig,
                ServicesThemeConfig servicesConfig,
                ButtonThemeConfig buttonConfig,
                CtaPillThemeConfig ctaPillConfig,
                LeadBannerThemeConfig leadBannerConfig,
                ArticleTileThemeConfig articleTileConfig,
                HeaderOverlayThemeConfig headerOverlayConfig,
                SiteBannerThemeConfig siteBannerConfig,
                QuoteThemeConfig quoteConfig,
                LoopingCircleGalleryThemeConfig loopingCircleGalleryConfig,
                CountUpThemeConfig countUpConfig,
                SubscriptionPlansThemeConfig subscriptionPlansConfig,
                CompareSubscriptionThemeConfig compareSubscriptionConfig,
                RatingThemeConfig ratingConfig,
                MarqueeCarouselThemeConfig marqueeCarouselConfig,
                MasonryGalleryThemeConfig masonryGalleryConfig,
                ComparisonThemeConfig comparisonConfig,
                FaqThemeConfig faqConfig,
                HeroThemeConfig heroConfig,
                BlobImageSectionThemeConfig blobImageSectionConfig,
                LeadMediaSectionThemeConfig leadMediaSectionConfig,
                GridControlThemeConfig gridControlConfig,
                FormBuilderThemeConfig formBuilderConfig,
                VideoArticleGridThemeConfig videoArticleGridConfig,
                CardsThemeConfig cardsConfig,
                CallToActionThemeConfig callToActionConfig,
                DataTableThemeConfig dataTableConfig,
                AnalyticsChartThemeConfig analyticsChartConfig,
                MetricTilesThemeConfig metricTilesConfig,
                CodeSnippetThemeConfig codeSnippetConfig,
                StepsTimelineThemeConfig stepsTimelineConfig,
                FlowDiagramThemeConfig flowDiagramConfig,
                ScreenshotShowcaseThemeConfig screenshotShowcaseConfig,
                LeadCarouselThemeConfig leadCarouselConfig
        ) {
            this.globalConfig = globalConfig;
            this.headerConfig = headerConfig;
            this.footerConfig = footerConfig;
            this.servicesConfig = servicesConfig;
            this.buttonConfig = buttonConfig;
            this.ctaPillConfig = ctaPillConfig;
            this.leadBannerConfig = leadBannerConfig;
            this.articleTileConfig = articleTileConfig;
            this.headerOverlayConfig = headerOverlayConfig;
            this.siteBannerConfig = siteBannerConfig;
            this.quoteConfig = quoteConfig;
            this.loopingCircleGalleryConfig = loopingCircleGalleryConfig;
            this.countUpConfig = countUpConfig;
            this.subscriptionPlansConfig = subscriptionPlansConfig;
            this.compareSubscriptionConfig = compareSubscriptionConfig;
            this.ratingConfig = ratingConfig;
            this.marqueeCarouselConfig = marqueeCarouselConfig;
            this.masonryGalleryConfig = masonryGalleryConfig;
            this.comparisonConfig = comparisonConfig;
            this.faqConfig = faqConfig;
            this.heroConfig = heroConfig;
            this.blobImageSectionConfig = blobImageSectionConfig;
            this.leadMediaSectionConfig = leadMediaSectionConfig;
            this.gridControlConfig = gridControlConfig;
            this.formBuilderConfig = formBuilderConfig;
            this.videoArticleGridConfig = videoArticleGridConfig;
            this.cardsConfig = cardsConfig;
            this.callToActionConfig = callToActionConfig;
            this.dataTableConfig = dataTableConfig;
            this.analyticsChartConfig = analyticsChartConfig;
            this.metricTilesConfig = metricTilesConfig;
            this.codeSnippetConfig = codeSnippetConfig;
            this.stepsTimelineConfig = stepsTimelineConfig;
            this.flowDiagramConfig = flowDiagramConfig;
            this.screenshotShowcaseConfig = screenshotShowcaseConfig;
            this.leadCarouselConfig = leadCarouselConfig;
        }

        private static ThemeConfigs empty() {
            return new ThemeConfigs(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}

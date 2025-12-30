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
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(config, c -> c.darkFooterBg(), "#363535") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(config, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(config, c -> c.darkServicesDividerColor(), "rgba(255, 255, 255, 0.12)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Dark theme */");
        writer.println("  --button-theme-dark-bg: " + getOrDefault(config, c -> c.darkButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-dark-text: " + getOrDefault(config, c -> c.darkButtonText(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-border: " + getOrDefault(config, c -> c.darkButtonBorder(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-bg: " + getOrDefault(config, c -> c.darkButtonHoverBg(), "#ffffff") + ";");
        writer.println("  --button-theme-dark-hover-text: " + getOrDefault(config, c -> c.darkButtonHoverText(), "#000000") + ";");
        writer.println();
        writer.println("  /* AboutUs */");
        writer.println("  --about-us-bg: " + getOrDefault(config, c -> c.darkAboutUsBg(), "#1e1e1e") + ";");
        writer.println();
        writer.println("  /* Lead Banner gradient variables - Dark theme */");
        writer.println("  --lead-banner-height: " + getOrDefault(config, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(config, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(config, c -> c.darkLeadBannerGradientStart(), "#212020") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(config, c -> c.darkLeadBannerGradientStop25(), "#aa7802") + ";");
        writer.println("  --lead-banner-gradient-stop-50: " + getOrDefault(config, c -> c.darkLeadBannerGradientStop50(), "#e3a002") + ";");
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
        writer.println("  --quote-card-glow: " + getOrDefault(config, c -> c.quoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(config, c -> c.darkLoopingCircleGalleryOverlayBg(), "rgba(255, 255, 255, 0.5)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: " + getOrDefault(config, c -> c.darkLoopingCircleGalleryOverlayText(), "#000000") + ";");
        writer.println("}");
        writer.println();
        
        // Light Theme
        writer.println(".theme-light {");
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
        writer.println();
        writer.println("  /* Footer */");
        writer.println("  --footer-bg: " + getOrDefault(config, c -> c.lightFooterBg(), "#f5f5f5") + ";");
        writer.println("  --footer-curtain-height-offset: " + getOrDefault(config, c -> c.footerCurtainHeightOffset(), "-25px") + ";");
        writer.println();
        writer.println("  /* Services */");
        writer.println("  --services-divider-color: " + getOrDefault(config, c -> c.lightServicesDividerColor(), "rgba(0, 0, 0, 0.12)") + ";");
        writer.println();
        writer.println("  /* Button theme variables - Light theme */");
        writer.println("  --button-theme-light-bg: " + getOrDefault(config, c -> c.lightButtonBg(), "transparent") + ";");
        writer.println("  --button-theme-light-text: " + getOrDefault(config, c -> c.lightButtonText(), "#000000") + ";");
        writer.println("  --button-theme-light-border: " + getOrDefault(config, c -> c.lightButtonBorder(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-bg: " + getOrDefault(config, c -> c.lightButtonHoverBg(), "#000000") + ";");
        writer.println("  --button-theme-light-hover-text: " + getOrDefault(config, c -> c.lightButtonHoverText(), "#ffffff") + ";");
        writer.println();
        writer.println("  /* AboutUs */");
        writer.println("  --about-us-bg: " + getOrDefault(config, c -> c.lightAboutUsBg(), "transparent") + ";");
        writer.println();
        writer.println("  /* Lead Banner gradient variables - Light theme */");
        writer.println("  --lead-banner-height: " + getOrDefault(config, c -> c.leadBannerHeight(), "600px") + ";");
        writer.println("  --lead-banner-height-mobile: " + getOrDefault(config, c -> c.leadBannerHeightMobile(), "460px") + ";");
        writer.println("  --lead-banner-gradient-start: " + getOrDefault(config, c -> c.lightLeadBannerGradientStart(), "#ffffff") + ";");
        writer.println("  --lead-banner-gradient-stop-25: " + getOrDefault(config, c -> c.lightLeadBannerGradientStop25(), "#b4e1f6") + ";");
        writer.println("  --lead-banner-gradient-stop-50: " + getOrDefault(config, c -> c.lightLeadBannerGradientStop50(), "#42c2fd") + ";");
        writer.println("  --lead-banner-gradient-stop-75: " + getOrDefault(config, c -> c.lightLeadBannerGradientStop75(), "#b4e1f6") + ";");
        writer.println("  --lead-banner-gradient-end: " + getOrDefault(config, c -> c.lightLeadBannerGradientEnd(), "#ffffff") + ";");
        writer.println("  --lead-banner-text-primary: " + getOrDefault(config, c -> c.lightLeadBannerTextPrimary(), "#323232") + ";");
        writer.println("  --lead-banner-text-secondary: " + getOrDefault(config, c -> c.lightLeadBannerTextSecondary(), "#6e6e6e") + ";");
        writer.println("  --lead-banner-secondary-text-color: " + getOrDefault(config, c -> c.lightLeadBannerSecondaryTextColor(), "#323232") + ";");
        writer.println("  --lead-banner-char-fade-duration: " + getOrDefault(config, c -> c.leadBannerCharFadeDuration(), "0.3s") + ";");
        writer.println();
        writer.println("  /* Article tiles (Header overlay) */");
        writer.println("  --article-tile-overlay-bg: " + getOrDefault(config, c -> c.lightArticleTileOverlayBg(), "#77d0fac7") + ";");
        writer.println();
        writer.println("  /* Header overlay */");
        writer.println("  --header-overlay-column-divider-color: " + getOrDefault(config, c -> c.lightHeaderOverlayColumnDividerColor(), "rgba(0, 0, 0, 0.18)") + ";");
        writer.println("  --header-overlay-hover-bg: " + getOrDefault(config, c -> c.lightHeaderOverlayHoverBg(), "rgba(0, 0, 0, 0.08)") + ";");
        writer.println();
        writer.println("  /* Site banner */");
        writer.println("  --site-banner-bg: " + getOrDefault(config, c -> c.lightSiteBannerBg(), "#9adcfa") + ";");
        writer.println("  --site-banner-text-color: var(--primary-text-color);");
        writer.println("  --site-banner-marquee-duration: " + getOrDefault(config, c -> c.lightSiteBannerMarqueeDuration(), "10s") + ";");
        writer.println("  --site-banner-cycle-duration: " + getOrDefault(config, c -> c.lightSiteBannerCycleDuration(), "20s") + ";");
        writer.println("  --site-banner-font-size: " + getOrDefault(config, c -> c.siteBannerFontSize(), "20px") + ";");
        writer.println();
        writer.println("  /* Quote */");
        String lightQuoteBg = getOrDefault(config, c -> c.lightQuoteBg(), "");
        writer.println("  --quote-bg: " + (lightQuoteBg.isEmpty() ? "var(--site-body-bg)" : lightQuoteBg) + ";");
        writer.println("  --quote-card-glow: " + getOrDefault(config, c -> c.quoteCardGlow(), 
                "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), " +
                "radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)") + ";");
        writer.println();
        writer.println("  /* LoopingCircleGallery overlay */");
        writer.println("  --looping-circle-gallery-overlay-bg: " + getOrDefault(config, c -> c.lightLoopingCircleGalleryOverlayBg(), "rgba(0, 0, 0, 0.5)") + ";");
        writer.println("  --looping-circle-gallery-overlay-text: " + getOrDefault(config, c -> c.lightLoopingCircleGalleryOverlayText(), "#ffffff") + ";");
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


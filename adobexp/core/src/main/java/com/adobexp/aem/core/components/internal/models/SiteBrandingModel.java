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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.ConfigurationResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.config.SiteBrandingConfig;

/**
 * Exposes the site's browser branding (favicon, touch icon, theme colour) to the page head.
 *
 * Usage in HTL:
 * {@code
 *   <sly data-sly-use.branding="com.adobexp.aem.core.components.internal.models.SiteBrandingModel"/>
 *   <link data-sly-test="${branding.hasFavicon}" rel="icon" href="${branding.faviconPath}">
 * }
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class SiteBrandingModel {

    private static final Logger LOG = LoggerFactory.getLogger(SiteBrandingModel.class);

    @SlingObject
    private Resource resource;

    @OSGiService
    private ConfigurationResolver configurationResolver;

    private String faviconPath = StringUtils.EMPTY;
    private String appleTouchIconPath = StringUtils.EMPTY;
    private String browserThemeColor = StringUtils.EMPTY;

    @PostConstruct
    protected void init() {
        if (configurationResolver == null || resource == null) {
            LOG.debug("ConfigurationResolver or Resource unavailable, site branding will not be emitted");
            return;
        }

        try {
            ConfigurationBuilder configBuilder = configurationResolver.get(resource);
            SiteBrandingConfig config = configBuilder.as(SiteBrandingConfig.class);
            if (config != null) {
                faviconPath = StringUtils.trimToEmpty(config.faviconPath());
                appleTouchIconPath = StringUtils.trimToEmpty(config.appleTouchIconPath());
                browserThemeColor = StringUtils.trimToEmpty(config.browserThemeColor());
            }
        } catch (Exception e) {
            LOG.warn("Unable to resolve SiteBrandingConfig for {}", resource.getPath(), e);
        }
    }

    public String getFaviconPath() {
        return faviconPath;
    }

    public boolean isHasFavicon() {
        return StringUtils.isNotBlank(faviconPath);
    }

    /**
     * The rel="icon" type attribute, derived from the file extension so the browser does not have
     * to sniff it.
     */
    public String getFaviconType() {
        return mimeTypeOf(faviconPath);
    }

    public String getAppleTouchIconPath() {
        return appleTouchIconPath;
    }

    public boolean isHasAppleTouchIcon() {
        return StringUtils.isNotBlank(appleTouchIconPath);
    }

    public String getBrowserThemeColor() {
        return browserThemeColor;
    }

    public boolean isHasBrowserThemeColor() {
        return StringUtils.isNotBlank(browserThemeColor);
    }

    private static String mimeTypeOf(final String path) {
        String extension = StringUtils.lowerCase(StringUtils.substringAfterLast(path, "."));
        switch (StringUtils.trimToEmpty(extension)) {
            case "svg":
                return "image/svg+xml";
            case "png":
                return "image/png";
            case "ico":
                return "image/x-icon";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return StringUtils.EMPTY;
        }
    }
}

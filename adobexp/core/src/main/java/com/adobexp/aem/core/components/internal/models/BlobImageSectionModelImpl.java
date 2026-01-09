/*
 *  Copyright 2024 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobexp.aem.core.components.internal.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.BlobImageSectionModel;

import javax.annotation.PostConstruct;

/**
 * Sling Model implementation for the Blob Image Section component.
 * Reads component configuration and determines asset type.
 * Supports theme selection, CTA elements, and overlay configurations.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = BlobImageSectionModel.class,
    resourceType = BlobImageSectionModelImpl.RESOURCE_TYPE
)
public class BlobImageSectionModelImpl implements BlobImageSectionModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/blob-image-section";

    // Content Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String badgeTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String mainTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String description;

    // Primary Media Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "image")
    private String mediaType;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryAssetPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryAssetAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryVideoPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryVideoAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String videoStartTime;

    // Icon Badge Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String iconBadgeImage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String iconBadgeAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section__icon-badge--bottom-left")
    private String iconBadgePosition;

    // Style Options
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section--shape-v1")
    private String shapePattern;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section--image-left")
    private String imageAlignment;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withCardBackground;

    // CTA Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "false")
    private String ctaExternal;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section__overlay-cta--bottom-left")
    private String ctaPosition;

    // Overlay Image Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String overlayImagePath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String overlayImageAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section__overlay-image--bottom-right")
    private String overlayImagePosition;

    // Overlay Card Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String overlayCardImagePath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String overlayCardTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String overlayCardSubtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "blob-image-section__overlay-card--bottom-right")
    private String overlayCardPosition;

    @PostConstruct
    protected void init() {
        // No initialization required - media type is determined by dialog selection
    }

    // ============================================
    // Content Fields Getters
    // ============================================

    @Override
    public String getBadgeTitle() {
        return badgeTitle;
    }

    @Override
    public String getMainTitle() {
        return mainTitle;
    }

    @Override
    public String getDescription() {
        return description;
    }

    // ============================================
    // Primary Media Fields Getters
    // ============================================

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public String getPrimaryAssetPath() {
        return primaryAssetPath;
    }

    @Override
    public String getPrimaryAssetAlt() {
        return primaryAssetAlt;
    }

    @Override
    public String getPrimaryVideoPath() {
        return primaryVideoPath;
    }

    @Override
    public String getPrimaryVideoAlt() {
        return primaryVideoAlt;
    }

    @Override
    public boolean isVideo() {
        return "video".equals(mediaType);
    }

    @Override
    public String getEffectiveMediaPath() {
        if (isVideo()) {
            return StringUtils.isNotBlank(primaryVideoPath) ? primaryVideoPath : primaryAssetPath;
        }
        return primaryAssetPath;
    }

    @Override
    public String getEffectiveMediaAlt() {
        if (isVideo()) {
            return StringUtils.isNotBlank(primaryVideoAlt) ? primaryVideoAlt : primaryAssetAlt;
        }
        return primaryAssetAlt;
    }

    @Override
    public String getVideoStartTime() {
        return videoStartTime;
    }

    // ============================================
    // Icon Badge Fields Getters
    // ============================================

    @Override
    public String getIconBadgeImage() {
        return iconBadgeImage;
    }

    @Override
    public String getIconBadgeAlt() {
        return iconBadgeAlt;
    }

    @Override
    public String getIconBadgePosition() {
        return iconBadgePosition;
    }

    @Override
    public boolean hasIconBadge() {
        return StringUtils.isNotBlank(iconBadgeImage);
    }

    // ============================================
    // Style Options Getters
    // ============================================

    @Override
    public String getShapePattern() {
        return shapePattern;
    }

    @Override
    public String getImageAlignment() {
        return imageAlignment;
    }

    @Override
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public boolean isWithCardBackground() {
        return "true".equals(withCardBackground);
    }

    // ============================================
    // CTA Fields Getters
    // ============================================

    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    @Override
    public String getCtaText() {
        return ctaText;
    }

    @Override
    public boolean isCtaExternal() {
        return "true".equals(ctaExternal);
    }

    @Override
    public boolean hasCtaLink() {
        return StringUtils.isNotBlank(ctaLink) && StringUtils.isNotBlank(ctaText);
    }

    @Override
    public String getCtaPosition() {
        return ctaPosition;
    }

    // ============================================
    // Overlay Image Fields Getters
    // ============================================

    @Override
    public String getOverlayImagePath() {
        return overlayImagePath;
    }

    @Override
    public String getOverlayImageAlt() {
        return overlayImageAlt;
    }

    @Override
    public String getOverlayImagePosition() {
        return overlayImagePosition;
    }

    @Override
    public boolean hasOverlayImage() {
        return StringUtils.isNotBlank(overlayImagePath);
    }

    // ============================================
    // Overlay Card Fields Getters
    // ============================================

    @Override
    public String getOverlayCardImagePath() {
        return overlayCardImagePath;
    }

    @Override
    public String getOverlayCardTitle() {
        return overlayCardTitle;
    }

    @Override
    public String getOverlayCardSubtitle() {
        return overlayCardSubtitle;
    }

    @Override
    public String getOverlayCardPosition() {
        return overlayCardPosition;
    }

    @Override
    public boolean hasOverlayCard() {
        return StringUtils.isNotBlank(overlayCardImagePath) && 
               (StringUtils.isNotBlank(overlayCardTitle) || StringUtils.isNotBlank(overlayCardSubtitle));
    }
}

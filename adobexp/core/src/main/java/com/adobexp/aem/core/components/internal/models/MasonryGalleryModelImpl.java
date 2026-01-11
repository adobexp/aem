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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.MasonryGalleryModel;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

/**
 * Sling Model implementation for the Masonry Gallery component.
 * Reads component configuration and builds gallery items from either
 * manual multifield entries or DAM folder contents.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = MasonryGalleryModel.class,
    resourceType = MasonryGalleryModelImpl.RESOURCE_TYPE
)
public class MasonryGalleryModelImpl implements MasonryGalleryModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/masonry-gallery";

    private static final String MEDIA_SOURCE_MANUAL = "manual";
    private static final String MEDIA_SOURCE_DAM_FOLDER = "damFolder";
    private static final String MEDIA_TYPE_VIDEO = "video";
    private static final String MEDIA_TYPE_IMAGE = "image";

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    // Media Source Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = MEDIA_SOURCE_MANUAL)
    private String mediaSourceType;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String damFolderPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String defaultVideoStartTime;

    // CTA Fields
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "false")
    private String ctaExternal;

    // Style Options
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String showTitleOverlay;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Gallery")
    private String ariaLabel;

    // Processed gallery items
    private List<GalleryItem> galleryItems;

    @PostConstruct
    protected void init() {
        galleryItems = new ArrayList<>();

        if (MEDIA_SOURCE_DAM_FOLDER.equals(mediaSourceType)) {
            loadItemsFromDamFolder();
        } else {
            loadItemsFromMultifield();
        }
    }

    /**
     * Load gallery items from DAM folder
     */
    private void loadItemsFromDamFolder() {
        if (StringUtils.isBlank(damFolderPath) || resourceResolver == null) {
            return;
        }

        Resource folderResource = resourceResolver.getResource(damFolderPath);
        if (folderResource == null) {
            return;
        }

        Iterator<Resource> children = folderResource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            Asset asset = child.adaptTo(Asset.class);
            
            if (asset != null) {
                String mimeType = asset.getMimeType();
                if (mimeType != null) {
                    // Get the asset title from metadata (dc:title), fallback to asset name
                    String assetTitle = getAssetTitle(asset);
                    String assetAlt = assetTitle; // Use same title for alt text
                    
                    if (mimeType.startsWith("image/")) {
                        galleryItems.add(new GalleryItemImpl(
                            MEDIA_TYPE_IMAGE,
                            child.getPath(),
                            assetTitle,
                            assetAlt,
                            null
                        ));
                    } else if (mimeType.startsWith("video/")) {
                        galleryItems.add(new GalleryItemImpl(
                            MEDIA_TYPE_VIDEO,
                            child.getPath(),
                            assetTitle,
                            assetAlt,
                            defaultVideoStartTime
                        ));
                    }
                }
            }
        }
    }

    /**
     * Get the asset title from DAM metadata.
     * Priority: dc:title from jcr:content/metadata node, then fallback to asset name.
     * 
     * @param asset The DAM asset
     * @return The asset title
     */
    private String getAssetTitle(Asset asset) {
        // First, try to get dc:title from metadata
        String dcTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
        if (StringUtils.isNotBlank(dcTitle)) {
            return dcTitle;
        }
        
        // Fallback to asset name (filename without extension would be cleaner,
        // but keeping full name for consistency)
        return asset.getName();
    }

    /**
     * Load gallery items from multifield
     */
    private void loadItemsFromMultifield() {
        Resource mediaItemsResource = resource.getChild("mediaItems");
        if (mediaItemsResource == null) {
            return;
        }

        Iterator<Resource> children = mediaItemsResource.listChildren();
        while (children.hasNext()) {
            Resource itemResource = children.next();
            ValueMap properties = itemResource.getValueMap();

            String mediaType = properties.get("mediaType", MEDIA_TYPE_IMAGE);
            String mediaPath = properties.get("mediaPath", String.class);
            String mediaTitle = properties.get("mediaTitle", String.class);
            String mediaAlt = properties.get("mediaAlt", String.class);
            String videoStartTime = properties.get("videoStartTime", String.class);

            if (StringUtils.isNotBlank(mediaPath)) {
                galleryItems.add(new GalleryItemImpl(
                    mediaType,
                    mediaPath,
                    mediaTitle,
                    mediaAlt,
                    videoStartTime
                ));
            }
        }
    }

    // ============================================
    // Media Source Getters
    // ============================================

    @Override
    public String getMediaSourceType() {
        return mediaSourceType;
    }

    @Override
    public String getDamFolderPath() {
        return damFolderPath;
    }

    @Override
    public String getDefaultVideoStartTime() {
        return defaultVideoStartTime;
    }

    @Override
    public List<GalleryItem> getGalleryItems() {
        return galleryItems;
    }

    @Override
    public boolean hasMediaItems() {
        return galleryItems != null && !galleryItems.isEmpty();
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

    // ============================================
    // Style Options Getters
    // ============================================

    @Override
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public boolean isShowTitleOverlay() {
        return "true".equals(showTitleOverlay);
    }

    @Override
    public String getAriaLabel() {
        return ariaLabel;
    }

    // ============================================
    // Inner class for Gallery Items
    // ============================================

    /**
     * Implementation of GalleryItem interface
     */
    public static class GalleryItemImpl implements GalleryItem {
        private final String mediaType;
        private final String mediaPath;
        private final String mediaTitle;
        private final String mediaAlt;
        private final String videoStartTime;

        public GalleryItemImpl(String mediaType, String mediaPath, String mediaTitle, 
                              String mediaAlt, String videoStartTime) {
            this.mediaType = mediaType;
            this.mediaPath = mediaPath;
            this.mediaTitle = mediaTitle;
            this.mediaAlt = mediaAlt;
            this.videoStartTime = videoStartTime;
        }

        @Override
        public String getMediaType() {
            return mediaType;
        }

        @Override
        public String getMediaPath() {
            return mediaPath;
        }

        @Override
        public String getMediaTitle() {
            return mediaTitle;
        }

        @Override
        public String getMediaAlt() {
            return mediaAlt;
        }

        @Override
        public String getVideoStartTime() {
            return videoStartTime;
        }

        @Override
        public boolean isVideo() {
            return MEDIA_TYPE_VIDEO.equals(mediaType);
        }
    }
}

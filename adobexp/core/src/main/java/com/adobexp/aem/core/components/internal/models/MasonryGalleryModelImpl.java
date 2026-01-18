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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(MasonryGalleryModelImpl.class);

    protected static final String RESOURCE_TYPE = "adobexp/components/content/masonry-gallery";

    private static final String MEDIA_SOURCE_MANUAL = "manual";
    private static final String MEDIA_SOURCE_DAM_FOLDER = "damFolder";
    private static final String MEDIA_TYPE_VIDEO = "video";
    private static final String MEDIA_TYPE_IMAGE = "image";
    
    // Sort By constants
    private static final String SORT_BY_LAST_MODIFIED = "lastModified";
    private static final String SORT_BY_PUBLISHED = "published";
    private static final String SORT_BY_SIZE = "size";
    private static final String SORT_BY_NAME = "name";
    
    // Sort Order constants
    private static final String SORT_ORDER_ASC = "ASC";
    private static final String SORT_ORDER_DESC = "DESC";

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

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "false")
    private String defaultVideoAutoplay;
    
    // DAM Folder Options
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Long assetCount;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String sortBy;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = SORT_ORDER_ASC)
    private String sortOrder;

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
     * Load gallery items from DAM folder with sorting and limiting support.
     */
    private void loadItemsFromDamFolder() {
        if (StringUtils.isBlank(damFolderPath) || resourceResolver == null) {
            return;
        }

        Resource folderResource = resourceResolver.getResource(damFolderPath);
        if (folderResource == null) {
            return;
        }

        // Temporary list to hold assets with metadata for sorting
        List<AssetWithMetadata> assetsWithMetadata = new ArrayList<>();

        Iterator<Resource> children = folderResource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            Asset asset = child.adaptTo(Asset.class);
            
            if (asset != null) {
                String mimeType = asset.getMimeType();
                if (mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"))) {
                    assetsWithMetadata.add(new AssetWithMetadata(asset, child));
                }
            }
        }
        
        // Sort assets if sortBy is specified and not empty
        if (StringUtils.isNotBlank(sortBy)) {
            sortAssets(assetsWithMetadata);
        }
        
        // Apply asset count limit
        int limit = getEffectiveAssetCount();
        int count = 0;
        
        for (AssetWithMetadata assetMeta : assetsWithMetadata) {
            // Check limit - if limit > 0, stop when reached
            if (limit > 0 && count >= limit) {
                break;
            }
            
            Asset asset = assetMeta.getAsset();
            Resource childResource = assetMeta.getResource();
            String mimeType = asset.getMimeType();
            
            // Get the asset title from metadata (dc:title), fallback to asset name
            String assetTitle = getAssetTitle(asset);
            String assetAlt = assetTitle; // Use same title for alt text
            
            if (mimeType.startsWith("image/")) {
                galleryItems.add(new GalleryItemImpl(
                    MEDIA_TYPE_IMAGE,
                    childResource.getPath(),
                    assetTitle,
                    assetAlt,
                    null,
                    false
                ));
                count++;
            } else if (mimeType.startsWith("video/")) {
                galleryItems.add(new GalleryItemImpl(
                    MEDIA_TYPE_VIDEO,
                    childResource.getPath(),
                    assetTitle,
                    assetAlt,
                    defaultVideoStartTime,
                    isDefaultVideoAutoplay()
                ));
                count++;
            }
        }
    }
    
    /**
     * Get the effective asset count limit.
     * Returns 0 if assetCount is null, 0, or negative (meaning show all assets).
     * Otherwise returns the specified count.
     * 
     * @return The number of assets to display, or 0 for unlimited
     */
    private int getEffectiveAssetCount() {
        if (assetCount == null || assetCount <= 0) {
            return 0; // 0 means no limit (show all)
        }
        return assetCount.intValue();
    }
    
    /**
     * Sort assets based on the sortBy and sortOrder configuration.
     * 
     * @param assets The list of assets to sort
     */
    private void sortAssets(List<AssetWithMetadata> assets) {
        Comparator<AssetWithMetadata> comparator = null;
        
        switch (sortBy) {
            case SORT_BY_LAST_MODIFIED:
                comparator = Comparator.comparing(
                    a -> a.getLastModified(),
                    Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
                
            case SORT_BY_PUBLISHED:
                comparator = Comparator.comparing(
                    a -> a.getPublishedDate(),
                    Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
                
            case SORT_BY_SIZE:
                comparator = Comparator.comparing(
                    a -> a.getSize(),
                    Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
                
            case SORT_BY_NAME:
                comparator = Comparator.comparing(
                    a -> a.getName().toLowerCase(),
                    Comparator.nullsLast(Comparator.naturalOrder())
                );
                break;
                
            default:
                // No sorting for empty or unknown sort type
                return;
        }
        
        // Apply descending order if specified
        if (SORT_ORDER_DESC.equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        
        assets.sort(comparator);
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
        // Wrapped in try-catch to handle assets with malformed/unknown XMP namespace prefixes
        try {
            String dcTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
            if (StringUtils.isNotBlank(dcTitle)) {
                return dcTitle;
            }
        } catch (Exception e) {
            LOG.warn("Failed to extract dc:title metadata from asset '{}': {}", 
                asset.getPath(), e.getMessage());
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
            String videoAutoplay = properties.get("videoAutoplay", String.class);

            if (StringUtils.isNotBlank(mediaPath)) {
                galleryItems.add(new GalleryItemImpl(
                    mediaType,
                    mediaPath,
                    mediaTitle,
                    mediaAlt,
                    videoStartTime,
                    "true".equals(videoAutoplay)
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
    public boolean isDefaultVideoAutoplay() {
        return "true".equals(defaultVideoAutoplay);
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
    // Inner class for Asset with Metadata (for sorting)
    // ============================================
    
    /**
     * Helper class to hold an asset along with its sortable metadata.
     */
    private static class AssetWithMetadata {
        private final Asset asset;
        private final Resource resource;
        private final Calendar lastModified;
        private final Calendar publishedDate;
        private final Long size;
        private final String name;
        
        public AssetWithMetadata(Asset asset, Resource resource) {
            this.asset = asset;
            this.resource = resource;
            this.name = asset.getName();
            
            // Get last modified date from jcr:content node
            long lastModifiedTimestamp = asset.getLastModified();
            if (lastModifiedTimestamp > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lastModifiedTimestamp);
                this.lastModified = cal;
            } else {
                this.lastModified = null;
            }
            
            // Get published date - try cq:lastReplicated first, then dc:date
            // Wrapped in try-catch to handle assets with malformed/unknown XMP namespace prefixes
            Calendar published = null;
            try {
                Object replicatedObj = asset.getMetadata("cq:lastReplicated");
                if (replicatedObj instanceof Calendar) {
                    published = (Calendar) replicatedObj;
                }
                if (published == null) {
                    Object dcDateObj = asset.getMetadata(DamConstants.DC_DATE);
                    if (dcDateObj instanceof Calendar) {
                        published = (Calendar) dcDateObj;
                    }
                }
            } catch (Exception e) {
                LOG.warn("Failed to extract published date metadata from asset '{}': {}", 
                    asset.getPath(), e.getMessage());
            }
            this.publishedDate = published;
            
            // Get file size from dam:size metadata or calculate from rendition
            // Wrapped in try-catch to handle assets with malformed/unknown XMP namespace prefixes
            Long assetSize = null;
            try {
                Object sizeObj = asset.getMetadata(DamConstants.DAM_SIZE);
                if (sizeObj instanceof Long) {
                    assetSize = (Long) sizeObj;
                } else if (sizeObj instanceof Number) {
                    assetSize = ((Number) sizeObj).longValue();
                }
            } catch (Exception e) {
                LOG.warn("Failed to extract size metadata from asset '{}': {}", 
                    asset.getPath(), e.getMessage());
            }
            this.size = assetSize;
        }
        
        public Asset getAsset() {
            return asset;
        }
        
        public Resource getResource() {
            return resource;
        }
        
        public Calendar getLastModified() {
            return lastModified;
        }
        
        public Calendar getPublishedDate() {
            return publishedDate;
        }
        
        public Long getSize() {
            return size;
        }
        
        public String getName() {
            return name;
        }
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
        private final boolean videoAutoplay;

        public GalleryItemImpl(String mediaType, String mediaPath, String mediaTitle, 
                              String mediaAlt, String videoStartTime, boolean videoAutoplay) {
            this.mediaType = mediaType;
            this.mediaPath = mediaPath;
            this.mediaTitle = mediaTitle;
            this.mediaAlt = mediaAlt;
            this.videoStartTime = videoStartTime;
            this.videoAutoplay = videoAutoplay;
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
        public boolean isVideoAutoplay() {
            return videoAutoplay;
        }

        @Override
        public boolean isVideo() {
            return MEDIA_TYPE_VIDEO.equals(mediaType);
        }
    }
}

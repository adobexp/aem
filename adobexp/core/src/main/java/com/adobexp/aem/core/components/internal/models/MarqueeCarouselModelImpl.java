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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.MarqueeCarouselModel;
import com.day.cq.dam.api.Asset;

/**
 * Sling Model implementation for the Marquee Carousel component.
 * Reads carousel images from either multifield (manual) or DAM folder based on configuration.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = MarqueeCarouselModel.class,
    resourceType = MarqueeCarouselModelImpl.RESOURCE_TYPE
)
public class MarqueeCarouselModelImpl implements MarqueeCarouselModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/marquee-carousel";
    
    private static final String CAROUSEL_IMAGES_NODE = "carouselImages";
    private static final String IMAGE_SOURCE_MANUAL = "manual";
    private static final String IMAGE_SOURCE_DAM_FOLDER = "damFolder";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String imageSource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String damFolderPath;

    private List<CarouselImage> carouselImages;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            carouselImages = loadCarouselImages(resource);
        } else {
            carouselImages = Collections.emptyList();
        }
    }

    private Resource getResource() {
        if (currentResource != null) {
            return currentResource;
        }
        if (request != null) {
            return request.getResource();
        }
        return null;
    }

    /**
     * Load carousel images based on image source configuration.
     */
    private List<CarouselImage> loadCarouselImages(Resource componentResource) {
        // Default to manual if not specified
        String source = StringUtils.isNotBlank(imageSource) ? imageSource : IMAGE_SOURCE_MANUAL;
        
        if (IMAGE_SOURCE_DAM_FOLDER.equals(source) && StringUtils.isNotBlank(damFolderPath)) {
            return loadImagesFromDamFolder(componentResource.getResourceResolver());
        } else {
            return loadManualImages(componentResource);
        }
    }

    /**
     * Load images from the DAM folder.
     */
    private List<CarouselImage> loadImagesFromDamFolder(ResourceResolver resolver) {
        List<CarouselImage> images = new ArrayList<>();
        
        if (resolver == null || StringUtils.isBlank(damFolderPath)) {
            return images;
        }
        
        Resource damFolder = resolver.getResource(damFolderPath);
        if (damFolder == null) {
            return images;
        }
        
        Iterator<Resource> children = damFolder.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            
            // Check if it's a DAM asset
            Asset asset = child.adaptTo(Asset.class);
            if (asset != null && isImageAsset(asset)) {
                String imagePath = child.getPath();
                String imageAlt = getAssetTitle(asset);
                images.add(new CarouselImageImpl(imagePath, imageAlt));
            }
        }
        
        return images;
    }

    /**
     * Check if the asset is an image.
     */
    private boolean isImageAsset(Asset asset) {
        String mimeType = asset.getMimeType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * Get the title of the asset for alt text.
     */
    private String getAssetTitle(Asset asset) {
        // Try to get dc:title metadata
        String title = asset.getMetadataValueFromJcr("dc:title");
        if (StringUtils.isNotBlank(title)) {
            return title;
        }
        // Fall back to asset name
        return asset.getName();
    }

    /**
     * Load manually added images from the multifield.
     */
    private List<CarouselImage> loadManualImages(Resource componentResource) {
        List<CarouselImage> images = new ArrayList<>();
        Resource carouselImagesResource = componentResource.getChild(CAROUSEL_IMAGES_NODE);
        
        if (carouselImagesResource != null) {
            for (Resource imageResource : carouselImagesResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (imageResource.getName().startsWith("jcr:")) {
                    continue;
                }
                CarouselImageImpl carouselImage = parseCarouselImage(imageResource);
                if (carouselImage != null) {
                    images.add(carouselImage);
                }
            }
        }
        
        return images;
    }

    /**
     * Parse a single carousel image resource.
     */
    private CarouselImageImpl parseCarouselImage(Resource imageResource) {
        ValueMap props = imageResource.getValueMap();
        
        String imagePath = props.get("imagePath", String.class);
        String imageAlt = props.get("imageAlt", String.class);
        
        if (StringUtils.isBlank(imagePath)) {
            return null;
        }
        
        return new CarouselImageImpl(imagePath, imageAlt);
    }

    // Getter implementations
    @Override
    public String getImageSource() {
        return StringUtils.isNotBlank(imageSource) ? imageSource : IMAGE_SOURCE_MANUAL;
    }

    @Override
    public String getDamFolderPath() {
        return damFolderPath;
    }

    @Override
    public List<CarouselImage> getCarouselImages() {
        return carouselImages;
    }

    @Override
    public boolean hasImages() {
        return carouselImages != null && !carouselImages.isEmpty();
    }

    /**
     * Implementation of CarouselImage interface.
     */
    public static class CarouselImageImpl implements CarouselImage {
        private final String imagePath;
        private final String imageAlt;

        public CarouselImageImpl(String imagePath, String imageAlt) {
            this.imagePath = imagePath;
            this.imageAlt = imageAlt;
        }

        @Override
        public String getImagePath() {
            return imagePath;
        }

        @Override
        public String getImageAlt() {
            return imageAlt;
        }
    }
}

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
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.LoopingCircleGalleryModel;

/**
 * Sling Model implementation for the Looping Circle Gallery component.
 * Reads gallery message and images configuration from the component resource.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = LoopingCircleGalleryModel.class,
    resourceType = LoopingCircleGalleryModelImpl.RESOURCE_TYPE
)
public class LoopingCircleGalleryModelImpl implements LoopingCircleGalleryModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/looping-circle-gallery";
    
    private static final String GALLERY_IMAGES_NODE = "galleryImages";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String galleryMessage;

    private List<GalleryImage> galleryImages;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            galleryImages = parseGalleryImages(resource);
        } else {
            galleryImages = Collections.emptyList();
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
     * Parse gallery images from the galleryImages child node.
     */
    private List<GalleryImage> parseGalleryImages(Resource componentResource) {
        List<GalleryImage> images = new ArrayList<>();
        Resource galleryImagesResource = componentResource.getChild(GALLERY_IMAGES_NODE);
        
        if (galleryImagesResource != null) {
            int index = 0;
            for (Resource imageResource : galleryImagesResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (imageResource.getName().startsWith("jcr:")) {
                    continue;
                }
                GalleryImageImpl galleryImage = parseGalleryImage(imageResource, index);
                if (galleryImage != null) {
                    images.add(galleryImage);
                    index++;
                }
            }
        }
        
        return images;
    }

    /**
     * Parse a single gallery image resource.
     */
    private GalleryImageImpl parseGalleryImage(Resource imageResource, int index) {
        ValueMap props = imageResource.getValueMap();
        
        String imagePath = props.get("imagePath", String.class);
        String imageAlt = props.get("imageAlt", String.class);
        
        if (StringUtils.isBlank(imagePath)) {
            return null;
        }
        
        return new GalleryImageImpl(imagePath, imageAlt, index);
    }

    // Getter implementations
    @Override
    public String getGalleryMessage() {
        return galleryMessage;
    }

    @Override
    public List<GalleryImage> getGalleryImages() {
        return galleryImages;
    }

    @Override
    public boolean hasImages() {
        return galleryImages != null && !galleryImages.isEmpty();
    }

    /**
     * Implementation of GalleryImage interface.
     */
    public static class GalleryImageImpl implements GalleryImage {
        private final String imagePath;
        private final String imageAlt;
        private final int index;

        public GalleryImageImpl(String imagePath, String imageAlt, int index) {
            this.imagePath = imagePath;
            this.imageAlt = imageAlt;
            this.index = index;
        }

        @Override
        public String getImagePath() {
            return imagePath;
        }

        @Override
        public String getImageAlt() {
            return imageAlt;
        }

        @Override
        public int getIndex() {
            return index;
        }
    }
}


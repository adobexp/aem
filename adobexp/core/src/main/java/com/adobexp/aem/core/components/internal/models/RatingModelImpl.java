/*~~~~* Copyright 2025 AdobeXP
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
 ~~~~~*/

package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.RatingModel;

/**
 * Sling Model implementation for the Rating component.
 * Reads nested multifield configurations for avatar images.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = RatingModel.class,
    resourceType = RatingModelImpl.RESOURCE_TYPE
)
public class RatingModelImpl implements RatingModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/rating";
    
    private static final String AVATAR_IMAGES_NODE = "images";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Boolean ctaLinkExternal;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Double starRating;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "5")
    private Integer starTotal;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Boolean applyBgColor;

    private List<AvatarImage> avatarImages;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            avatarImages = parseAvatarImages(resource);
        } else {
            avatarImages = Collections.emptyList();
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
     * Parse avatar images from the images child node.
     */
    private List<AvatarImage> parseAvatarImages(Resource componentResource) {
        List<AvatarImage> images = new ArrayList<>();
        Resource avatarImagesResource = componentResource.getChild(AVATAR_IMAGES_NODE);
        
        if (avatarImagesResource != null) {
            for (Resource imageResource : avatarImagesResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (imageResource.getName().startsWith("jcr:")) {
                    continue;
                }
                AvatarImageImpl avatarImage = parseAvatarImage(imageResource);
                if (avatarImage != null) {
                    images.add(avatarImage);
                }
            }
        }
        
        return images;
    }

    /**
     * Parse a single avatar image resource.
     */
    private AvatarImageImpl parseAvatarImage(Resource imageResource) {
        ValueMap props = imageResource.getValueMap();
        
        String imagePath = props.get("imagePath", String.class);
        String altText = props.get("altText", String.class);
        
        if (StringUtils.isBlank(imagePath)) {
            return null;
        }
        
        return new AvatarImageImpl(imagePath, altText);
    }

    // Getter implementations
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public List<AvatarImage> getAvatarImages() {
        return avatarImages;
    }

    @Override
    public String getCtaText() {
        return ctaText;
    }

    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    @Override
    public boolean isCtaLinkExternal() {
        return ctaLinkExternal != null ? ctaLinkExternal : false;
    }

    @Override
    public double getStarRating() {
        return starRating != null ? starRating : 0.0;
    }

    @Override
    public int getStarTotal() {
        return starTotal != null ? starTotal : 5;
    }

    @Override
    public boolean isApplyBgColor() {
        return applyBgColor != null ? applyBgColor : false;
    }

    /**
     * Implementation of AvatarImage interface.
     */
    public static class AvatarImageImpl implements AvatarImage {
        private final String imagePath;
        private final String altText;

        public AvatarImageImpl(String imagePath, String altText) {
            this.imagePath = imagePath;
            this.altText = altText;
        }

        @Override
        public String getImagePath() {
            return imagePath;
        }

        @Override
        public String getAltText() {
            return altText;
        }
    }
}
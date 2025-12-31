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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.LeadBannerModel;
import com.google.gson.Gson;

/**
 * Sling Model implementation for the Lead Banner component.
 * Reads multifield configurations for secondary headline items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = LeadBannerModel.class,
    resourceType = LeadBannerModelImpl.RESOURCE_TYPE
)
public class LeadBannerModelImpl implements LeadBannerModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/leadbanner";
    
    private static final String SECONDARY_HEADLINE_ITEMS_NODE = "secondaryHeadlineItems";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryHeadline;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String secondaryText;

    private List<SecondaryHeadlineItem> secondaryHeadlineItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            secondaryHeadlineItems = parseSecondaryHeadlineItems(resource);
        } else {
            secondaryHeadlineItems = Collections.emptyList();
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
     * Parse secondary headline items from the secondaryHeadlineItems child node.
     */
    private List<SecondaryHeadlineItem> parseSecondaryHeadlineItems(Resource componentResource) {
        List<SecondaryHeadlineItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(SECONDARY_HEADLINE_ITEMS_NODE);
        
        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                SecondaryHeadlineItemImpl item = parseSecondaryHeadlineItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single secondary headline item resource.
     */
    private SecondaryHeadlineItemImpl parseSecondaryHeadlineItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String stackImage = props.get("stackImage", String.class);
        String stackImageAlt = props.get("stackImageAlt", String.class);
        String secondaryHeadlineText = props.get("secondaryHeadlineText", String.class);
        
        // At minimum, we need the secondary headline text
        if (StringUtils.isBlank(secondaryHeadlineText)) {
            return null;
        }
        
        return new SecondaryHeadlineItemImpl(stackImage, stackImageAlt, secondaryHeadlineText);
    }

    // Getter implementations
    @Override
    public String getPrimaryHeadline() {
        return primaryHeadline;
    }

    @Override
    public String getSecondaryText() {
        return secondaryText;
    }

    @Override
    public List<SecondaryHeadlineItem> getSecondaryHeadlineItems() {
        return secondaryHeadlineItems;
    }

    @Override
    public boolean hasSecondaryHeadlineItems() {
        return secondaryHeadlineItems != null && !secondaryHeadlineItems.isEmpty();
    }

    @Override
    public String getSecondaryHeadlineStringsJson() {
        if (!hasSecondaryHeadlineItems()) {
            return "[]";
        }
        
        List<String> texts = secondaryHeadlineItems.stream()
            .map(SecondaryHeadlineItem::getSecondaryHeadlineText)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        
        return new Gson().toJson(texts);
    }

    @Override
    public String getFirstSecondaryHeadlineText() {
        if (!hasSecondaryHeadlineItems()) {
            return "";
        }
        return secondaryHeadlineItems.get(0).getSecondaryHeadlineText();
    }

    /**
     * Implementation of SecondaryHeadlineItem interface.
     */
    public static class SecondaryHeadlineItemImpl implements SecondaryHeadlineItem {
        private final String stackImage;
        private final String stackImageAlt;
        private final String secondaryHeadlineText;

        public SecondaryHeadlineItemImpl(String stackImage, String stackImageAlt, String secondaryHeadlineText) {
            this.stackImage = stackImage;
            this.stackImageAlt = stackImageAlt;
            this.secondaryHeadlineText = secondaryHeadlineText;
        }

        @Override
        public String getStackImage() {
            return stackImage;
        }

        @Override
        public String getStackImageAlt() {
            return stackImageAlt;
        }

        @Override
        public String getSecondaryHeadlineText() {
            return secondaryHeadlineText;
        }
    }
}


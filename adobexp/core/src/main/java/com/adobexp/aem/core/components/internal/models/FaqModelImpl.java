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
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.FaqModel;

/**
 * Sling Model implementation for the FAQ component.
 * Reads multifield configurations for FAQ items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = FaqModel.class,
    resourceType = FaqModelImpl.RESOURCE_TYPE
)
public class FaqModelImpl implements FaqModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/faq";
    
    private static final String FAQ_ITEMS_NODE = "faqItems";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String faqTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = true)
    private boolean withBackground;

    private List<FaqItem> faqItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            faqItems = parseFaqItems(resource);
        } else {
            faqItems = Collections.emptyList();
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
     * Parse FAQ items from the faqItems child node.
     */
    private List<FaqItem> parseFaqItems(Resource componentResource) {
        List<FaqItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(FAQ_ITEMS_NODE);
        
        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                FaqItemImpl item = parseFaqItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single FAQ item resource.
     */
    private FaqItemImpl parseFaqItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String faqQuestion = props.get("faqQuestion", String.class);
        String faqAnswer = props.get("faqAnswer", String.class);
        String faqExpanded = props.get("faqExpanded", String.class);
        boolean expanded = "true".equals(faqExpanded);
        
        // At minimum, we need both question and answer
        if (StringUtils.isBlank(faqQuestion) || StringUtils.isBlank(faqAnswer)) {
            return null;
        }
        
        return new FaqItemImpl(faqQuestion, faqAnswer, expanded);
    }

    // Getter implementations
    @Override
    public String getFaqTitle() {
        return faqTitle;
    }

    @Override
    public List<FaqItem> getFaqItems() {
        return faqItems;
    }

    @Override
    public boolean hasFaqItems() {
        return faqItems != null && !faqItems.isEmpty();
    }

    @Override
    public boolean isWithBackground() {
        return withBackground;
    }

    /**
     * Implementation of FaqItem interface.
     */
    public static class FaqItemImpl implements FaqItem {
        private final String question;
        private final String answer;
        private final boolean expanded;

        public FaqItemImpl(String question, String answer, boolean expanded) {
            this.question = question;
            this.answer = answer;
            this.expanded = expanded;
        }

        @Override
        public String getQuestion() {
            return question;
        }

        @Override
        public String getAnswer() {
            return answer;
        }

        @Override
        public boolean isExpanded() {
            return expanded;
        }
    }
}

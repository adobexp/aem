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

import com.adobexp.aem.core.components.models.SiteBannerModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Sling Model implementation for the Site Banner component.
 * Reads multifield configuration for banner messages.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = SiteBannerModel.class,
    resourceType = SiteBannerModelImpl.RESOURCE_TYPE
)
public class SiteBannerModelImpl implements SiteBannerModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/global/sitebanner";
    
    private static final String BANNER_MESSAGES_NODE = "bannerMessages";
    private static final int DEFAULT_CYCLE_DURATION = 10;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = DEFAULT_CYCLE_DURATION)
    private int cycleDuration;

    private List<BannerMessage> messages;
    private String messagesJson;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            messages = parseBannerMessages(resource);
        } else {
            messages = Collections.emptyList();
        }
        
        // Generate JSON for data attribute
        messagesJson = generateMessagesJson();
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
     * Parse banner messages from the bannerMessages child node.
     */
    private List<BannerMessage> parseBannerMessages(Resource componentResource) {
        List<BannerMessage> items = new ArrayList<>();
        Resource bannerMessagesResource = componentResource.getChild(BANNER_MESSAGES_NODE);
        
        if (bannerMessagesResource != null) {
            for (Resource itemResource : bannerMessagesResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                BannerMessageImpl bannerMessage = parseBannerMessage(itemResource);
                if (bannerMessage != null) {
                    items.add(bannerMessage);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single banner message resource.
     */
    private BannerMessageImpl parseBannerMessage(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String messageText = props.get("messageText", String.class);
        
        if (StringUtils.isBlank(messageText)) {
            return null;
        }
        
        return new BannerMessageImpl(messageText);
    }

    /**
     * Generate JSON string of message texts for data attribute.
     */
    private String generateMessagesJson() {
        if (messages == null || messages.isEmpty()) {
            return "[]";
        }
        
        List<String> messageTexts = new ArrayList<>();
        for (BannerMessage message : messages) {
            if (message.getMessageText() != null) {
                messageTexts.add(message.getMessageText());
            }
        }
        
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(messageTexts);
    }

    // Getter implementations
    @Override
    public List<BannerMessage> getMessages() {
        return messages;
    }

    @Override
    public String getMessagesJson() {
        return messagesJson;
    }

    @Override
    public int getCycleDuration() {
        return cycleDuration > 0 ? cycleDuration : DEFAULT_CYCLE_DURATION;
    }

    @Override
    public boolean hasMessages() {
        return messages != null && !messages.isEmpty();
    }

    // Inner class for banner message

    /**
     * Implementation of BannerMessage interface.
     */
    public static class BannerMessageImpl implements BannerMessage {
        private final String messageText;

        public BannerMessageImpl(String messageText) {
            this.messageText = messageText;
        }

        @Override
        public String getMessageText() {
            return messageText;
        }
    }
}


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
package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Site Banner component.
 * Provides access to banner configuration including messages and cycle duration.
 */
public interface SiteBannerModel {

    /**
     * Gets the list of banner messages.
     * @return list of banner messages
     */
    List<BannerMessage> getMessages();

    /**
     * Gets the messages as a JSON string for use in data attributes.
     * @return JSON string of messages
     */
    String getMessagesJson();

    /**
     * Gets the cycle duration in seconds.
     * @return cycle duration in seconds
     */
    int getCycleDuration();

    /**
     * Checks if any messages are configured.
     * @return true if any messages are configured
     */
    boolean hasMessages();

    /**
     * Represents a single banner message.
     */
    interface BannerMessage {
        /**
         * Gets the message text.
         * @return message text (may contain HTML)
         */
        String getMessageText();
    }
}


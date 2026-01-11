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
 * Sling Model interface for the FAQ component.
 * Provides access to FAQ configuration including title and FAQ items.
 */
public interface FaqModel {

    /**
     * Gets the FAQ section title (H2).
     * @return FAQ title
     */
    String getFaqTitle();

    /**
     * Gets the list of FAQ items.
     * @return list of FAQ items
     */
    List<FaqItem> getFaqItems();

    /**
     * Checks if FAQ items are configured.
     * @return true if any FAQ item is configured
     */
    boolean hasFaqItems();

    /**
     * Checks if background should be applied.
     * @return true if background should be applied
     */
    boolean isWithBackground();

    /**
     * Represents a FAQ item with question, answer, and expanded state.
     */
    interface FaqItem {
        /**
         * Gets the FAQ question text.
         * @return FAQ question
         */
        String getQuestion();

        /**
         * Gets the FAQ answer text.
         * @return FAQ answer
         */
        String getAnswer();

        /**
         * Checks if this item should be expanded by default.
         * @return true if expanded by default
         */
        boolean isExpanded();
    }
}

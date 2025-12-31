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
 * Sling Model interface for the Lead Banner component.
 * Provides access to lead banner configuration including headlines, images, and text.
 */
public interface LeadBannerModel {

    /**
     * Gets the primary headline text.
     * @return primary headline
     */
    String getPrimaryHeadline();

    /**
     * Gets the secondary text (paragraph content).
     * @return secondary text
     */
    String getSecondaryText();

    /**
     * Gets the list of secondary headline items.
     * @return list of secondary headline items
     */
    List<SecondaryHeadlineItem> getSecondaryHeadlineItems();

    /**
     * Checks if secondary headline items are configured.
     * @return true if any secondary headline item is configured
     */
    boolean hasSecondaryHeadlineItems();

    /**
     * Gets the secondary headline texts as a JSON array string for the data-strings attribute.
     * @return JSON array string of secondary headline texts
     */
    String getSecondaryHeadlineStringsJson();

    /**
     * Gets the first secondary headline text for initial display.
     * @return first secondary headline text or empty string
     */
    String getFirstSecondaryHeadlineText();

    /**
     * Represents a secondary headline item with image and text.
     */
    interface SecondaryHeadlineItem {
        /**
         * Gets the stack image path.
         * @return stack image path
         */
        String getStackImage();

        /**
         * Gets the stack image alt text.
         * @return stack image alt text
         */
        String getStackImageAlt();

        /**
         * Gets the secondary headline text.
         * @return secondary headline text
         */
        String getSecondaryHeadlineText();
    }
}


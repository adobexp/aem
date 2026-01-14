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

package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Count Up component.
 * Provides access to count up configuration including title, subtitle, and counter items.
 */
public interface CountUpModel {

    /**
     * Gets the section title.
     * @return title
     */
    String getTitle();

    /**
     * Gets the section subtitle.
     * @return subtitle
     */
    String getSubtitle();

    /**
     * Gets the animation duration in milliseconds.
     * @return duration
     */
    String getDuration();

    /**
     * Gets whether the component should have a background.
     * @return withBackground
     */
    boolean isWithBackground();

    /**
     * Gets the list of counter items.
     * @return list of counter items
     */
    List<CounterItem> getCounterItems();

    /**
     * Checks if counter items are configured.
     * @return true if any counter item is configured
     */
    boolean hasCounterItems();

    /**
     * Represents a counter item with value, unit, and label.
     */
    interface CounterItem {
        /**
         * Gets the start value for the counter animation.
         * @return start value
         */
        String getStartValue();

        /**
         * Gets the end value for the counter animation.
         * @return end value
         */
        String getEndValue();

        /**
         * Gets the unit to display after the value (M, K, B, %, +, or custom).
         * @return unit
         */
        String getUnit();

        /**
         * Gets the number of decimal places to display.
         * @return decimals
         */
        String getDecimals();

        /**
         * Gets the label text displayed below the counter value.
         * @return label
         */
        String getLabel();
    }
}

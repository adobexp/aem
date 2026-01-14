/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 AdobeXP
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
 * Sling Model interface for the Compare Subscription component.
 */
public interface CompareSubscriptionModel {

    /**
     * Gets the component title.
     * @return title
     */
    String getTitle();

    /**
     * Gets the component description.
     * @return description
     */
    String getDescription();

    /**
     * Gets the currency symbol.
     * @return currency symbol
     */
    String getCurrencySymbol();

    /**
     * Gets whether the component should have a background.
     * @return with background
     */
    boolean isWithBackground();

    /**
     * Gets the list of plan configurations.
     * @return plans
     */
    List<Plan> getPlans();

    /**
     * Gets the ordered list of feature titles used by the table.
     * @return feature titles
     */
    List<String> getFeatureTitles();

    /**
     * Checks if any plan has content.
     * @return true when at least one plan is configured
     */
    boolean hasPlans();

    /**
     * Represents a plan column configuration.
     */
    interface Plan {
        String getKey();
        String getName();
        String getMonthlyPrice();
        boolean isMostPopular();
        boolean isBestValue();
        boolean isHighlighted();
        List<FeatureValue> getFeatureValues();
        boolean hasContent();
    }

    /**
     * Represents a per-feature value in a plan column.
     */
    interface FeatureValue {
        String getTitle();
        boolean isIncluded();
    }
}

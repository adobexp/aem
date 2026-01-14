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
 * Sling Model interface for the Subscription Plans component.
 * Provides access to header content and plan configurations.
 */
public interface SubscriptionPlansModel {

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
     * Gets the default billing mode (monthly/yearly).
     * @return default billing
     */
    String getDefaultBilling();

    /**
     * Gets the yearly discount badge text.
     * @return yearly discount text
     */
    String getYearlyDiscountText();

    /**
     * Gets the currency symbol.
     * @return currency symbol
     */
    String getCurrencySymbol();

    /**
     * Gets the monthly billing note text.
     * @return monthly billing note
     */
    String getMonthlyBillingNote();

    /**
     * Gets the yearly billing note text.
     * @return yearly billing note
     */
    String getYearlyBillingNote();

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
     * Checks if any plan has content.
     * @return true when at least one plan is configured
     */
    boolean hasPlans();

    /**
     * Represents a plan card configuration.
     */
    interface Plan {
        String getName();
        String getMonthlyPrice();
        String getYearlyPrice();
        String getYearlyOriginalPrice();
        String getCtaText();
        String getCtaLink();
        boolean isCtaNewTab();
        boolean isTopChoice();
        boolean isSpecialOffer();
        String getDiscountUntil();
        String getCreditsText();
        List<String> getFeatures();
        boolean hasFeatures();
        boolean hasContent();
    }
}

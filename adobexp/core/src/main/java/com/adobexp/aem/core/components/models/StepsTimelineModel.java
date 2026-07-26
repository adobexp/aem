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
 * Sling Model interface for the Steps Timeline component.
 */
public interface StepsTimelineModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the section style variant CSS class, e.g. {@code steps-timeline--with-bg}
     * or {@code steps-timeline--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    /**
     * Returns the accessible label applied to the section element.
     */
    String getAriaLabel();

    List<Step> getSteps();

    boolean hasSteps();

    boolean hasContent();

    interface Step {

        String getStepTitle();

        String getDescription();

        /**
         * Returns the small chip text shown beside the step title, e.g. {@code 2 min}.
         */
        String getMeta();

        /**
         * Returns the number rendered inside the step marker. This is the authored
         * override when one was supplied, otherwise the 1-based position of the step.
         */
        String getStepNumber();
    }
}

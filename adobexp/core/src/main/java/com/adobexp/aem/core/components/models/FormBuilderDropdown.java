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
 * Sling Model interface for the Form Builder Dropdown component.
 * Provides access to dropdown configuration including options from multifield.
 */
public interface FormBuilderDropdown {

    /**
     * Gets the list of dropdown options.
     * @return list of dropdown options
     */
    List<DropdownOption> getOptions();

    /**
     * Checks if dropdown options are configured.
     * @return true if any option is configured
     */
    boolean hasOptions();

    /**
     * Represents a dropdown option with value, label, and selected state.
     */
    interface DropdownOption {
        /**
         * Gets the option value (submitted with form).
         * @return option value
         */
        String getValue();

        /**
         * Gets the option display label.
         * @return option label
         */
        String getLabel();

        /**
         * Checks if this option should be selected by default.
         * @return true if selected by default
         */
        boolean isSelected();
    }
}

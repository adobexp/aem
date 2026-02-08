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
package com.adobexp.aem.core.components.config.components;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-Aware Configuration for Form Builder component theme variables.
 */
@Configuration(
        label = "AdobeXP - Form Builder Theme Configuration",
        description = "Context-Aware Configuration for Form Builder component CSS variables"
)
public @interface FormBuilderThemeConfig {

    // Form Builder Section Background
    @Property(label = "Form Builder Background (Dark)", description = "Form Builder section background in dark theme")
    String darkFormBuilderBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Form Builder Background (Light)", description = "Form Builder section background in light theme")
    String lightFormBuilderBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    // Form Field Background
    @Property(label = "Form Field Background (Dark)", description = "Form field background in dark theme")
    String darkFormFieldBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Form Field Background (Light)", description = "Form field background in light theme")
    String lightFormFieldBg() default "rgba(0, 0, 0, 0.03)";

    // Form Field Border
    @Property(label = "Form Field Border (Dark)", description = "Form field border color in dark theme")
    String darkFormFieldBorder() default "rgba(255, 255, 255, 0.15)";

    @Property(label = "Form Field Border (Light)", description = "Form field border color in light theme")
    String lightFormFieldBorder() default "rgba(0, 0, 0, 0.15)";

    // Form Field Hover Border
    @Property(label = "Form Field Hover Border (Dark)", description = "Form field hover border color in dark theme")
    String darkFormFieldHoverBorder() default "rgba(255, 255, 255, 0.3)";

    @Property(label = "Form Field Hover Border (Light)", description = "Form field hover border color in light theme")
    String lightFormFieldHoverBorder() default "rgba(0, 0, 0, 0.3)";

    // Form Field Focus Background
    @Property(label = "Form Field Focus Background (Dark)", description = "Form field focus background in dark theme")
    String darkFormFieldFocusBg() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Form Field Focus Background (Light)", description = "Form field focus background in light theme")
    String lightFormFieldFocusBg() default "rgba(0, 0, 0, 0.05)";

    // Form Dropdown Background
    @Property(label = "Form Dropdown Background (Dark)", description = "Form dropdown menu background in dark theme")
    String darkFormDropdownBg() default "#2a2a2a";

    @Property(label = "Form Dropdown Background (Light)", description = "Form dropdown menu background in light theme")
    String lightFormDropdownBg() default "#ffffff";

    // Form Builder Card Background
    @Property(label = "Form Builder Card Background (Dark)", description = "Form builder card/container background in dark theme")
    String darkFormBuilderCardBg() default "#2a2a2a";

    @Property(label = "Form Builder Card Background (Light)", description = "Form builder card/container background in light theme")
    String lightFormBuilderCardBg() default "#f2fffd";
}

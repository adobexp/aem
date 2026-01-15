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
 * Context-Aware Configuration for Comparison component theme variables.
 */
@Configuration(
        label = "AdobeXP - Comparison Theme Configuration",
        description = "Context-Aware Configuration for Comparison component CSS variables"
)
public @interface ComparisonThemeConfig {

    @Property(label = "Comparison Background (Dark)", description = "Comparison section background in dark theme")
    String darkComparisonBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Comparison Background (Light)", description = "Comparison section background in light theme")
    String lightComparisonBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";

    @Property(label = "Comparison Column Background (Dark)", description = "Comparison column background in dark theme")
    String darkComparisonColBg() default "#2a2a2a";

    @Property(label = "Comparison Column Background (Light)", description = "Comparison column background in light theme")
    String lightComparisonColBg() default "#ffffff";
}

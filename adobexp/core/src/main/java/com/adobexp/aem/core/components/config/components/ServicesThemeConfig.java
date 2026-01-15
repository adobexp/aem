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
 * Context-Aware Configuration for Services component theme variables.
 */
@Configuration(
        label = "AdobeXP - Services Theme Configuration",
        description = "Context-Aware Configuration for Services component CSS variables"
)
public @interface ServicesThemeConfig {

    @Property(label = "Services Divider Color (Dark)", description = "Services divider color in dark theme")
    String darkServicesDividerColor() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Services Divider Color (Light)", description = "Services divider color in light theme")
    String lightServicesDividerColor() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Services Background (Dark)", description = "Services background gradient in dark theme")
    String darkServicesBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Services Background (Light)", description = "Services background gradient in light theme")
    String lightServicesBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";
}

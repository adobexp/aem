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
 * Context-Aware Configuration for Header component theme variables.
 */
@Configuration(
        label = "AdobeXP - Header Theme Configuration",
        description = "Context-Aware Configuration for Header component CSS variables"
)
public @interface HeaderThemeConfig {

    @Property(label = "Header Background Color (Dark)", description = "Background color for header in dark theme")
    String darkHeaderBackgroundColor() default "#212020";

    @Property(label = "Header Background Color (Light)", description = "Background color for header in light theme")
    String lightHeaderBackgroundColor() default "#fdfeff";

    @Property(label = "Header Height", description = "Height of the header")
    String headerHeight() default "60px";
}

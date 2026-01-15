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
 * Context-Aware Configuration for Button component theme variables.
 */
@Configuration(
        label = "AdobeXP - Button Theme Configuration",
        description = "Context-Aware Configuration for Button component CSS variables"
)
public @interface ButtonThemeConfig {

    @Property(label = "Button Background (Dark)", description = "Button background in dark theme")
    String darkButtonBg() default "transparent";

    @Property(label = "Button Text Color (Dark)", description = "Button text color in dark theme")
    String darkButtonText() default "#ffffff";

    @Property(label = "Button Border (Dark)", description = "Button border color in dark theme")
    String darkButtonBorder() default "#ffffff";

    @Property(label = "Button Hover Background (Dark)", description = "Button hover background in dark theme")
    String darkButtonHoverBg() default "#ffffff";

    @Property(label = "Button Hover Text (Dark)", description = "Button hover text color in dark theme")
    String darkButtonHoverText() default "#000000";

    @Property(label = "Button Background (Light)", description = "Button background in light theme")
    String lightButtonBg() default "transparent";

    @Property(label = "Button Text Color (Light)", description = "Button text color in light theme")
    String lightButtonText() default "#000000";

    @Property(label = "Button Border (Light)", description = "Button border color in light theme")
    String lightButtonBorder() default "#000000";

    @Property(label = "Button Hover Background (Light)", description = "Button hover background in light theme")
    String lightButtonHoverBg() default "#000000";

    @Property(label = "Button Hover Text (Light)", description = "Button hover text color in light theme")
    String lightButtonHoverText() default "#ffffff";
}

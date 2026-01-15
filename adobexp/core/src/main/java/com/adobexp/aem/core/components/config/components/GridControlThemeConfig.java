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
 * Context-Aware Configuration for Grid Control component theme variables.
 */
@Configuration(
        label = "AdobeXP - Grid Control Theme Configuration",
        description = "Context-Aware Configuration for Grid Control component CSS variables"
)
public @interface GridControlThemeConfig {

    @Property(label = "Grid Control Background (Dark)", description = "Grid control component background in dark theme")
    String darkGridControlBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Background (Light)", description = "Grid control component background in light theme")
    String lightGridControlBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Column Background (Dark)", description = "Grid control column background in dark theme")
    String darkGridControlColumnBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Grid Control Column Background (Light)", description = "Grid control column background in light theme")
    String lightGridControlColumnBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";
}

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
 * Context-Aware Configuration for CountUp component theme variables.
 */
@Configuration(
        label = "AdobeXP - CountUp Theme Configuration",
        description = "Context-Aware Configuration for CountUp component CSS variables"
)
public @interface CountUpThemeConfig {

    @Property(label = "CountUp Background (Dark)", description = "CountUp section background in dark theme")
    String darkCountUpBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "CountUp Background (Light)", description = "CountUp section background in light theme")
    String lightCountUpBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "CountUp Card Background (Dark)", description = "CountUp card background in dark theme")
    String darkCountUpCardBg() default "#2a2a2a";

    @Property(label = "CountUp Card Background (Light)", description = "CountUp card background in light theme")
    String lightCountUpCardBg() default "#ffffff";
}

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
 * Context-Aware Configuration for FAQ component theme variables.
 */
@Configuration(
        label = "AdobeXP - FAQ Theme Configuration",
        description = "Context-Aware Configuration for FAQ component CSS variables"
)
public @interface FaqThemeConfig {

    @Property(label = "FAQ Background (Dark)", description = "FAQ section background in dark theme")
    String darkFaqBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "FAQ Background (Light)", description = "FAQ section background in light theme")
    String lightFaqBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "FAQ Item Background (Dark)", description = "FAQ item background in dark theme")
    String darkFaqItemBg() default "#2a2a2a";

    @Property(label = "FAQ Item Background (Light)", description = "FAQ item background in light theme")
    String lightFaqItemBg() default "#f2fffd";

    @Property(label = "FAQ Item Hover Background (Dark)", description = "FAQ item hover background in dark theme")
    String darkFaqItemHoverBg() default "#333333";

    @Property(label = "FAQ Item Hover Background (Light)", description = "FAQ item hover background in light theme")
    String lightFaqItemHoverBg() default "#e5e7eb";
}

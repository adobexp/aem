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
 * Context-Aware Configuration for Cards component theme variables.
 */
@Configuration(
        label = "AdobeXP - Cards Theme Configuration",
        description = "Context-Aware Configuration for Cards component CSS variables"
)
public @interface CardsThemeConfig {

    @Property(label = "Cards Background (Dark)", description = "Cards section background (With Background style) in dark theme")
    String darkCardsBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Cards Background (Light)", description = "Cards section background (With Background style) in light theme")
    String lightCardsBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Cards Muted Background (Dark)", description = "Cards section muted gradient background in dark theme")
    String darkCardsMutedBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Cards Muted Background (Light)", description = "Cards section muted gradient background in light theme")
    String lightCardsMutedBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Cards Item Background (Dark)", description = "Individual card item background in dark theme")
    String darkCardsItemBg() default "#2a2a2a";

    @Property(label = "Cards Item Background (Light)", description = "Individual card item background in light theme")
    String lightCardsItemBg() default "#ffffff";

    @Property(label = "Cards Item Border (Dark)", description = "Individual card item border in dark theme")
    String darkCardsItemBorder() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Cards Item Border (Light)", description = "Individual card item border in light theme")
    String lightCardsItemBorder() default "rgba(0, 0, 0, 0.08)";

    @Property(label = "Cards Item Icon Background (Dark)", description = "Card icon/code chip background in dark theme")
    String darkCardsItemIconBg() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Cards Item Icon Background (Light)", description = "Card icon/code chip background in light theme")
    String lightCardsItemIconBg() default "rgba(0, 0, 0, 0.04)";
}

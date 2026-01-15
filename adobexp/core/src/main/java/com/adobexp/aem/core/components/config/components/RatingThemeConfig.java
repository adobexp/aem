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
 * Context-Aware Configuration for Rating component theme variables.
 */
@Configuration(
        label = "AdobeXP - Rating Theme Configuration",
        description = "Context-Aware Configuration for Rating component CSS variables"
)
public @interface RatingThemeConfig {

    @Property(label = "Rating Background (Dark)", description = "Rating section background in dark theme")
    String darkRatingBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Rating Background (Light)", description = "Rating section background in light theme")
    String lightRatingBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Rating Avatar Border (Dark)", description = "Rating avatar border in dark theme")
    String darkRatingAvatarBorder() default "#2a2a2a";

    @Property(label = "Rating Avatar Border (Light)", description = "Rating avatar border in light theme")
    String lightRatingAvatarBorder() default "#ffffff";

    @Property(label = "Rating Avatar Background (Dark)", description = "Rating avatar background in dark theme")
    String darkRatingAvatarBg() default "#404040";

    @Property(label = "Rating Avatar Background (Light)", description = "Rating avatar background in light theme")
    String lightRatingAvatarBg() default "#e5e7eb";

    @Property(label = "Rating Star Color (Dark)", description = "Rating star color in dark theme")
    String darkRatingStarColor() default "#fbbf24";

    @Property(label = "Rating Star Color (Light)", description = "Rating star color in light theme")
    String lightRatingStarColor() default "#fbbf24";

    @Property(label = "Rating Star Empty Color (Dark)", description = "Rating star empty color in dark theme")
    String darkRatingStarEmptyColor() default "#525252";

    @Property(label = "Rating Star Empty Color (Light)", description = "Rating star empty color in light theme")
    String lightRatingStarEmptyColor() default "#d1d5db";

    @Property(label = "Rating CTA Background (Dark)", description = "Rating CTA background in dark theme")
    String darkRatingCtaBg() default "#ffffff";

    @Property(label = "Rating CTA Background (Light)", description = "Rating CTA background in light theme")
    String lightRatingCtaBg() default "#000000";

    @Property(label = "Rating CTA Text (Dark)", description = "Rating CTA text color in dark theme")
    String darkRatingCtaText() default "#000000";

    @Property(label = "Rating CTA Text (Light)", description = "Rating CTA text color in light theme")
    String lightRatingCtaText() default "#ffffff";

    @Property(label = "Rating CTA Hover Background (Dark)", description = "Rating CTA hover background in dark theme")
    String darkRatingCtaHoverBg() default "#e0e0e0";

    @Property(label = "Rating CTA Hover Background (Light)", description = "Rating CTA hover background in light theme")
    String lightRatingCtaHoverBg() default "#333333";

    @Property(label = "Rating CTA Icon Background (Dark)", description = "Rating CTA icon background in dark theme")
    String darkRatingCtaIconBg() default "#000000";

    @Property(label = "Rating CTA Icon Background (Light)", description = "Rating CTA icon background in light theme")
    String lightRatingCtaIconBg() default "#ffffff";

    @Property(label = "Rating CTA Icon Color (Dark)", description = "Rating CTA icon color in dark theme")
    String darkRatingCtaIconColor() default "#ffffff";

    @Property(label = "Rating CTA Icon Color (Light)", description = "Rating CTA icon color in light theme")
    String lightRatingCtaIconColor() default "#000000";
}

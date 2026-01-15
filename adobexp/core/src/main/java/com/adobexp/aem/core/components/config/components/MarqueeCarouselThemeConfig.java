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
 * Context-Aware Configuration for Marquee Carousel component theme variables.
 */
@Configuration(
        label = "AdobeXP - Marquee Carousel Theme Configuration",
        description = "Context-Aware Configuration for Marquee Carousel component CSS variables"
)
public @interface MarqueeCarouselThemeConfig {

    @Property(label = "Marquee Carousel Background (Dark)", description = "Marquee carousel section background in dark theme")
    String darkMarqueeCarouselBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Marquee Carousel Background (Light)", description = "Marquee carousel section background in light theme")
    String lightMarqueeCarouselBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Marquee Carousel Fade Color (Light)", description = "Marquee carousel fade color in light theme")
    String lightMarqueeCarouselFadeColor() default "#dbeeff";

    @Property(label = "Marquee Carousel Card Background (Dark)", description = "Marquee carousel card background in dark theme")
    String darkMarqueeCarouselCardBg() default "#404040";

    @Property(label = "Marquee Carousel Card Background (Light)", description = "Marquee carousel card background in light theme")
    String lightMarqueeCarouselCardBg() default "#e5e7eb";

    @Property(label = "Marquee Carousel CTA Background (Dark)", description = "Marquee carousel CTA background in dark theme")
    String darkMarqueeCarouselCtaBg() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Background (Light)", description = "Marquee carousel CTA background in light theme")
    String lightMarqueeCarouselCtaBg() default "#000000";

    @Property(label = "Marquee Carousel CTA Text (Dark)", description = "Marquee carousel CTA text color in dark theme")
    String darkMarqueeCarouselCtaText() default "#000000";

    @Property(label = "Marquee Carousel CTA Text (Light)", description = "Marquee carousel CTA text color in light theme")
    String lightMarqueeCarouselCtaText() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Hover Background (Dark)", description = "Marquee carousel CTA hover background in dark theme")
    String darkMarqueeCarouselCtaHoverBg() default "#e0e0e0";

    @Property(label = "Marquee Carousel CTA Hover Background (Light)", description = "Marquee carousel CTA hover background in light theme")
    String lightMarqueeCarouselCtaHoverBg() default "#333333";

    @Property(label = "Marquee Carousel CTA Icon Background (Dark)", description = "Marquee carousel CTA icon background in dark theme")
    String darkMarqueeCarouselCtaIconBg() default "#000000";

    @Property(label = "Marquee Carousel CTA Icon Background (Light)", description = "Marquee carousel CTA icon background in light theme")
    String lightMarqueeCarouselCtaIconBg() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Icon Color (Dark)", description = "Marquee carousel CTA icon color in dark theme")
    String darkMarqueeCarouselCtaIconColor() default "#ffffff";

    @Property(label = "Marquee Carousel CTA Icon Color (Light)", description = "Marquee carousel CTA icon color in light theme")
    String lightMarqueeCarouselCtaIconColor() default "#000000";

    @Property(label = "Marquee Carousel Duration", description = "Marquee carousel animation duration")
    String marqueeCarouselDuration() default "35s";
}

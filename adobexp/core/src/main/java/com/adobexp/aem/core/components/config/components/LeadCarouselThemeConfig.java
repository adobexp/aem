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
 * Context-Aware Configuration for Lead Carousel component theme variables.
 */
@Configuration(
        label = "AdobeXP - Lead Carousel Theme Configuration",
        description = "Context-Aware Configuration for Lead Carousel component CSS variables"
)
public @interface LeadCarouselThemeConfig {

    @Property(label = "Lead Carousel Background (Dark)", description = "Lead carousel fallback background in dark theme")
    String darkLeadCarouselBg() default "#111111";

    @Property(label = "Lead Carousel Background (Light)", description = "Lead carousel fallback background in light theme")
    String lightLeadCarouselBg() default "#111111";

    @Property(label = "Lead Carousel Text (Dark)", description = "Lead carousel title and copy color in dark theme")
    String darkLeadCarouselText() default "#ffffff";

    @Property(label = "Lead Carousel Text (Light)", description = "Lead carousel title and copy color in light theme")
    String lightLeadCarouselText() default "#ffffff";

    @Property(label = "Lead Carousel Text Muted (Dark)", description = "Lead carousel supporting text color in dark theme")
    String darkLeadCarouselTextMuted() default "rgba(255, 255, 255, 0.92)";

    @Property(label = "Lead Carousel Text Muted (Light)", description = "Lead carousel supporting text color in light theme")
    String lightLeadCarouselTextMuted() default "rgba(255, 255, 255, 0.92)";

    @Property(label = "Lead Carousel Promo Text (Dark)", description = "Lead carousel promo stats color in dark theme")
    String darkLeadCarouselPromoText() default "#ffffff";

    @Property(label = "Lead Carousel Promo Text (Light)", description = "Lead carousel promo stats color in light theme")
    String lightLeadCarouselPromoText() default "#ffffff";

    @Property(label = "Lead Carousel CTA Background (Dark)", description = "Lead carousel CTA pill background in dark theme")
    String darkLeadCarouselCtaBg() default "#ffffff";

    @Property(label = "Lead Carousel CTA Background (Light)", description = "Lead carousel CTA pill background in light theme")
    String lightLeadCarouselCtaBg() default "#ffffff";

    @Property(label = "Lead Carousel CTA Text (Dark)", description = "Lead carousel CTA pill text color in dark theme")
    String darkLeadCarouselCtaText() default "#111111";

    @Property(label = "Lead Carousel CTA Text (Light)", description = "Lead carousel CTA pill text color in light theme")
    String lightLeadCarouselCtaText() default "#111111";

    @Property(label = "Lead Carousel CTA Hover Background (Dark)", description = "Lead carousel CTA pill hover background in dark theme")
    String darkLeadCarouselCtaHoverBg() default "#e8e8e8";

    @Property(label = "Lead Carousel CTA Hover Background (Light)", description = "Lead carousel CTA pill hover background in light theme")
    String lightLeadCarouselCtaHoverBg() default "#e8e8e8";

    @Property(label = "Lead Carousel Controls (Dark)", description = "Lead carousel pause/play and pagination color in dark theme")
    String darkLeadCarouselControls() default "#ffffff";

    @Property(label = "Lead Carousel Controls (Light)", description = "Lead carousel pause/play and pagination color in light theme")
    String lightLeadCarouselControls() default "#ffffff";

    @Property(label = "Lead Carousel Dot (Dark)", description = "Lead carousel inactive pagination color in dark theme")
    String darkLeadCarouselDot() default "rgba(255, 255, 255, 0.45)";

    @Property(label = "Lead Carousel Dot (Light)", description = "Lead carousel inactive pagination color in light theme")
    String lightLeadCarouselDot() default "rgba(255, 255, 255, 0.45)";

    @Property(label = "Lead Carousel Dot Track (Dark)", description = "Lead carousel pagination track color in dark theme")
    String darkLeadCarouselDotTrack() default "rgba(255, 255, 255, 0.28)";

    @Property(label = "Lead Carousel Dot Track (Light)", description = "Lead carousel pagination track color in light theme")
    String lightLeadCarouselDotTrack() default "rgba(255, 255, 255, 0.28)";

    @Property(label = "Lead Carousel Dot Fill (Dark)", description = "Lead carousel pagination progress fill color in dark theme")
    String darkLeadCarouselDotFill() default "#ffffff";

    @Property(label = "Lead Carousel Dot Fill (Light)", description = "Lead carousel pagination progress fill color in light theme")
    String lightLeadCarouselDotFill() default "#ffffff";
}

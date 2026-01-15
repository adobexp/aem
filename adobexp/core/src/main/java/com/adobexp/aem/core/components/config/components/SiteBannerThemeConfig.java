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
 * Context-Aware Configuration for Site Banner component theme variables.
 */
@Configuration(
        label = "AdobeXP - Site Banner Theme Configuration",
        description = "Context-Aware Configuration for Site Banner component CSS variables"
)
public @interface SiteBannerThemeConfig {

    @Property(label = "Site Banner Background (Dark)", description = "Site banner background color in dark theme")
    String darkSiteBannerBg() default "#363535";

    @Property(label = "Site Banner Background (Light)", description = "Site banner background color in light theme")
    String lightSiteBannerBg() default "#9adcfa";

    @Property(label = "Site Banner Marquee Duration (Dark)", description = "Site banner marquee animation duration in dark theme")
    String darkSiteBannerMarqueeDuration() default "5s";

    @Property(label = "Site Banner Marquee Duration (Light)", description = "Site banner marquee animation duration in light theme")
    String lightSiteBannerMarqueeDuration() default "10s";

    @Property(label = "Site Banner Cycle Duration (Dark)", description = "Site banner cycle duration in dark theme")
    String darkSiteBannerCycleDuration() default "10s";

    @Property(label = "Site Banner Cycle Duration (Light)", description = "Site banner cycle duration in light theme")
    String lightSiteBannerCycleDuration() default "20s";

    @Property(label = "Site Banner Font Size", description = "Site banner font size")
    String siteBannerFontSize() default "20px";
}

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
 * Context-Aware Configuration for Masonry Gallery component theme variables.
 */
@Configuration(
        label = "AdobeXP - Masonry Gallery Theme Configuration",
        description = "Context-Aware Configuration for Masonry Gallery component CSS variables"
)
public @interface MasonryGalleryThemeConfig {

    @Property(label = "Masonry Gallery Background (Dark)", description = "Masonry gallery section background in dark theme")
    String darkMasonryGalleryBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Masonry Gallery Background (Light)", description = "Masonry gallery section background in light theme")
    String lightMasonryGalleryBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Masonry Gallery Item Background (Dark)", description = "Masonry gallery item background in dark theme")
    String darkMasonryGalleryItemBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Masonry Gallery Item Background (Light)", description = "Masonry gallery item background in light theme")
    String lightMasonryGalleryItemBg() default "rgba(0, 0, 0, 0.03)";

    @Property(label = "Masonry Gallery Lightbox Background (Dark)", description = "Masonry gallery lightbox background in dark theme")
    String darkMasonryGalleryLightboxBg() default "rgba(10, 10, 10, 0.95)";

    @Property(label = "Masonry Gallery Lightbox Background (Light)", description = "Masonry gallery lightbox background in light theme")
    String lightMasonryGalleryLightboxBg() default "rgba(10, 10, 10, 0.95)";

    @Property(label = "Masonry Gallery Lightbox Title Color (Dark)", description = "Masonry gallery lightbox title color in dark theme")
    String darkMasonryGalleryLightboxTitleColor() default "#ffffff";

    @Property(label = "Masonry Gallery Lightbox Title Color (Light)", description = "Masonry gallery lightbox title color in light theme")
    String lightMasonryGalleryLightboxTitleColor() default "#ffffff";

    @Property(label = "Masonry Gallery Lightbox Title Background (Dark)", description = "Masonry gallery lightbox title background in dark theme")
    String darkMasonryGalleryLightboxTitleBg() default "rgba(0, 0, 0, 0.7)";

    @Property(label = "Masonry Gallery Lightbox Title Background (Light)", description = "Masonry gallery lightbox title background in light theme")
    String lightMasonryGalleryLightboxTitleBg() default "rgba(0, 0, 0, 0.7)";
}

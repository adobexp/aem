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
 * Context-Aware Configuration for Blob Image Section component theme variables.
 */
@Configuration(
        label = "AdobeXP - Blob Image Section Theme Configuration",
        description = "Context-Aware Configuration for Blob Image Section component CSS variables"
)
public @interface BlobImageSectionThemeConfig {

    @Property(label = "Blob Image Section Background (Dark)", description = "Blob image section background in dark theme")
    String darkBlobImageSectionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Blob Image Section Background (Light)", description = "Blob image section background in light theme")
    String lightBlobImageSectionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, #aafbff 10%, var(--main-theme-color) 50%, #aafbff 90%, var(--site-body-bg) 100%)";

    @Property(label = "Blob Image Section Card Background (Dark)", description = "Blob image section card background in dark theme")
    String darkBlobImageSectionCardBg() default "#2a2a2a";

    @Property(label = "Blob Image Section Card Background (Light)", description = "Blob image section card background in light theme")
    String lightBlobImageSectionCardBg() default "#f2fffd";

    @Property(label = "Blob Image Section Badge Background (Dark)", description = "Blob image section badge background in dark theme")
    String darkBlobImageSectionBadgeBg() default "#363636";

    @Property(label = "Blob Image Section Badge Background (Light)", description = "Blob image section badge background in light theme")
    String lightBlobImageSectionBadgeBg() default "#ffffff";

    @Property(label = "Blob Image Section Badge Border (Dark)", description = "Blob image section badge border in dark theme")
    String darkBlobImageSectionBadgeBorder() default "#404040";

    @Property(label = "Blob Image Section Badge Border (Light)", description = "Blob image section badge border in light theme")
    String lightBlobImageSectionBadgeBorder() default "#e5e7eb";

    @Property(label = "Blob Image Section Badge Text (Light)", description = "Blob image section badge text in light theme")
    String lightBlobImageSectionBadgeText() default "#3b82f6";

    @Property(label = "Blob Image Section Icon Badge Background (Dark)", description = "Blob image section icon badge background in dark theme")
    String darkBlobImageSectionIconBadgeBg() default "#8b5cf6";

    @Property(label = "Blob Image Section Icon Badge Background (Light)", description = "Blob image section icon badge background in light theme")
    String lightBlobImageSectionIconBadgeBg() default "#8b5cf6";

    @Property(label = "Blob Image Section Icon Badge Color (Dark)", description = "Blob image section icon badge color in dark theme")
    String darkBlobImageSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Blob Image Section Icon Badge Color (Light)", description = "Blob image section icon badge color in light theme")
    String lightBlobImageSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Blob Image Section Overlay Card Background (Dark)", description = "Blob image section overlay card background in dark theme")
    String darkBlobImageSectionOverlayCardBg() default "#363636";

    @Property(label = "Blob Image Section Overlay Card Background (Light)", description = "Blob image section overlay card background in light theme")
    String lightBlobImageSectionOverlayCardBg() default "#ffffff";
}

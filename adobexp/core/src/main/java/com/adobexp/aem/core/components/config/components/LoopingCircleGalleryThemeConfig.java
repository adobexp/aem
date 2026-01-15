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
 * Context-Aware Configuration for Looping Circle Gallery component theme variables.
 */
@Configuration(
        label = "AdobeXP - Looping Circle Gallery Theme Configuration",
        description = "Context-Aware Configuration for Looping Circle Gallery component CSS variables"
)
public @interface LoopingCircleGalleryThemeConfig {

    @Property(label = "Looping Circle Gallery Overlay Background (Dark)", description = "Gallery overlay background in dark theme")
    String darkLoopingCircleGalleryOverlayBg() default "rgba(255, 255, 255, 0.5)";

    @Property(label = "Looping Circle Gallery Overlay Background (Light)", description = "Gallery overlay background in light theme")
    String lightLoopingCircleGalleryOverlayBg() default "linear-gradient(180deg, #ffffff53 0%, var(--main-theme-color) 50%, #ffffff53 100%)";

    @Property(label = "Looping Circle Gallery Overlay Text (Dark)", description = "Gallery overlay text color in dark theme")
    String darkLoopingCircleGalleryOverlayText() default "#000000";

    @Property(label = "Looping Circle Gallery Overlay Text (Light)", description = "Gallery overlay text color in light theme")
    String lightLoopingCircleGalleryOverlayText() default "#ffffff";
}

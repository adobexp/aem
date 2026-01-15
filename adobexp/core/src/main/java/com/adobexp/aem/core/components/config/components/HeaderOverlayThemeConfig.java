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
 * Context-Aware Configuration for Header Overlay theme variables.
 */
@Configuration(
        label = "AdobeXP - Header Overlay Theme Configuration",
        description = "Context-Aware Configuration for Header Overlay CSS variables"
)
public @interface HeaderOverlayThemeConfig {

    @Property(label = "Header Overlay Column Divider Color (Dark)", description = "Header overlay column divider color in dark theme")
    String darkHeaderOverlayColumnDividerColor() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Header Overlay Column Divider Color (Light)", description = "Header overlay column divider color in light theme")
    String lightHeaderOverlayColumnDividerColor() default "rgba(0, 0, 0, 0.18)";

    @Property(label = "Header Overlay Hover Background (Dark)", description = "Header overlay hover background in dark theme")
    String darkHeaderOverlayHoverBg() default "rgba(255, 255, 255, 0.10)";

    @Property(label = "Header Overlay Hover Background (Light)", description = "Header overlay hover background in light theme")
    String lightHeaderOverlayHoverBg() default "rgba(0, 0, 0, 0.08)";
}

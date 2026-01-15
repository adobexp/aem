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
 * Context-Aware Configuration for CTA Pill component theme variables.
 */
@Configuration(
        label = "AdobeXP - CTA Pill Theme Configuration",
        description = "Context-Aware Configuration for CTA Pill component CSS variables"
)
public @interface CtaPillThemeConfig {

    @Property(label = "CTA Pill Background (Dark)", description = "CTA pill button background in dark theme")
    String darkCtaPillBg() default "#ffffff";

    @Property(label = "CTA Pill Text (Dark)", description = "CTA pill button text color in dark theme")
    String darkCtaPillText() default "#000000";

    @Property(label = "CTA Pill Hover Background (Dark)", description = "CTA pill button hover background in dark theme")
    String darkCtaPillHoverBg() default "#e0e0e0";

    @Property(label = "CTA Pill Icon Background (Dark)", description = "CTA pill button icon background in dark theme")
    String darkCtaPillIconBg() default "#000000";

    @Property(label = "CTA Pill Icon Color (Dark)", description = "CTA pill button icon color in dark theme")
    String darkCtaPillIconColor() default "#ffffff";

    @Property(label = "CTA Pill Background (Light)", description = "CTA pill button background in light theme")
    String lightCtaPillBg() default "#000000";

    @Property(label = "CTA Pill Text (Light)", description = "CTA pill button text color in light theme")
    String lightCtaPillText() default "#ffffff";

    @Property(label = "CTA Pill Hover Background (Light)", description = "CTA pill button hover background in light theme")
    String lightCtaPillHoverBg() default "#333333";

    @Property(label = "CTA Pill Icon Background (Light)", description = "CTA pill button icon background in light theme")
    String lightCtaPillIconBg() default "#ffffff";

    @Property(label = "CTA Pill Icon Color (Light)", description = "CTA pill button icon color in light theme")
    String lightCtaPillIconColor() default "#000000";
}

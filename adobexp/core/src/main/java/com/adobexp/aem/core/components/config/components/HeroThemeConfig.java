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
 * Context-Aware Configuration for Hero component theme variables.
 */
@Configuration(
        label = "AdobeXP - Hero Theme Configuration",
        description = "Context-Aware Configuration for Hero component CSS variables"
)
public @interface HeroThemeConfig {

    @Property(label = "Hero Background (Dark)", description = "Hero section background in dark theme")
    String darkHeroBg() default "linear-gradient(135deg, var(--lead-banner-gradient-start, #212020) 0%, var(--lead-banner-gradient-stop-25, #aa7802) 50%, var(--lead-banner-gradient-end, #212020) 100%)";

    @Property(label = "Hero Background (Light)", description = "Hero section background in light theme")
    String lightHeroBg() default "linear-gradient(135deg, var(--lead-banner-gradient-start, #ffffff) 0%, var(--lead-banner-gradient-stop-25, #aafbff) 50%, var(--lead-banner-gradient-end, #ffffff) 100%)";
}

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
 * Context-Aware Configuration for Footer component theme variables.
 */
@Configuration(
        label = "AdobeXP - Footer Theme Configuration",
        description = "Context-Aware Configuration for Footer component CSS variables"
)
public @interface FooterThemeConfig {

    @Property(label = "Footer Background (Dark)", description = "Footer background color in dark theme")
    String darkFooterBg() default "#363535";

    @Property(label = "Footer Background (Light)", description = "Footer background color in light theme")
    String lightFooterBg() default "#f5f5f5";

    @Property(label = "Footer Curtain Height Offset", description = "Footer curtain height offset")
    String footerCurtainHeightOffset() default "-25px";
}

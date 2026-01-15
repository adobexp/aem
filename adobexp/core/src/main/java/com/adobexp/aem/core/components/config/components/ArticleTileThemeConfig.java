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
 * Context-Aware Configuration for Article Tile theme variables.
 */
@Configuration(
        label = "AdobeXP - Article Tile Theme Configuration",
        description = "Context-Aware Configuration for Article Tile CSS variables"
)
public @interface ArticleTileThemeConfig {

    @Property(label = "Article Tile Overlay Background (Dark)", description = "Article tile overlay background in dark theme")
    String darkArticleTileOverlayBg() default "rgba(0, 0, 0, 0.8)";

    @Property(label = "Article Tile Overlay Background (Light)", description = "Article tile overlay background in light theme")
    String lightArticleTileOverlayBg() default "#42f4fd75";
}

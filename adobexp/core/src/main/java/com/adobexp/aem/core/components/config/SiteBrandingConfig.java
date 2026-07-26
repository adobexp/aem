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
package com.adobexp.aem.core.components.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Per-site browser branding: the icons and colour a browser uses for tabs, bookmarks and
 * home-screen shortcuts.
 *
 * Kept separate from {@link SiteThemeGlobalConfig} because that config feeds the generated theme
 * stylesheet and its cache-busting hash, which these values have no business influencing.
 */
@Configuration(label = "AdobeXP Site Branding", description = "Favicon and browser chrome branding for the site")
public @interface SiteBrandingConfig {

    @Property(label = "Favicon", description = "Path to the favicon shown in browser tabs. PNG, SVG or ICO.")
    String faviconPath() default "";

    @Property(label = "Apple Touch Icon", description = "Path to the icon used when the site is saved to an iOS home screen. PNG, 180x180 recommended.")
    String appleTouchIconPath() default "";

    @Property(label = "Browser Theme Color", description = "Colour applied to browser UI on mobile, as a CSS hex value.")
    String browserThemeColor() default "";
}

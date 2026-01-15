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
 * Context-Aware Configuration for Quote component theme variables.
 */
@Configuration(
        label = "AdobeXP - Quote Theme Configuration",
        description = "Context-Aware Configuration for Quote component CSS variables"
)
public @interface QuoteThemeConfig {

    @Property(label = "Quote Background (Dark)", description = "Quote section background in dark theme")
    String darkQuoteBg() default "#363535";

    @Property(label = "Quote Background (Light)", description = "Quote section background in light theme (uses var(--site-body-bg) by default)")
    String lightQuoteBg() default "";

    @Property(label = "Quote Card Glow (Dark)", description = "Quote card glow gradient effect in dark theme")
    String darkQuoteCardGlow() default "radial-gradient(closest-side at 82% 28%, rgba(246, 255, 0, 0.34), transparent 60%), radial-gradient(closest-side at 92% 10%, rgba(255, 196, 0, 0.22), transparent 58%)";

    @Property(label = "Quote Card Glow (Light)", description = "Quote card glow gradient effect in light theme")
    String lightQuoteCardGlow() default "radial-gradient(closest-side at 82% 28%, var(--main-theme-color)ad, transparent 60%), radial-gradient(closest-side at 92% 10%, var(--main-theme-color)5e, transparent 58%)";
}

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
 * Context-Aware Configuration for Compare Subscription component theme variables.
 */
@Configuration(
        label = "AdobeXP - Compare Subscription Theme Configuration",
        description = "Context-Aware Configuration for Compare Subscription component CSS variables"
)
public @interface CompareSubscriptionThemeConfig {

    @Property(label = "Compare Subscription Background (Dark)", description = "Compare subscription section background in dark theme")
    String darkCompareSubscriptionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Compare Subscription Background (Light)", description = "Compare subscription section background in light theme")
    String lightCompareSubscriptionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Compare Subscription Table Background (Dark)", description = "Compare subscription table background in dark theme")
    String darkCompareSubscriptionTableBg() default "#2a2a2a";

    @Property(label = "Compare Subscription Table Background (Light)", description = "Compare subscription table background in light theme")
    String lightCompareSubscriptionTableBg() default "#ffffff";

    @Property(label = "Compare Subscription Border (Dark)", description = "Compare subscription border in dark theme")
    String darkCompareSubscriptionBorder() default "#404040";

    @Property(label = "Compare Subscription Border (Light)", description = "Compare subscription border in light theme")
    String lightCompareSubscriptionBorder() default "#e5e7eb";

    @Property(label = "Compare Subscription Features Background (Dark)", description = "Compare subscription features background in dark theme")
    String darkCompareSubscriptionFeaturesBg() default "#1f1f1f";

    @Property(label = "Compare Subscription Features Background (Light)", description = "Compare subscription features background in light theme")
    String lightCompareSubscriptionFeaturesBg() default "#f9fafb";

    @Property(label = "Compare Subscription Row Alt Background (Dark)", description = "Compare subscription row alt background in dark theme")
    String darkCompareSubscriptionRowAltBg() default "rgba(255, 255, 255, 0.02)";

    @Property(label = "Compare Subscription Row Alt Background (Light)", description = "Compare subscription row alt background in light theme")
    String lightCompareSubscriptionRowAltBg() default "rgba(0, 0, 0, 0.02)";

    @Property(label = "Compare Subscription Section Background (Dark)", description = "Compare subscription section background in dark theme")
    String darkCompareSubscriptionSectionBg() default "#262626";

    @Property(label = "Compare Subscription Section Background (Light)", description = "Compare subscription section background in light theme")
    String lightCompareSubscriptionSectionBg() default "#f3f4f6";

    @Property(label = "Compare Subscription Highlight Background (Dark)", description = "Compare subscription highlight background in dark theme")
    String darkCompareSubscriptionHighlightBg() default "rgba(255, 200, 70, 0.08)";

    @Property(label = "Compare Subscription Highlight Background (Light)", description = "Compare subscription highlight background in light theme")
    String lightCompareSubscriptionHighlightBg() default "rgba(59, 130, 246, 0.05)";

    @Property(label = "Compare Subscription Highlight Accent (Light)", description = "Compare subscription highlight accent in light theme")
    String lightCompareSubscriptionHighlightAccent() default "#3b82f6";

    @Property(label = "Compare Subscription Price Color (Light)", description = "Compare subscription price color in light theme")
    String lightCompareSubscriptionPriceColor() default "#3b82f6";

    @Property(label = "Compare Subscription Check Color (Dark)", description = "Compare subscription check color in dark theme")
    String darkCompareSubscriptionCheckColor() default "#4ade80";

    @Property(label = "Compare Subscription Check Color (Light)", description = "Compare subscription check color in light theme")
    String lightCompareSubscriptionCheckColor() default "#22c55e";

    @Property(label = "Compare Subscription Cross Color (Dark)", description = "Compare subscription cross color in dark theme")
    String darkCompareSubscriptionCrossColor() default "#ef4444";

    @Property(label = "Compare Subscription Cross Color (Light)", description = "Compare subscription cross color in light theme")
    String lightCompareSubscriptionCrossColor() default "#ef4444";

    @Property(label = "Compare Subscription CTA Primary Background (Dark)", description = "Compare subscription CTA primary background in dark theme")
    String darkCompareSubscriptionCtaPrimaryBg() default "#02c36f";

    @Property(label = "Compare Subscription CTA Primary Background (Light)", description = "Compare subscription CTA primary background in light theme")
    String lightCompareSubscriptionCtaPrimaryBg() default "#3b82f6";

    @Property(label = "Compare Subscription CTA Primary Hover (Dark)", description = "Compare subscription CTA primary hover in dark theme")
    String darkCompareSubscriptionCtaPrimaryHover() default "#02a25d";

    @Property(label = "Compare Subscription CTA Primary Hover (Light)", description = "Compare subscription CTA primary hover in light theme")
    String lightCompareSubscriptionCtaPrimaryHover() default "#2563eb";
}

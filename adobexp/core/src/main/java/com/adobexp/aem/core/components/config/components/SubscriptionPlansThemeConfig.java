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
 * Context-Aware Configuration for Subscription Plans component theme variables.
 */
@Configuration(
        label = "AdobeXP - Subscription Plans Theme Configuration",
        description = "Context-Aware Configuration for Subscription Plans component CSS variables"
)
public @interface SubscriptionPlansThemeConfig {

    @Property(label = "Subscription Plans Background (Dark)", description = "Subscription plans section background in dark theme")
    String darkSubscriptionPlansBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Subscription Plans Background (Light)", description = "Subscription plans section background in light theme")
    String lightSubscriptionPlansBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Subscription Plans Card Background (Dark)", description = "Subscription plans card background in dark theme")
    String darkSubscriptionPlansCardBg() default "#2a2a2a";

    @Property(label = "Subscription Plans Card Background (Light)", description = "Subscription plans card background in light theme")
    String lightSubscriptionPlansCardBg() default "#ffffff";

    @Property(label = "Subscription Plans Card Border (Dark)", description = "Subscription plans card border in dark theme")
    String darkSubscriptionPlansCardBorder() default "#404040";

    @Property(label = "Subscription Plans Card Border (Light)", description = "Subscription plans card border in light theme")
    String lightSubscriptionPlansCardBorder() default "#e5e7eb";

    @Property(label = "Subscription Plans Toggle Background (Dark)", description = "Subscription plans toggle background in dark theme")
    String darkSubscriptionPlansToggleBg() default "#374151";

    @Property(label = "Subscription Plans Toggle Background (Light)", description = "Subscription plans toggle background in light theme")
    String lightSubscriptionPlansToggleBg() default "#e5e7eb";

    @Property(label = "Subscription Plans Toggle Active Background (Light)", description = "Subscription plans toggle active background in light theme")
    String lightSubscriptionPlansToggleActiveBg() default "#3b82f6";

    @Property(label = "Subscription Plans Toggle Active Text (Dark)", description = "Subscription plans toggle active text in dark theme")
    String darkSubscriptionPlansToggleActiveText() default "#ffffff";

    @Property(label = "Subscription Plans Toggle Active Text (Light)", description = "Subscription plans toggle active text in light theme")
    String lightSubscriptionPlansToggleActiveText() default "#ffffff";

    @Property(label = "Subscription Plans Save Badge Background (Dark)", description = "Subscription plans save badge background in dark theme")
    String darkSubscriptionPlansSaveBadgeBg() default "rgba(34, 197, 94, 0.2)";

    @Property(label = "Subscription Plans Save Badge Background (Light)", description = "Subscription plans save badge background in light theme")
    String lightSubscriptionPlansSaveBadgeBg() default "#dcfce7";

    @Property(label = "Subscription Plans Save Badge Text (Dark)", description = "Subscription plans save badge text in dark theme")
    String darkSubscriptionPlansSaveBadgeText() default "#4ade80";

    @Property(label = "Subscription Plans Save Badge Text (Light)", description = "Subscription plans save badge text in light theme")
    String lightSubscriptionPlansSaveBadgeText() default "#16a34a";

    @Property(label = "Subscription Plans Price Color (Light)", description = "Subscription plans price color in light theme")
    String lightSubscriptionPlansPriceColor() default "#3b82f6";

    @Property(label = "Subscription Plans Highlight Border (Light)", description = "Subscription plans highlight border in light theme")
    String lightSubscriptionPlansHighlightBorder() default "#3b82f6";

    @Property(label = "Subscription Plans Divider (Dark)", description = "Subscription plans divider color in dark theme")
    String darkSubscriptionPlansDivider() default "#404040";

    @Property(label = "Subscription Plans Divider (Light)", description = "Subscription plans divider color in light theme")
    String lightSubscriptionPlansDivider() default "#e5e7eb";

    @Property(label = "Subscription Plans Credits Icon (Light)", description = "Subscription plans credits icon color in light theme")
    String lightSubscriptionPlansCreditsIcon() default "#3b82f6";

    @Property(label = "Subscription Plans Feature Check (Dark)", description = "Subscription plans feature check color in dark theme")
    String darkSubscriptionPlansFeatureCheck() default "#4ade80";

    @Property(label = "Subscription Plans Feature Check (Light)", description = "Subscription plans feature check color in light theme")
    String lightSubscriptionPlansFeatureCheck() default "#22c55e";

    @Property(label = "Subscription Plans CTA Primary Background (Dark)", description = "Subscription plans CTA primary background in dark theme")
    String darkSubscriptionPlansCtaPrimaryBg() default "#02c36f";

    @Property(label = "Subscription Plans CTA Primary Background (Light)", description = "Subscription plans CTA primary background in light theme")
    String lightSubscriptionPlansCtaPrimaryBg() default "#3b82f6";

    @Property(label = "Subscription Plans CTA Primary Hover (Dark)", description = "Subscription plans CTA primary hover in dark theme")
    String darkSubscriptionPlansCtaPrimaryHover() default "#02a25d";

    @Property(label = "Subscription Plans CTA Primary Hover (Light)", description = "Subscription plans CTA primary hover in light theme")
    String lightSubscriptionPlansCtaPrimaryHover() default "#2563eb";
}

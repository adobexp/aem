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
 * Context-Aware Configuration for Lead Media Section component theme variables.
 */
@Configuration(
        label = "AdobeXP - Lead Media Section Theme Configuration",
        description = "Context-Aware Configuration for Lead Media Section component CSS variables"
)
public @interface LeadMediaSectionThemeConfig {

    @Property(label = "Lead Media Section Background (Dark)", description = "Lead media section background in dark theme")
    String darkLeadMediaSectionBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Lead Media Section Background (Light)", description = "Lead media section background in light theme")
    String lightLeadMediaSectionBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Lead Media Section Card Background (Dark)", description = "Lead media section card background in dark theme")
    String darkLeadMediaSectionCardBg() default "#2a2a2a";

    @Property(label = "Lead Media Section Card Background (Light)", description = "Lead media section card background in light theme")
    String lightLeadMediaSectionCardBg() default "#f2fffd";

    @Property(label = "Lead Media Section Icon Badge Background (Dark)", description = "Lead media section icon badge background in dark theme")
    String darkLeadMediaSectionIconBadgeBg() default "#3a3a3a";

    @Property(label = "Lead Media Section Icon Badge Background (Light)", description = "Lead media section icon badge background in light theme")
    String lightLeadMediaSectionIconBadgeBg() default "#e5e7eb";

    @Property(label = "Lead Media Section Icon Badge Color (Dark)", description = "Lead media section icon badge color in dark theme")
    String darkLeadMediaSectionIconBadgeColor() default "#ffffff";

    @Property(label = "Lead Media Section Icon Badge Color (Light)", description = "Lead media section icon badge color in light theme")
    String lightLeadMediaSectionIconBadgeColor() default "#374151";

    @Property(label = "Lead Media Section CTA Background (Dark)", description = "Lead media section CTA background in dark theme")
    String darkLeadMediaSectionCtaBg() default "#ffffff";

    @Property(label = "Lead Media Section CTA Background (Light)", description = "Lead media section CTA background in light theme")
    String lightLeadMediaSectionCtaBg() default "#000000";

    @Property(label = "Lead Media Section CTA Text (Dark)", description = "Lead media section CTA text color in dark theme")
    String darkLeadMediaSectionCtaText() default "#000000";

    @Property(label = "Lead Media Section CTA Text (Light)", description = "Lead media section CTA text color in light theme")
    String lightLeadMediaSectionCtaText() default "#ffffff";

    @Property(label = "Lead Media Section CTA Hover Background (Dark)", description = "Lead media section CTA hover background in dark theme")
    String darkLeadMediaSectionCtaHoverBg() default "#e0e0e0";

    @Property(label = "Lead Media Section CTA Hover Background (Light)", description = "Lead media section CTA hover background in light theme")
    String lightLeadMediaSectionCtaHoverBg() default "#333333";

    @Property(label = "Lead Media Section CTA Icon Background (Dark)", description = "Lead media section CTA icon background in dark theme")
    String darkLeadMediaSectionCtaIconBg() default "#3b82f6";

    @Property(label = "Lead Media Section CTA Icon Background (Light)", description = "Lead media section CTA icon background in light theme")
    String lightLeadMediaSectionCtaIconBg() default "#3b82f6";

    @Property(label = "Lead Media Section CTA Icon Color (Dark)", description = "Lead media section CTA icon color in dark theme")
    String darkLeadMediaSectionCtaIconColor() default "#ffffff";

    @Property(label = "Lead Media Section CTA Icon Color (Light)", description = "Lead media section CTA icon color in light theme")
    String lightLeadMediaSectionCtaIconColor() default "#ffffff";

    @Property(label = "Lead Media Section Media Background (Dark)", description = "Lead media section media background in dark theme")
    String darkLeadMediaSectionMediaBg() default "#1a1a1a";

    @Property(label = "Lead Media Section Media Background (Light)", description = "Lead media section media background in light theme")
    String lightLeadMediaSectionMediaBg() default "#e5e7eb";

    @Property(label = "Lead Media Section Media Border (Dark)", description = "Lead media section media border in dark theme")
    String darkLeadMediaSectionMediaBorder() default "rgba(180, 180, 180, 0.4)";

    @Property(label = "Lead Media Section Media Border (Light)", description = "Lead media section media border in light theme")
    String lightLeadMediaSectionMediaBorder() default "rgba(180, 180, 180, 0.5)";
}

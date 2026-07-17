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
 * Context-Aware Configuration for Video Article Grid component theme variables.
 */
@Configuration(
        label = "AdobeXP - Video Article Grid Theme Configuration",
        description = "Context-Aware Configuration for Video Article Grid component CSS variables"
)
public @interface VideoArticleGridThemeConfig {

    @Property(label = "Background (Dark)", description = "Section background in dark theme")
    String darkVideoArticleGridBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Background (Light)", description = "Section background in light theme")
    String lightVideoArticleGridBg() default "radial-gradient(circle, var(--main-theme-color) 0%, var(--site-body-bg) 70%)";

    @Property(label = "Card Background (Dark)")
    String darkVideoArticleGridCardBg() default "#1a1a2e";

    @Property(label = "Card Background (Light)")
    String lightVideoArticleGridCardBg() default "#ffffff";

    @Property(label = "Card Border (Dark)")
    String darkVideoArticleGridCardBorder() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Card Border (Light)")
    String lightVideoArticleGridCardBorder() default "#e5e7eb";

    @Property(label = "Thumbnail Background (Dark)")
    String darkVideoArticleGridThumbBg() default "#0d0d1a";

    @Property(label = "Thumbnail Background (Light)")
    String lightVideoArticleGridThumbBg() default "#e8f4f8";

    @Property(label = "Thumbnail Overlay (Dark)")
    String darkVideoArticleGridThumbOverlay() default "rgba(0, 0, 0, 0.15)";

    @Property(label = "Thumbnail Overlay (Light)")
    String lightVideoArticleGridThumbOverlay() default "rgba(0, 0, 0, 0.04)";

    @Property(label = "Play Button Background (Dark)")
    String darkVideoArticleGridPlayBtnBg() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Play Button Background (Light)")
    String lightVideoArticleGridPlayBtnBg() default "rgba(0, 0, 0, 0.06)";

    @Property(label = "Play Button Border (Dark)")
    String darkVideoArticleGridPlayBtnBorder() default "rgba(255, 255, 255, 0.18)";

    @Property(label = "Play Button Border (Light)")
    String lightVideoArticleGridPlayBtnBorder() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Play Button Color (Dark)")
    String darkVideoArticleGridPlayBtnColor() default "rgba(255, 255, 255, 0.6)";

    @Property(label = "Play Button Color (Light)")
    String lightVideoArticleGridPlayBtnColor() default "rgba(0, 0, 0, 0.45)";

    @Property(label = "Play Button Hover Background (Dark)")
    String darkVideoArticleGridPlayBtnHoverBg() default "rgba(255, 255, 255, 0.2)";

    @Property(label = "Play Button Hover Background (Light)")
    String lightVideoArticleGridPlayBtnHoverBg() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Play Button Hover Color (Dark)")
    String darkVideoArticleGridPlayBtnHoverColor() default "#ffffff";

    @Property(label = "Play Button Hover Color (Light)")
    String lightVideoArticleGridPlayBtnHoverColor() default "#000000";

    @Property(label = "Badge Background (Dark)")
    String darkVideoArticleGridBadgeBg() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Badge Background (Light)")
    String lightVideoArticleGridBadgeBg() default "#f3f4f6";

    @Property(label = "Badge Text (Dark)")
    String darkVideoArticleGridBadgeText() default "#9ca3af";

    @Property(label = "Badge Text (Light)")
    String lightVideoArticleGridBadgeText() default "#6b7280";

    @Property(label = "Badge Border (Dark)")
    String darkVideoArticleGridBadgeBorder() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Badge Border (Light)")
    String lightVideoArticleGridBadgeBorder() default "#e5e7eb";

    @Property(label = "Search Background (Dark)")
    String darkVideoArticleGridSearchBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Search Background (Light)")
    String lightVideoArticleGridSearchBg() default "rgba(0, 0, 0, 0.03)";

    @Property(label = "Search Border (Dark)")
    String darkVideoArticleGridSearchBorder() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Search Border (Light)")
    String lightVideoArticleGridSearchBorder() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Search Focus Border (Dark)")
    String darkVideoArticleGridSearchFocusBorder() default "rgba(255, 255, 255, 0.28)";

    @Property(label = "Search Focus Border (Light)")
    String lightVideoArticleGridSearchFocusBorder() default "rgba(0, 0, 0, 0.3)";

    @Property(label = "Search Focus Ring (Dark)")
    String darkVideoArticleGridSearchFocusRing() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Search Focus Ring (Light)")
    String lightVideoArticleGridSearchFocusRing() default "rgba(0, 0, 0, 0.06)";

    @Property(label = "Page Button Background (Dark)")
    String darkVideoArticleGridPageBtnBg() default "transparent";

    @Property(label = "Page Button Background (Light)")
    String lightVideoArticleGridPageBtnBg() default "transparent";

    @Property(label = "Page Button Text (Dark)")
    String darkVideoArticleGridPageBtnText() default "#ffffff";

    @Property(label = "Page Button Text (Light)")
    String lightVideoArticleGridPageBtnText() default "#000000";

    @Property(label = "Page Button Border (Dark)")
    String darkVideoArticleGridPageBtnBorder() default "#ffffff";

    @Property(label = "Page Button Border (Light)")
    String lightVideoArticleGridPageBtnBorder() default "#000000";

    @Property(label = "Page Button Hover Background (Dark)")
    String darkVideoArticleGridPageBtnHoverBg() default "#ffffff";

    @Property(label = "Page Button Hover Background (Light)")
    String lightVideoArticleGridPageBtnHoverBg() default "#000000";

    @Property(label = "Page Button Hover Text (Dark)")
    String darkVideoArticleGridPageBtnHoverText() default "#000000";

    @Property(label = "Page Button Hover Text (Light)")
    String lightVideoArticleGridPageBtnHoverText() default "#ffffff";

    @Property(label = "Dropdown Background (Dark)")
    String darkVideoArticleGridDropdownBg() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Dropdown Background (Light)")
    String lightVideoArticleGridDropdownBg() default "rgba(0, 0, 0, 0.03)";

    @Property(label = "Dropdown Border (Dark)")
    String darkVideoArticleGridDropdownBorder() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Dropdown Border (Light)")
    String lightVideoArticleGridDropdownBorder() default "rgba(0, 0, 0, 0.12)";

    @Property(label = "Dropdown Focus Border (Dark)")
    String darkVideoArticleGridDropdownFocusBorder() default "rgba(255, 255, 255, 0.28)";

    @Property(label = "Dropdown Focus Border (Light)")
    String lightVideoArticleGridDropdownFocusBorder() default "rgba(0, 0, 0, 0.3)";

    @Property(label = "Dropdown Focus Ring (Dark)")
    String darkVideoArticleGridDropdownFocusRing() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Dropdown Focus Ring (Light)")
    String lightVideoArticleGridDropdownFocusRing() default "rgba(0, 0, 0, 0.06)";

    @Property(label = "Dropdown Option Background (Dark)")
    String darkVideoArticleGridDropdownOptionBg() default "#1e1e1e";

    @Property(label = "Dropdown Option Background (Light)")
    String lightVideoArticleGridDropdownOptionBg() default "#ffffff";
}

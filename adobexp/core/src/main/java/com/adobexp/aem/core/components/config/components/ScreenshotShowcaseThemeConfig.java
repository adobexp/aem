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
 * Context-Aware Configuration for Screenshot Showcase component theme variables.
 */
@Configuration(
        label = "AdobeXP - Screenshot Showcase Theme Configuration",
        description = "Context-Aware Configuration for Screenshot Showcase component CSS variables"
)
public @interface ScreenshotShowcaseThemeConfig {

    @Property(label = "Screenshot Showcase Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkScreenshotShowcaseBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Screenshot Showcase Background (Light)", description = "Section background (With Background style) in light theme")
    String lightScreenshotShowcaseBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Screenshot Showcase Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkScreenshotShowcaseMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Screenshot Showcase Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightScreenshotShowcaseMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Screenshot Showcase Frame Background (Dark)", description = "Browser frame background in dark theme")
    String darkScreenshotShowcaseFrameBg() default "#16161d";

    @Property(label = "Screenshot Showcase Frame Background (Light)", description = "Browser frame background in light theme")
    String lightScreenshotShowcaseFrameBg() default "#ffffff";

    @Property(label = "Screenshot Showcase Frame Border (Dark)", description = "Browser frame border in dark theme")
    String darkScreenshotShowcaseFrameBorder() default "rgba(255, 255, 255, 0.12)";

    @Property(label = "Screenshot Showcase Frame Border (Light)", description = "Browser frame border in light theme")
    String lightScreenshotShowcaseFrameBorder() default "rgba(0, 0, 0, 0.1)";

    @Property(label = "Screenshot Showcase Frame Shadow (Dark)", description = "Browser frame resting shadow in dark theme")
    String darkScreenshotShowcaseFrameShadow() default "0 24px 70px rgba(0, 0, 0, 0.45)";

    @Property(label = "Screenshot Showcase Frame Shadow (Light)", description = "Browser frame resting shadow in light theme")
    String lightScreenshotShowcaseFrameShadow() default "0 18px 50px rgba(0, 0, 0, 0.13)";

    @Property(label = "Screenshot Showcase Frame Shadow Hover (Dark)", description = "Browser frame hover shadow in dark theme")
    String darkScreenshotShowcaseFrameShadowHover() default "0 30px 90px rgba(0, 0, 0, 0.55)";

    @Property(label = "Screenshot Showcase Frame Shadow Hover (Light)", description = "Browser frame hover shadow in light theme")
    String lightScreenshotShowcaseFrameShadowHover() default "0 26px 70px rgba(0, 0, 0, 0.18)";

    @Property(label = "Screenshot Showcase Chrome Background (Dark)", description = "Browser chrome bar background in dark theme")
    String darkScreenshotShowcaseChromeBg() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Screenshot Showcase Chrome Background (Light)", description = "Browser chrome bar background in light theme")
    String lightScreenshotShowcaseChromeBg() default "rgba(0, 0, 0, 0.04)";

    @Property(label = "Screenshot Showcase Dot Color (Dark)", description = "Browser chrome traffic-light dot colour in dark theme")
    String darkScreenshotShowcaseDotColor() default "rgba(255, 255, 255, 0.22)";

    @Property(label = "Screenshot Showcase Dot Color (Light)", description = "Browser chrome traffic-light dot colour in light theme")
    String lightScreenshotShowcaseDotColor() default "rgba(0, 0, 0, 0.16)";

    @Property(label = "Screenshot Showcase URL Background (Dark)", description = "Browser chrome address pill background in dark theme")
    String darkScreenshotShowcaseUrlBg() default "rgba(0, 0, 0, 0.3)";

    @Property(label = "Screenshot Showcase URL Background (Light)", description = "Browser chrome address pill background in light theme")
    String lightScreenshotShowcaseUrlBg() default "rgba(0, 0, 0, 0.05)";

    @Property(label = "Screenshot Showcase URL Text (Dark)", description = "Browser chrome address text colour in dark theme")
    String darkScreenshotShowcaseUrlText() default "rgba(255, 255, 255, 0.6)";

    @Property(label = "Screenshot Showcase URL Text (Light)", description = "Browser chrome address text colour in light theme")
    String lightScreenshotShowcaseUrlText() default "rgba(0, 0, 0, 0.55)";

    @Property(label = "Screenshot Showcase Media Background (Dark)", description = "Letterbox background behind the screenshot in dark theme")
    String darkScreenshotShowcaseMediaBg() default "#0e0e14";

    @Property(label = "Screenshot Showcase Media Background (Light)", description = "Letterbox background behind the screenshot in light theme")
    String lightScreenshotShowcaseMediaBg() default "#f3f4f6";

    @Property(label = "Screenshot Showcase Badge Background (Dark)", description = "Caption badge background in dark theme")
    String darkScreenshotShowcaseBadgeBg() default "rgba(74, 222, 128, 0.13)";

    @Property(label = "Screenshot Showcase Badge Background (Light)", description = "Caption badge background in light theme")
    String lightScreenshotShowcaseBadgeBg() default "rgba(5, 150, 105, 0.1)";

    @Property(label = "Screenshot Showcase Badge Text (Dark)", description = "Caption badge text colour in dark theme")
    String darkScreenshotShowcaseBadgeText() default "#4ade80";

    @Property(label = "Screenshot Showcase Badge Text (Light)", description = "Caption badge text colour in light theme")
    String lightScreenshotShowcaseBadgeText() default "#047857";
}

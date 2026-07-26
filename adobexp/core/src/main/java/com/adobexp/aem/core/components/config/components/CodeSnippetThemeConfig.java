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
 * Context-Aware Configuration for Code Snippet component theme variables.
 *
 * The code shell itself stays dark in both themes, matching editor conventions, so
 * only the surrounding section and shell chrome differ between dark and light.
 */
@Configuration(
        label = "AdobeXP - Code Snippet Theme Configuration",
        description = "Context-Aware Configuration for Code Snippet component CSS variables"
)
public @interface CodeSnippetThemeConfig {

    @Property(label = "Code Snippet Background (Dark)", description = "Section background (With Background style) in dark theme")
    String darkCodeSnippetBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg, #1e1e1e) 100%)";

    @Property(label = "Code Snippet Background (Light)", description = "Section background (With Background style) in light theme")
    String lightCodeSnippetBg() default "linear-gradient(180deg, var(--site-body-bg, #ffffff) 0%, rgba(0, 0, 0, 0.03) 100%)";

    @Property(label = "Code Snippet Muted Background (Dark)", description = "Section background (Muted style) in dark theme")
    String darkCodeSnippetMutedBg() default "var(--site-body-bg, #1e1e1e)";

    @Property(label = "Code Snippet Muted Background (Light)", description = "Section background (Muted style) in light theme")
    String lightCodeSnippetMutedBg() default "var(--site-body-bg, #ffffff)";

    @Property(label = "Code Snippet Shell Background (Dark)", description = "Code viewer shell background in dark theme")
    String darkCodeSnippetShellBg() default "#16161d";

    @Property(label = "Code Snippet Shell Background (Light)", description = "Code viewer shell background in light theme")
    String lightCodeSnippetShellBg() default "#1b1b25";

    @Property(label = "Code Snippet Shell Border (Dark)", description = "Code viewer shell border in dark theme")
    String darkCodeSnippetShellBorder() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Code Snippet Shell Border (Light)", description = "Code viewer shell border in light theme")
    String lightCodeSnippetShellBorder() default "rgba(0, 0, 0, 0.14)";

    @Property(label = "Code Snippet Tab Bar Background (Dark)", description = "Tab bar background in dark theme")
    String darkCodeSnippetBarBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Code Snippet Tab Bar Background (Light)", description = "Tab bar background in light theme")
    String lightCodeSnippetBarBg() default "rgba(255, 255, 255, 0.05)";

    @Property(label = "Code Snippet Text (Dark)", description = "Base code text colour in dark theme")
    String darkCodeSnippetText() default "#e6e6ef";

    @Property(label = "Code Snippet Text (Light)", description = "Base code text colour in light theme")
    String lightCodeSnippetText() default "#e6e6ef";

    @Property(label = "Code Snippet Tab Text (Dark)", description = "Inactive tab label colour in dark theme")
    String darkCodeSnippetTabText() default "rgba(255, 255, 255, 0.6)";

    @Property(label = "Code Snippet Tab Text (Light)", description = "Inactive tab label colour in light theme")
    String lightCodeSnippetTabText() default "rgba(255, 255, 255, 0.6)";

    @Property(label = "Code Snippet Tab Hover Background (Dark)", description = "Tab hover background in dark theme")
    String darkCodeSnippetTabHoverBg() default "rgba(255, 255, 255, 0.07)";

    @Property(label = "Code Snippet Tab Hover Background (Light)", description = "Tab hover background in light theme")
    String lightCodeSnippetTabHoverBg() default "rgba(255, 255, 255, 0.07)";

    @Property(label = "Code Snippet Tab Active Background (Dark)", description = "Active tab background in dark theme")
    String darkCodeSnippetTabActiveBg() default "rgba(255, 255, 255, 0.13)";

    @Property(label = "Code Snippet Tab Active Background (Light)", description = "Active tab background in light theme")
    String lightCodeSnippetTabActiveBg() default "rgba(255, 255, 255, 0.13)";

    @Property(label = "Code Snippet Tab Active Text (Dark)", description = "Active tab label colour in dark theme")
    String darkCodeSnippetTabActiveText() default "#ffffff";

    @Property(label = "Code Snippet Tab Active Text (Light)", description = "Active tab label colour in light theme")
    String lightCodeSnippetTabActiveText() default "#ffffff";

    @Property(label = "Code Snippet Copy Button Background (Dark)", description = "Copy button background in dark theme")
    String darkCodeSnippetCopyBg() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Code Snippet Copy Button Background (Light)", description = "Copy button background in light theme")
    String lightCodeSnippetCopyBg() default "rgba(255, 255, 255, 0.08)";

    @Property(label = "Code Snippet Copy Button Hover Background (Dark)", description = "Copy button hover background in dark theme")
    String darkCodeSnippetCopyHoverBg() default "rgba(255, 255, 255, 0.16)";

    @Property(label = "Code Snippet Copy Button Hover Background (Light)", description = "Copy button hover background in light theme")
    String lightCodeSnippetCopyHoverBg() default "rgba(255, 255, 255, 0.16)";

    @Property(label = "Code Snippet Copied Color (Dark)", description = "Copy confirmation colour in dark theme")
    String darkCodeSnippetCopiedColor() default "#4ade80";

    @Property(label = "Code Snippet Copied Color (Light)", description = "Copy confirmation colour in light theme")
    String lightCodeSnippetCopiedColor() default "#4ade80";

    @Property(label = "Code Snippet Gutter Color (Dark)", description = "Line number colour in dark theme")
    String darkCodeSnippetGutterColor() default "rgba(255, 255, 255, 0.25)";

    @Property(label = "Code Snippet Gutter Color (Light)", description = "Line number colour in light theme")
    String lightCodeSnippetGutterColor() default "rgba(255, 255, 255, 0.25)";

    @Property(label = "Code Snippet Keyword Token (Dark)", description = "Syntax colour for keywords in dark theme")
    String darkCodeSnippetTokKey() default "#c792ea";

    @Property(label = "Code Snippet Keyword Token (Light)", description = "Syntax colour for keywords in light theme")
    String lightCodeSnippetTokKey() default "#c792ea";

    @Property(label = "Code Snippet String Token (Dark)", description = "Syntax colour for strings in dark theme")
    String darkCodeSnippetTokStr() default "#a5e075";

    @Property(label = "Code Snippet String Token (Light)", description = "Syntax colour for strings in light theme")
    String lightCodeSnippetTokStr() default "#a5e075";

    @Property(label = "Code Snippet Number Token (Dark)", description = "Syntax colour for numbers in dark theme")
    String darkCodeSnippetTokNum() default "#f78c6c";

    @Property(label = "Code Snippet Number Token (Light)", description = "Syntax colour for numbers in light theme")
    String lightCodeSnippetTokNum() default "#f78c6c";

    @Property(label = "Code Snippet Comment Token (Dark)", description = "Syntax colour for comments in dark theme")
    String darkCodeSnippetTokComment() default "#6b7789";

    @Property(label = "Code Snippet Comment Token (Light)", description = "Syntax colour for comments in light theme")
    String lightCodeSnippetTokComment() default "#6b7789";

    @Property(label = "Code Snippet Tag Token (Dark)", description = "Syntax colour for markup tags in dark theme")
    String darkCodeSnippetTokTag() default "#7fd1f7";

    @Property(label = "Code Snippet Tag Token (Light)", description = "Syntax colour for markup tags in light theme")
    String lightCodeSnippetTokTag() default "#7fd1f7";

    @Property(label = "Code Snippet Attribute Token (Dark)", description = "Syntax colour for attributes and JSON keys in dark theme")
    String darkCodeSnippetTokAttr() default "#ffcb6b";

    @Property(label = "Code Snippet Attribute Token (Light)", description = "Syntax colour for attributes and JSON keys in light theme")
    String lightCodeSnippetTokAttr() default "#ffcb6b";
}

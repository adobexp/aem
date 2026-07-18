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
 * Context-Aware Configuration for Data Table component theme variables.
 */
@Configuration(
        label = "AdobeXP - Data Table Theme Configuration",
        description = "Context-Aware Configuration for Data Table component CSS variables"
)
public @interface DataTableThemeConfig {

    @Property(label = "Data Table Background (Dark)", description = "Data table section background (Apply Background Color) in dark theme")
    String darkDataTableBg() default "linear-gradient(180deg, #0c0c0c 0%, var(--site-body-bg) 100%)";

    @Property(label = "Data Table Background (Light)", description = "Data table section background (Apply Background Color) in light theme")
    String lightDataTableBg() default "linear-gradient(180deg, var(--site-body-bg) 0%, var(--main-theme-color) 50%, var(--site-body-bg) 100%)";

    @Property(label = "Data Table Header Background (Dark)", description = "Table header cell background in dark theme")
    String darkDataTableHeaderBg() default "rgba(255, 255, 255, 0.06)";

    @Property(label = "Data Table Header Background (Light)", description = "Table header cell background in light theme")
    String lightDataTableHeaderBg() default "rgba(0, 0, 0, 0.04)";

    @Property(label = "Data Table Cell Border (Dark)", description = "Table cell bottom border in dark theme")
    String darkDataTableCellBorder() default "rgba(255, 255, 255, 0.1)";

    @Property(label = "Data Table Cell Border (Light)", description = "Table cell bottom border in light theme")
    String lightDataTableCellBorder() default "rgba(0, 0, 0, 0.1)";

    @Property(label = "Data Table Row Hover Background (Dark)", description = "Table row hover background in dark theme")
    String darkDataTableRowHoverBg() default "rgba(255, 255, 255, 0.04)";

    @Property(label = "Data Table Row Hover Background (Light)", description = "Table row hover background in light theme")
    String lightDataTableRowHoverBg() default "rgba(0, 0, 0, 0.03)";
}

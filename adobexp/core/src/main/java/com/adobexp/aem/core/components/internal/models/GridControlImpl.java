/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2024 Adobe
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

package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import com.adobexp.aem.core.components.models.GridControl;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {GridControl.class},
       resourceType = {GridControlImpl.RESOURCE_TYPE_V1})
public class GridControlImpl implements GridControl {

    protected static final String RESOURCE_TYPE_V1 = "adobexp/components/content/gridcontrol/v1/gridcontrol";

    private static final String DEFAULT_LAYOUT = "12";

    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String id;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String layoutColumns;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean applyBackgroundColor;

    private List<GridControlColumn> columns;

    @PostConstruct
    private void initModel() {
        columns = new ArrayList<>();
        
        // Determine the resource path (handle template structure)
        String resourcePath = resource.getPath();
        String currentPagePath = currentPage.getPath();
        
        if (currentPagePath.contains("initial") && resourcePath.contains("structure")) {
            resourcePath = currentPagePath + resourcePath.substring(resourcePath.indexOf("structure") + 9);
        } else if (currentPagePath.contains("/content") && resourcePath.contains("structure")) {
            resourcePath = currentPagePath + resourcePath.substring(resourcePath.indexOf("structure") + 9);
        }

        // Parse layout columns
        String layout = StringUtils.defaultIfBlank(layoutColumns, DEFAULT_LAYOUT);
        String[] columnWidths = layout.contains(",") ? layout.split(",") : new String[]{layout};

        // Parse column styles from multifield
        List<ColumnStyleConfig> columnStyles = parseColumnStyles();

        // Build column list
        for (int i = 0; i < columnWidths.length; i++) {
            String columnWidth = columnWidths[i].trim();
            ColumnStyleConfig styleConfig = (i < columnStyles.size()) ? columnStyles.get(i) : null;
            
            String columnClass = buildColumnClass(columnWidth, styleConfig);
            String columnStyle = buildColumnStyle(styleConfig);
            String parSysName = resourcePath + "/column-" + i;
            
            columns.add(new GridControlColumnImpl(columnClass, columnStyle, parSysName));
        }
    }

    /**
     * Parse column styles from the multifield child resource.
     */
    private List<ColumnStyleConfig> parseColumnStyles() {
        List<ColumnStyleConfig> styles = new ArrayList<>();
        
        Resource columnStylesResource = resource.getChild("columnStyles");
        if (columnStylesResource != null) {
            for (Resource childResource : columnStylesResource.getChildren()) {
                ValueMap props = childResource.getValueMap();
                boolean applyBackground = props.get("applyColumnBackground", false);
                String backgroundColor = props.get("columnBackgroundColor", String.class);
                styles.add(new ColumnStyleConfig(applyBackground, backgroundColor));
            }
        }
        
        return styles;
    }

    /**
     * Build the CSS class string for a column.
     */
    private String buildColumnClass(String desktopSize, ColumnStyleConfig styleConfig) {
        StringBuilder className = new StringBuilder();
        className.append("aem-GridColumn aem-GridColumn--default--").append(desktopSize);

        // Add responsive breakpoint classes
        switch (desktopSize) {
            case "8":
                className.append(" aem-GridColumn--tablet--12 aem-GridColumn--phone--12");
                break;
            case "6":
                className.append(" aem-GridColumn--tablet--12 aem-GridColumn--phone--12");
                break;
            case "4":
                className.append(" aem-GridColumn--tablet--5 aem-GridColumn--phone--12");
                break;
            case "3":
                className.append(" aem-GridColumn--tablet--6 aem-GridColumn--phone--6");
                break;
            default:
                // No additional responsive classes for other sizes
                break;
        }

        // Add background class if enabled and no custom color specified
        if (styleConfig != null && styleConfig.isApplyBackground() 
                && StringUtils.isBlank(styleConfig.getBackgroundColor())) {
            className.append(" aem-GridColumn--bg");
        }

        return className.toString();
    }

    /**
     * Build the inline style string for a column.
     * Uses 'background' property to support both colors and gradients.
     */
    private String buildColumnStyle(ColumnStyleConfig styleConfig) {
        if (styleConfig != null && styleConfig.isApplyBackground() 
                && StringUtils.isNotBlank(styleConfig.getBackgroundColor())) {
            return "background: " + styleConfig.getBackgroundColor() + ";";
        }
        return "";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isApplyBackgroundColor() {
        return applyBackgroundColor;
    }

    @Override
    public String getGridClass() {
        StringBuilder gridClass = new StringBuilder("aem-Grid aem-Grid--12");
        if (applyBackgroundColor) {
            gridClass.append(" aem-Grid--bg");
        }
        return gridClass.toString();
    }

    @Override
    public List<GridControlColumn> getColumns() {
        return columns;
    }

    /**
     * Internal class to hold column style configuration from multifield.
     */
    private static class ColumnStyleConfig {
        private final boolean applyBackground;
        private final String backgroundColor;

        ColumnStyleConfig(boolean applyBackground, String backgroundColor) {
            this.applyBackground = applyBackground;
            this.backgroundColor = backgroundColor;
        }

        boolean isApplyBackground() {
            return applyBackground;
        }

        String getBackgroundColor() {
            return backgroundColor;
        }
    }

    /**
     * Implementation of GridControlColumn.
     */
    private static class GridControlColumnImpl implements GridControlColumn {
        private final String columnClass;
        private final String columnStyle;
        private final String parSysName;

        GridControlColumnImpl(String columnClass, String columnStyle, String parSysName) {
            this.columnClass = columnClass;
            this.columnStyle = columnStyle;
            this.parSysName = parSysName;
        }

        @Override
        public String getColumnClass() {
            return columnClass;
        }

        @Override
        public String getColumnStyle() {
            return columnStyle;
        }

        @Override
        public String getParSysName() {
            return parSysName;
        }
    }
}

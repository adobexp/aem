/*
 *  Copyright 2024 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.ComparisonModel;

/**
 * Sling Model implementation for the Comparison component.
 * Reads configuration for three columns with multifield items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ComparisonModel.class,
    resourceType = ComparisonModelImpl.RESOURCE_TYPE
)
public class ComparisonModelImpl implements ComparisonModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/comparison";
    
    private static final String COLUMN1_ITEMS_NODE = "column1Items";
    private static final String COLUMN2_ITEMS_NODE = "column2Items";
    private static final String COLUMN3_ITEMS_NODE = "column3Items";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    // Column 1 properties
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column1HeadingNum;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column1Title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column1Description;

    // Column 2 properties
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column2HeadingNum;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column2Title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column2Description;

    // Column 3 properties
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column3HeadingNum;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column3Title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String column3Description;

    private List<ComparisonColumn> columns;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        columns = new ArrayList<>();
        
        if (resource != null) {
            // Parse Column 1
            List<ComparisonItem> column1Items = parseColumnItems(resource, COLUMN1_ITEMS_NODE);
            ComparisonColumnImpl column1 = new ComparisonColumnImpl(
                column1HeadingNum, column1Title, column1Description, column1Items);
            columns.add(column1);

            // Parse Column 2
            List<ComparisonItem> column2Items = parseColumnItems(resource, COLUMN2_ITEMS_NODE);
            ComparisonColumnImpl column2 = new ComparisonColumnImpl(
                column2HeadingNum, column2Title, column2Description, column2Items);
            columns.add(column2);

            // Parse Column 3
            List<ComparisonItem> column3Items = parseColumnItems(resource, COLUMN3_ITEMS_NODE);
            ComparisonColumnImpl column3 = new ComparisonColumnImpl(
                column3HeadingNum, column3Title, column3Description, column3Items);
            columns.add(column3);
        }
    }

    private Resource getResource() {
        if (currentResource != null) {
            return currentResource;
        }
        if (request != null) {
            return request.getResource();
        }
        return null;
    }

    /**
     * Parse column items from the specified child node.
     */
    private List<ComparisonItem> parseColumnItems(Resource componentResource, String nodeName) {
        List<ComparisonItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(nodeName);
        
        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ComparisonItemImpl item = parseItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single item resource.
     */
    private ComparisonItemImpl parseItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        String itemText = props.get("itemText", String.class);
        
        if (StringUtils.isBlank(itemText)) {
            return null;
        }
        
        return new ComparisonItemImpl(itemText);
    }

    // Getter implementations
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<ComparisonColumn> getColumns() {
        return columns;
    }

    @Override
    public boolean hasContent() {
        if (columns == null || columns.isEmpty()) {
            return false;
        }
        for (ComparisonColumn column : columns) {
            if (column.hasContent()) {
                return true;
            }
        }
        return false;
    }

    // Inner classes for comparison structures

    /**
     * Implementation of ComparisonColumn interface.
     */
    public static class ComparisonColumnImpl implements ComparisonColumn {
        private final String headingNum;
        private final String columnTitle;
        private final String description;
        private final List<ComparisonItem> items;

        public ComparisonColumnImpl(String headingNum, String columnTitle, String description, 
                                   List<ComparisonItem> items) {
            this.headingNum = headingNum;
            this.columnTitle = columnTitle;
            this.description = description;
            this.items = items != null ? items : Collections.emptyList();
        }

        @Override
        public String getHeadingNum() {
            return headingNum;
        }

        @Override
        public String getColumnTitle() {
            return columnTitle;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public List<ComparisonItem> getItems() {
            return items;
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotBlank(columnTitle) || (items != null && !items.isEmpty());
        }
    }

    /**
     * Implementation of ComparisonItem interface.
     */
    public static class ComparisonItemImpl implements ComparisonItem {
        private final String itemText;

        public ComparisonItemImpl(String itemText) {
            this.itemText = itemText;
        }

        @Override
        public String getItemText() {
            return itemText;
        }
    }
}


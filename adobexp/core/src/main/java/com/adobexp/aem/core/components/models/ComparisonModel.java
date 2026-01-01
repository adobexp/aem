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
package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Comparison component.
 * Provides access to comparison configuration including title and three column configurations.
 */
public interface ComparisonModel {

    /**
     * Gets the section title.
     * @return section title
     */
    String getTitle();

    /**
     * Gets the list of comparison columns.
     * @return list of comparison columns
     */
    List<ComparisonColumn> getColumns();

    /**
     * Checks if the component has valid content to display.
     * @return true if at least one column has content
     */
    boolean hasContent();

    /**
     * Represents a comparison column.
     */
    interface ComparisonColumn {
        /**
         * Gets the heading number (e.g., "01").
         * @return heading number or null if not set
         */
        String getHeadingNum();

        /**
         * Gets the column title.
         * @return column title
         */
        String getColumnTitle();

        /**
         * Gets the column description.
         * @return column description
         */
        String getDescription();

        /**
         * Gets the list of items for this column.
         * @return list of column items
         */
        List<ComparisonItem> getItems();

        /**
         * Checks if the column has content.
         * @return true if the column has a title or items
         */
        boolean hasContent();
    }

    /**
     * Represents a single item in a comparison column.
     */
    interface ComparisonItem {
        /**
         * Gets the item text.
         * @return item text
         */
        String getItemText();
    }
}


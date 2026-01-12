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

package com.adobexp.aem.core.components.models;

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;

/**
 * Defines the {@code GridControl} Sling Model used for the 
 * {@code /apps/adobexp/components/content/gridcontrol} component.
 */
@ConsumerType
public interface GridControl {

    /**
     * Returns the HTML ID attribute for the component.
     *
     * @return the component's ID, or null if not set
     */
    @Nullable
    default String getId() {
        return null;
    }

    /**
     * Returns whether the background color should be applied to the grid container.
     *
     * @return true if background color should be applied, false otherwise
     */
    default boolean isApplyBackgroundColor() {
        return false;
    }

    /**
     * Returns the CSS class for the grid container.
     *
     * @return the grid container CSS class
     */
    default String getGridClass() {
        return "aem-Grid aem-Grid--12";
    }

    /**
     * Returns the list of columns for this grid.
     *
     * @return list of GridControlColumn objects
     */
    default List<GridControlColumn> getColumns() {
        return null;
    }

    /**
     * Represents a single column in the grid.
     */
    interface GridControlColumn {

        /**
         * Returns the CSS class names for this column.
         *
         * @return the column's CSS classes
         */
        String getColumnClass();

        /**
         * Returns the inline style for this column.
         *
         * @return the column's inline style, or empty string if none
         */
        String getColumnStyle();

        /**
         * Returns the parsys resource name for this column.
         *
         * @return the parsys resource name/path
         */
        String getParSysName();
    }
}

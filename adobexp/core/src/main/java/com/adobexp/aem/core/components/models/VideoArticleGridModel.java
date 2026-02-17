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

package com.adobexp.aem.core.components.models;

/**
 * Sling Model interface for the Video Article Grid component.
 * Provides access to component configuration, labels, and article items
 * sourced from children of a configured content path.
 */
public interface VideoArticleGridModel {

    /**
     * Gets the component title (H2).
     * @return component title
     */
    String getComponentTitle();

    /**
     * Gets the component subtitle.
     * @return component subtitle
     */
    String getComponentSubTitle();

    /**
     * Gets the JSON string for the data-items attribute.
     * Contains an array of article objects sourced from the configured content path.
     * @return JSON array string of article items
     */
    String getDataItemsJson();

    /**
     * Gets the JSON string for the data-config attribute.
     * Contains grid configuration (cardsInSingleRow, playOnHover, etc.).
     * @return JSON object string of configuration
     */
    String getDataConfigJson();

    /**
     * Gets the JSON string for the data-labels attribute.
     * Contains all UI labels for search, pagination, and sorting.
     * @return JSON object string of labels
     */
    String getDataLabelsJson();

    /**
     * Checks if articles are available from the configured path.
     * @return true if any articles are found
     */
    boolean hasArticles();

    /**
     * Checks if background color should be applied.
     * @return true if background color is enabled
     */
    boolean isWithBackground();
}

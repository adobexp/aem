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

import java.util.List;

/**
 * Sling Model interface for the Metric Tiles component.
 */
public interface MetricTilesModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the accessible label for the section, defaults to {@code Key metrics}.
     */
    String getAriaLabel();

    /**
     * Returns the section style variant CSS class, e.g. {@code metric-tiles--with-bg}
     * or {@code metric-tiles--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    /**
     * Returns the grid modifier class, e.g. {@code metric-tiles__grid--4}.
     */
    String getGridClass();

    List<MetricTile> getTiles();

    boolean hasTiles();

    boolean hasContent();

    interface MetricTile {

        String getIcon();

        String getLabel();

        /**
         * Returns a clean numeric string suitable for {@code data-metric-value},
         * or an empty string when the authored value cannot be parsed as a number.
         */
        String getValue();

        String getPrefix();

        String getSuffix();

        /**
         * Returns the number of decimal places as a string, or {@code null} when
         * not authored so that the data attribute is omitted.
         */
        String getDecimals();

        /**
         * Returns {@code "true"} when large numbers should be abbreviated,
         * {@code null} otherwise so that the data attribute is omitted.
         */
        String getCompact();

        /**
         * Returns the validated per-tile accent colour, or {@code null} when the
         * authored value is not a recognisable CSS colour.
         */
        String getAccentColor();

        /**
         * Returns the ready-to-render inline style declaration that sets the
         * {@code --metric-tiles-face-accent} custom property, or {@code null}
         * when no valid accent colour was authored.
         */
        String getFaceStyle();

        String getCaption();
    }
}

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
 * Sling Model interface for the Cards component.
 */
public interface CardsModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the grid modifier class, e.g. {@code cards__grid--3}.
     */
    String getGridClass();

    /**
     * Returns the section style variant CSS class, e.g. {@code cards--with-bg} or {@code cards--muted}.
     * Empty string for the default (no modifier) style.
     */
    String getVariant();

    boolean isWithBackground();

    List<CardItem> getCardItems();

    boolean hasCards();

    boolean hasContent();

    interface CardItem {
        String getIcon();

        String getItemTitle();

        String getDescription();
    }
}

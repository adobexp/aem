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

package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.CardsModel;

/**
 * Sling Model implementation for the Cards component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = CardsModel.class,
    resourceType = CardsModelImpl.RESOURCE_TYPE
)
public class CardsModelImpl implements CardsModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/cards";

    private static final String CARD_ITEMS_NODE = "cardItems";
    private static final String DEFAULT_GRID = "3";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_GRID)
    private String gridVariant;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "with-bg")
    private String variant;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    private List<CardItem> cardItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            cardItems = parseCardItems(resource);
        } else {
            cardItems = Collections.emptyList();
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

    private List<CardItem> parseCardItems(Resource componentResource) {
        List<CardItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(CARD_ITEMS_NODE);

        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                CardItemImpl item = parseCardItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    private CardItemImpl parseCardItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        String icon = props.get("icon", String.class);
        String itemTitle = props.get("itemTitle", String.class);
        String description = props.get("description", String.class);

        if (StringUtils.isBlank(itemTitle) && StringUtils.isBlank(description)) {
            return null;
        }

        return new CardItemImpl(icon, itemTitle, description);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public String getGridClass() {
        String grid = StringUtils.isNotBlank(gridVariant) ? gridVariant : DEFAULT_GRID;
        if (!"2".equals(grid) && !"3".equals(grid) && !"4".equals(grid)) {
            grid = DEFAULT_GRID;
        }
        return "cards__grid--" + grid;
    }

    @Override
    public String getVariant() {
        if ("muted".equals(variant)) {
            return "cards--muted";
        }
        if ("default".equals(variant)) {
            return "";
        }
        if ("with-bg".equals(variant)) {
            return "cards--with-bg";
        }
        // Legacy fallback when variant is unset
        return "true".equals(withBackground) ? "cards--with-bg" : "";
    }

    @Override
    public boolean isWithBackground() {
        return "cards--with-bg".equals(getVariant());
    }

    @Override
    public List<CardItem> getCardItems() {
        return cardItems;
    }

    @Override
    public boolean hasCards() {
        return cardItems != null && !cardItems.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasCards();
    }

    public static class CardItemImpl implements CardItem {
        private final String icon;
        private final String itemTitle;
        private final String description;

        public CardItemImpl(String icon, String itemTitle, String description) {
            this.icon = icon;
            this.itemTitle = itemTitle;
            this.description = description;
        }

        @Override
        public String getIcon() {
            return icon;
        }

        @Override
        public String getItemTitle() {
            return itemTitle;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}

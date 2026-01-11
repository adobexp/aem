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
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.CountUpModel;

/**
 * Sling Model implementation for the Count Up component.
 * Reads multifield configurations for counter items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = CountUpModel.class,
    resourceType = CountUpModelImpl.RESOURCE_TYPE
)
public class CountUpModelImpl implements CountUpModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/count-up";
    
    private static final String COUNTER_ITEMS_NODE = "counterItems";
    private static final String DEFAULT_DURATION = "2000";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_DURATION)
    private String duration;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    private List<CounterItem> counterItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            counterItems = parseCounterItems(resource);
        } else {
            counterItems = Collections.emptyList();
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
     * Parse counter items from the counterItems child node.
     */
    private List<CounterItem> parseCounterItems(Resource componentResource) {
        List<CounterItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(COUNTER_ITEMS_NODE);
        
        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                CounterItemImpl item = parseCounterItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single counter item resource.
     */
    private CounterItemImpl parseCounterItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String startValue = props.get("startValue", "0");
        String endValue = props.get("endValue", "0");
        String unit = props.get("unit", "");
        String customUnit = props.get("customUnit", String.class);
        String decimals = props.get("decimals", "0");
        String label = props.get("label", String.class);
        
        // Use custom unit if provided, otherwise use standard unit
        String finalUnit = StringUtils.isNotBlank(customUnit) ? customUnit : unit;
        
        // At minimum, we need a label
        if (StringUtils.isBlank(label)) {
            return null;
        }
        
        return new CounterItemImpl(startValue, endValue, finalUnit, decimals, label);
    }

    // Getter implementations
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public String getDuration() {
        return duration != null ? duration : DEFAULT_DURATION;
    }

    @Override
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public List<CounterItem> getCounterItems() {
        return counterItems;
    }

    @Override
    public boolean hasCounterItems() {
        return counterItems != null && !counterItems.isEmpty();
    }

    /**
     * Implementation of CounterItem interface.
     */
    public static class CounterItemImpl implements CounterItem {
        private final String startValue;
        private final String endValue;
        private final String unit;
        private final String decimals;
        private final String label;

        public CounterItemImpl(String startValue, String endValue, String unit, String decimals, String label) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.unit = unit;
            this.decimals = decimals;
            this.label = label;
        }

        @Override
        public String getStartValue() {
            return startValue != null ? startValue : "0";
        }

        @Override
        public String getEndValue() {
            return endValue != null ? endValue : "0";
        }

        @Override
        public String getUnit() {
            return unit != null ? unit : "";
        }

        @Override
        public String getDecimals() {
            return decimals != null ? decimals : "0";
        }

        @Override
        public String getLabel() {
            return label;
        }
    }
}

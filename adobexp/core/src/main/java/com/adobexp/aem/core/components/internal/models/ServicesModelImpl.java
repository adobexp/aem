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

import com.adobexp.aem.core.components.models.ServicesModel;

/**
 * Sling Model implementation for the Services component.
 * Reads multifield configurations for service items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ServicesModel.class,
    resourceType = ServicesModelImpl.RESOURCE_TYPE
)
public class ServicesModelImpl implements ServicesModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/services";
    
    private static final String SERVICE_ITEMS_NODE = "serviceItems";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String servicesTitle;

    private List<ServiceItem> serviceItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            serviceItems = parseServiceItems(resource);
        } else {
            serviceItems = Collections.emptyList();
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
     * Parse service items from the serviceItems child node.
     */
    private List<ServiceItem> parseServiceItems(Resource componentResource) {
        List<ServiceItem> items = new ArrayList<>();
        Resource itemsResource = componentResource.getChild(SERVICE_ITEMS_NODE);
        
        if (itemsResource != null) {
            for (Resource itemResource : itemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ServiceItemImpl item = parseServiceItem(itemResource);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single service item resource.
     */
    private ServiceItemImpl parseServiceItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String serviceIcon = props.get("serviceIcon", String.class);
        String serviceHeadline = props.get("serviceHeadline", String.class);
        String serviceDescription = props.get("serviceDescription", String.class);
        
        // At minimum, we need the service headline
        if (StringUtils.isBlank(serviceHeadline)) {
            return null;
        }
        
        return new ServiceItemImpl(serviceIcon, serviceHeadline, serviceDescription);
    }

    // Getter implementations
    @Override
    public String getServicesTitle() {
        return servicesTitle;
    }

    @Override
    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    @Override
    public boolean hasServiceItems() {
        return serviceItems != null && !serviceItems.isEmpty();
    }

    /**
     * Implementation of ServiceItem interface.
     */
    public static class ServiceItemImpl implements ServiceItem {
        private final String serviceIcon;
        private final String serviceHeadline;
        private final String serviceDescription;

        public ServiceItemImpl(String serviceIcon, String serviceHeadline, String serviceDescription) {
            this.serviceIcon = serviceIcon;
            this.serviceHeadline = serviceHeadline;
            this.serviceDescription = serviceDescription;
        }

        @Override
        public String getServiceIcon() {
            return serviceIcon;
        }

        @Override
        public String getServiceHeadline() {
            return serviceHeadline;
        }

        @Override
        public String getServiceDescription() {
            return serviceDescription;
        }
    }
}



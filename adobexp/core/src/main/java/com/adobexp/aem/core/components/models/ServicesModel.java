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
 * Sling Model interface for the Services component.
 * Provides access to services configuration including title and service items.
 */
public interface ServicesModel {

    /**
     * Gets the services section title (H2).
     * @return services title
     */
    String getServicesTitle();

    /**
     * Gets the list of service items.
     * @return list of service items
     */
    List<ServiceItem> getServiceItems();

    /**
     * Checks if service items are configured.
     * @return true if any service item is configured
     */
    boolean hasServiceItems();

    /**
     * Represents a service item with icon, headline, and description.
     */
    interface ServiceItem {
        /**
         * Gets the service icon path (SVG from DAM).
         * @return service icon path
         */
        String getServiceIcon();

        /**
         * Gets the service headline text (H3).
         * @return service headline
         */
        String getServiceHeadline();

        /**
         * Gets the service description text.
         * @return service description
         */
        String getServiceDescription();
    }
}



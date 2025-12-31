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

import com.adobexp.aem.core.components.models.FooterModel;

/**
 * Sling Model implementation for the Footer component.
 * Reads nested multifield configurations for main menu and useful menu items.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = FooterModel.class,
    resourceType = FooterModelImpl.RESOURCE_TYPE
)
public class FooterModelImpl implements FooterModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/global/footer";
    
    private static final String MAIN_MENU_ITEMS_NODE = "mainMenuItems";
    private static final String USEFUL_MENU_ITEMS_NODE = "usefulMenuItems";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String footerHeading;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String footerLead;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String footerEmail;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String mainMenuTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String usefulMenuTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String privacyPolicyTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String privacyPolicyLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyrightText;

    private List<FooterMenuItem> mainMenuItems;
    private List<FooterMenuItem> usefulMenuItems;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            mainMenuItems = parseMainMenuItems(resource);
            usefulMenuItems = parseUsefulMenuItems(resource);
        } else {
            mainMenuItems = Collections.emptyList();
            usefulMenuItems = Collections.emptyList();
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
     * Parse main menu items from the mainMenuItems child node.
     */
    private List<FooterMenuItem> parseMainMenuItems(Resource componentResource) {
        List<FooterMenuItem> items = new ArrayList<>();
        Resource menuItemsResource = componentResource.getChild(MAIN_MENU_ITEMS_NODE);
        
        if (menuItemsResource != null) {
            for (Resource itemResource : menuItemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                MainMenuItemImpl menuItem = parseMainMenuItem(itemResource);
                if (menuItem != null) {
                    items.add(menuItem);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single main menu item resource.
     */
    private MainMenuItemImpl parseMainMenuItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String menuItemTitle = props.get("menuItemTitle", String.class);
        String menuItemLink = props.get("menuItemLink", String.class);
        String menuItemExternal = props.get("menuItemExternal", String.class);
        
        if (StringUtils.isBlank(menuItemTitle)) {
            return null;
        }
        
        return new MainMenuItemImpl(menuItemTitle, menuItemLink, "true".equals(menuItemExternal));
    }

    /**
     * Parse useful menu items from the usefulMenuItems child node.
     */
    private List<FooterMenuItem> parseUsefulMenuItems(Resource componentResource) {
        List<FooterMenuItem> items = new ArrayList<>();
        Resource menuItemsResource = componentResource.getChild(USEFUL_MENU_ITEMS_NODE);
        
        if (menuItemsResource != null) {
            for (Resource itemResource : menuItemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                UsefulMenuItemImpl menuItem = parseUsefulMenuItem(itemResource);
                if (menuItem != null) {
                    items.add(menuItem);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single useful menu item resource.
     */
    private UsefulMenuItemImpl parseUsefulMenuItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String usefulItemTitle = props.get("usefulItemTitle", String.class);
        String usefulItemLink = props.get("usefulItemLink", String.class);
        String usefulItemExternal = props.get("usefulItemExternal", String.class);
        
        if (StringUtils.isBlank(usefulItemTitle)) {
            return null;
        }
        
        return new UsefulMenuItemImpl(usefulItemTitle, usefulItemLink, "true".equals(usefulItemExternal));
    }

    // Getter implementations
    @Override
    public String getFooterHeading() {
        return footerHeading;
    }

    @Override
    public String getFooterLead() {
        return footerLead;
    }

    @Override
    public String getFooterEmail() {
        return footerEmail;
    }

    @Override
    public String getMainMenuTitle() {
        return mainMenuTitle;
    }

    @Override
    public List<FooterMenuItem> getMainMenuItems() {
        return mainMenuItems;
    }

    @Override
    public boolean hasMainMenuItems() {
        return mainMenuItems != null && !mainMenuItems.isEmpty();
    }

    @Override
    public String getUsefulMenuTitle() {
        return usefulMenuTitle;
    }

    @Override
    public List<FooterMenuItem> getUsefulMenuItems() {
        return usefulMenuItems;
    }

    @Override
    public boolean hasUsefulMenuItems() {
        return usefulMenuItems != null && !usefulMenuItems.isEmpty();
    }

    @Override
    public String getPrivacyPolicyTitle() {
        return privacyPolicyTitle;
    }

    @Override
    public String getPrivacyPolicyLink() {
        return privacyPolicyLink;
    }

    @Override
    public String getCopyrightText() {
        return copyrightText;
    }

    // Inner classes for menu items

    /**
     * Implementation of FooterMenuItem interface for main menu items.
     */
    public static class MainMenuItemImpl implements FooterMenuItem {
        private final String menuItemTitle;
        private final String menuItemLink;
        private final boolean external;

        public MainMenuItemImpl(String menuItemTitle, String menuItemLink, boolean external) {
            this.menuItemTitle = menuItemTitle;
            this.menuItemLink = menuItemLink;
            this.external = external;
        }

        @Override
        public String getMenuItemTitle() {
            return menuItemTitle;
        }

        @Override
        public String getUsefulItemTitle() {
            return null; // Not applicable for main menu items
        }

        @Override
        public String getMenuItemLink() {
            return menuItemLink;
        }

        @Override
        public String getUsefulItemLink() {
            return null; // Not applicable for main menu items
        }

        @Override
        public boolean isExternal() {
            return external;
        }
    }

    /**
     * Implementation of FooterMenuItem interface for useful menu items.
     */
    public static class UsefulMenuItemImpl implements FooterMenuItem {
        private final String usefulItemTitle;
        private final String usefulItemLink;
        private final boolean external;

        public UsefulMenuItemImpl(String usefulItemTitle, String usefulItemLink, boolean external) {
            this.usefulItemTitle = usefulItemTitle;
            this.usefulItemLink = usefulItemLink;
            this.external = external;
        }

        @Override
        public String getMenuItemTitle() {
            return null; // Not applicable for useful menu items
        }

        @Override
        public String getUsefulItemTitle() {
            return usefulItemTitle;
        }

        @Override
        public String getMenuItemLink() {
            return null; // Not applicable for useful menu items
        }

        @Override
        public String getUsefulItemLink() {
            return usefulItemLink;
        }

        @Override
        public boolean isExternal() {
            return external;
        }
    }
}


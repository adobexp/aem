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

import com.adobexp.aem.core.components.models.HeaderModel;

/**
 * Sling Model implementation for the Header component.
 * Reads nested multifield configurations for menu items and menu options.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = HeaderModel.class,
    resourceType = HeaderModelImpl.RESOURCE_TYPE
)
public class HeaderModelImpl implements HeaderModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/global/header";
    
    private static final String MENU_ITEMS_NODE = "menuItems";
    private static final String MENU_OPTIONS_NODE = "menuOptions";
    private static final String SUB_MENU_ITEMS_NODE = "subMenuItems";
    private static final String LEVEL3_MENU_ITEMS_NODE = "level3MenuItems";
    private static final String ARTICLE_TEASERS_NODE = "articleTeasers";
    
    private static final String TYPE_LEAF = "leaf";
    private static final String TYPE_CONTAINER = "container";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logoDarkImage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logoDarkAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logoLightImage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logoLightAlt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logoLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String headerTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String headerSubtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Follow us")
    private String socialSectionTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String twitterLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String facebookLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String instagramLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkedinLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String pinterestLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String youtubeLink;

    private List<MenuItem> menuItems;
    private List<MenuOption> menuOptions;
    private List<ArticleTeaser> articleTeasers;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            menuItems = parseMenuItems(resource);
            menuOptions = parseMenuOptions(resource);
            articleTeasers = parseArticleTeasers(resource);
        } else {
            menuItems = Collections.emptyList();
            menuOptions = Collections.emptyList();
            articleTeasers = Collections.emptyList();
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
     * Parse menu items from the menuItems child node.
     */
    private List<MenuItem> parseMenuItems(Resource componentResource) {
        List<MenuItem> items = new ArrayList<>();
        Resource menuItemsResource = componentResource.getChild(MENU_ITEMS_NODE);
        
        if (menuItemsResource != null) {
            for (Resource itemResource : menuItemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                MenuItemImpl menuItem = parseMenuItem(itemResource);
                if (menuItem != null) {
                    items.add(menuItem);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single menu item resource.
     */
    private MenuItemImpl parseMenuItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String menuItemType = props.get("menuItemType", String.class);
        String menuTitle = props.get("menuTitle", String.class);
        String menuDescription = props.get("menuDescription", String.class);
        String menuLink = props.get("menuLink", String.class);
        
        if (StringUtils.isBlank(menuTitle)) {
            return null;
        }
        
        List<SubMenuItem> subMenuItems = Collections.emptyList();
        if (TYPE_CONTAINER.equals(menuItemType)) {
            subMenuItems = parseSubMenuItems(itemResource);
        }
        
        return new MenuItemImpl(menuItemType, menuTitle, menuDescription, menuLink, subMenuItems);
    }

    /**
     * Parse sub menu items from a parent menu item.
     */
    private List<SubMenuItem> parseSubMenuItems(Resource parentResource) {
        List<SubMenuItem> items = new ArrayList<>();
        Resource subMenuItemsResource = parentResource.getChild(SUB_MENU_ITEMS_NODE);
        
        if (subMenuItemsResource != null) {
            for (Resource itemResource : subMenuItemsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                SubMenuItemImpl subMenuItem = parseSubMenuItem(itemResource);
                if (subMenuItem != null) {
                    items.add(subMenuItem);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single sub menu item resource.
     */
    private SubMenuItemImpl parseSubMenuItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String subMenuItemType = props.get("subMenuItemType", String.class);
        String subMenuTitle = props.get("subMenuTitle", String.class);
        String subMenuDescription = props.get("subMenuDescription", String.class);
        String subMenuLink = props.get("subMenuLink", String.class);
        
        if (StringUtils.isBlank(subMenuTitle)) {
            return null;
        }
        
        List<Level3MenuItem> level3Items = Collections.emptyList();
        if (TYPE_CONTAINER.equals(subMenuItemType)) {
            level3Items = parseLevel3MenuItems(itemResource);
        }
        
        return new SubMenuItemImpl(subMenuItemType, subMenuTitle, subMenuDescription, subMenuLink, level3Items);
    }

    /**
     * Parse Level 3 menu items from a parent sub menu item.
     */
    private List<Level3MenuItem> parseLevel3MenuItems(Resource parentResource) {
        List<Level3MenuItem> items = new ArrayList<>();
        Resource level3Resource = parentResource.getChild(LEVEL3_MENU_ITEMS_NODE);
        
        if (level3Resource != null) {
            for (Resource itemResource : level3Resource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                Level3MenuItemImpl level3Item = parseLevel3MenuItem(itemResource);
                if (level3Item != null) {
                    items.add(level3Item);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single Level 3 menu item resource.
     */
    private Level3MenuItemImpl parseLevel3MenuItem(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();
        
        String level3MenuTitle = props.get("level3MenuTitle", String.class);
        String level3MenuDescription = props.get("level3MenuDescription", String.class);
        String level3MenuLink = props.get("level3MenuLink", String.class);
        
        if (StringUtils.isBlank(level3MenuTitle)) {
            return null;
        }
        
        return new Level3MenuItemImpl(level3MenuTitle, level3MenuDescription, level3MenuLink);
    }

    /**
     * Parse menu options from the menuOptions child node.
     */
    private List<MenuOption> parseMenuOptions(Resource componentResource) {
        List<MenuOption> options = new ArrayList<>();
        Resource menuOptionsResource = componentResource.getChild(MENU_OPTIONS_NODE);
        
        if (menuOptionsResource != null) {
            for (Resource optionResource : menuOptionsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (optionResource.getName().startsWith("jcr:")) {
                    continue;
                }
                MenuOptionImpl menuOption = parseMenuOption(optionResource);
                if (menuOption != null) {
                    options.add(menuOption);
                }
            }
        }
        
        return options;
    }

    /**
     * Parse a single menu option resource.
     */
    private MenuOptionImpl parseMenuOption(Resource optionResource) {
        ValueMap props = optionResource.getValueMap();
        
        String optionTitle = props.get("optionTitle", String.class);
        String optionDescription = props.get("optionDescription", String.class);
        String optionLink = props.get("optionLink", String.class);
        String optionNewTab = props.get("optionNewTab", String.class);
        
        if (StringUtils.isBlank(optionTitle)) {
            return null;
        }
        
        return new MenuOptionImpl(optionTitle, optionDescription, optionLink, "true".equals(optionNewTab));
    }

    /**
     * Parse article teasers from the articleTeasers child node.
     */
    private List<ArticleTeaser> parseArticleTeasers(Resource componentResource) {
        List<ArticleTeaser> teasers = new ArrayList<>();
        Resource articleTeasersResource = componentResource.getChild(ARTICLE_TEASERS_NODE);
        
        if (articleTeasersResource != null) {
            for (Resource teaserResource : articleTeasersResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (teaserResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ArticleTeaserImpl articleTeaser = parseArticleTeaser(teaserResource);
                if (articleTeaser != null) {
                    teasers.add(articleTeaser);
                }
            }
        }
        
        return teasers;
    }

    /**
     * Parse a single article teaser resource.
     */
    private ArticleTeaserImpl parseArticleTeaser(Resource teaserResource) {
        ValueMap props = teaserResource.getValueMap();
        
        String articleTitle = props.get("articleTitle", String.class);
        String articleDescription = props.get("articleDescription", String.class);
        String articleLink = props.get("articleLink", String.class);
        String articleImage = props.get("articleImage", String.class);
        String articleImageAlt = props.get("articleImageAlt", String.class);
        
        if (StringUtils.isBlank(articleTitle)) {
            return null;
        }
        
        return new ArticleTeaserImpl(articleTitle, articleDescription, articleLink, articleImage, articleImageAlt);
    }

    // Getter implementations
    @Override
    public String getLogoDarkImage() {
        return logoDarkImage;
    }

    @Override
    public String getLogoDarkAlt() {
        return logoDarkAlt;
    }

    @Override
    public String getLogoLightImage() {
        return logoLightImage;
    }

    @Override
    public String getLogoLightAlt() {
        return logoLightAlt;
    }

    @Override
    public String getLogoLink() {
        return logoLink;
    }

    @Override
    public String getHeaderTitle() {
        return headerTitle;
    }

    @Override
    public String getHeaderSubtitle() {
        return headerSubtitle;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public List<MenuOption> getMenuOptions() {
        return menuOptions;
    }

    @Override
    public String getSocialSectionTitle() {
        return socialSectionTitle;
    }

    @Override
    public String getTwitterLink() {
        return twitterLink;
    }

    @Override
    public String getFacebookLink() {
        return facebookLink;
    }

    @Override
    public String getInstagramLink() {
        return instagramLink;
    }

    @Override
    public String getLinkedinLink() {
        return linkedinLink;
    }

    @Override
    public String getPinterestLink() {
        return pinterestLink;
    }

    @Override
    public String getYoutubeLink() {
        return youtubeLink;
    }

    @Override
    public boolean hasSocialLinks() {
        return StringUtils.isNotBlank(twitterLink) 
            || StringUtils.isNotBlank(facebookLink)
            || StringUtils.isNotBlank(instagramLink)
            || StringUtils.isNotBlank(linkedinLink)
            || StringUtils.isNotBlank(pinterestLink)
            || StringUtils.isNotBlank(youtubeLink);
    }

    @Override
    public List<ArticleTeaser> getArticleTeasers() {
        return articleTeasers;
    }

    @Override
    public boolean hasArticleTeasers() {
        return articleTeasers != null && !articleTeasers.isEmpty();
    }

    // Inner classes for menu structures

    /**
     * Implementation of MenuItem interface.
     */
    public static class MenuItemImpl implements MenuItem {
        private final String menuItemType;
        private final String menuTitle;
        private final String menuDescription;
        private final String menuLink;
        private final List<SubMenuItem> subMenuItems;

        public MenuItemImpl(String menuItemType, String menuTitle, String menuDescription, 
                           String menuLink, List<SubMenuItem> subMenuItems) {
            this.menuItemType = menuItemType;
            this.menuTitle = menuTitle;
            this.menuDescription = menuDescription;
            this.menuLink = menuLink;
            this.subMenuItems = subMenuItems != null ? subMenuItems : Collections.emptyList();
        }

        @Override
        public String getMenuItemType() {
            return menuItemType;
        }

        @Override
        public String getMenuTitle() {
            return menuTitle;
        }

        @Override
        public String getMenuDescription() {
            return menuDescription;
        }

        @Override
        public String getMenuLink() {
            return menuLink;
        }

        @Override
        public boolean isLeaf() {
            return TYPE_LEAF.equals(menuItemType);
        }

        @Override
        public boolean isContainer() {
            return TYPE_CONTAINER.equals(menuItemType);
        }

        @Override
        public List<SubMenuItem> getSubMenuItems() {
            return subMenuItems;
        }
    }

    /**
     * Implementation of SubMenuItem interface.
     */
    public static class SubMenuItemImpl implements SubMenuItem {
        private final String subMenuItemType;
        private final String subMenuTitle;
        private final String subMenuDescription;
        private final String subMenuLink;
        private final List<Level3MenuItem> level3MenuItems;

        public SubMenuItemImpl(String subMenuItemType, String subMenuTitle, String subMenuDescription,
                              String subMenuLink, List<Level3MenuItem> level3MenuItems) {
            this.subMenuItemType = subMenuItemType;
            this.subMenuTitle = subMenuTitle;
            this.subMenuDescription = subMenuDescription;
            this.subMenuLink = subMenuLink;
            this.level3MenuItems = level3MenuItems != null ? level3MenuItems : Collections.emptyList();
        }

        @Override
        public String getSubMenuItemType() {
            return subMenuItemType;
        }

        @Override
        public String getSubMenuTitle() {
            return subMenuTitle;
        }

        @Override
        public String getSubMenuDescription() {
            return subMenuDescription;
        }

        @Override
        public String getSubMenuLink() {
            return subMenuLink;
        }

        @Override
        public boolean isLeaf() {
            return TYPE_LEAF.equals(subMenuItemType);
        }

        @Override
        public boolean isContainer() {
            return TYPE_CONTAINER.equals(subMenuItemType);
        }

        @Override
        public List<Level3MenuItem> getLevel3MenuItems() {
            return level3MenuItems;
        }
    }

    /**
     * Implementation of Level3MenuItem interface.
     */
    public static class Level3MenuItemImpl implements Level3MenuItem {
        private final String level3MenuTitle;
        private final String level3MenuDescription;
        private final String level3MenuLink;

        public Level3MenuItemImpl(String level3MenuTitle, String level3MenuDescription, String level3MenuLink) {
            this.level3MenuTitle = level3MenuTitle;
            this.level3MenuDescription = level3MenuDescription;
            this.level3MenuLink = level3MenuLink;
        }

        @Override
        public String getLevel3MenuTitle() {
            return level3MenuTitle;
        }

        @Override
        public String getLevel3MenuDescription() {
            return level3MenuDescription;
        }

        @Override
        public String getLevel3MenuLink() {
            return level3MenuLink;
        }
    }

    /**
     * Implementation of MenuOption interface.
     */
    public static class MenuOptionImpl implements MenuOption {
        private final String optionTitle;
        private final String optionDescription;
        private final String optionLink;
        private final boolean optionNewTab;

        public MenuOptionImpl(String optionTitle, String optionDescription, String optionLink, boolean optionNewTab) {
            this.optionTitle = optionTitle;
            this.optionDescription = optionDescription;
            this.optionLink = optionLink;
            this.optionNewTab = optionNewTab;
        }

        @Override
        public String getOptionTitle() {
            return optionTitle;
        }

        @Override
        public String getOptionDescription() {
            return optionDescription;
        }

        @Override
        public String getOptionLink() {
            return optionLink;
        }

        @Override
        public boolean isOptionNewTab() {
            return optionNewTab;
        }
    }

    /**
     * Implementation of ArticleTeaser interface.
     */
    public static class ArticleTeaserImpl implements ArticleTeaser {
        private final String articleTitle;
        private final String articleDescription;
        private final String articleLink;
        private final String articleImage;
        private final String articleImageAlt;

        public ArticleTeaserImpl(String articleTitle, String articleDescription, String articleLink, 
                                String articleImage, String articleImageAlt) {
            this.articleTitle = articleTitle;
            this.articleDescription = articleDescription;
            this.articleLink = articleLink;
            this.articleImage = articleImage;
            this.articleImageAlt = articleImageAlt;
        }

        @Override
        public String getArticleTitle() {
            return articleTitle;
        }

        @Override
        public String getArticleDescription() {
            return articleDescription;
        }

        @Override
        public String getArticleLink() {
            return articleLink;
        }

        @Override
        public String getArticleImage() {
            return articleImage;
        }

        @Override
        public String getArticleImageAlt() {
            return articleImageAlt;
        }
    }
}


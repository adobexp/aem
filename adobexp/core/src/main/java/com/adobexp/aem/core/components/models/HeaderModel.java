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
 * Sling Model interface for the Header component.
 * Provides access to header configuration including logo, menu items, menu options, and social links.
 */
public interface HeaderModel {

    /**
     * Gets the dark theme logo image path.
     * @return dark theme logo path
     */
    String getLogoDarkImage();

    /**
     * Gets the dark theme logo alt text.
     * @return dark theme logo alt text
     */
    String getLogoDarkAlt();

    /**
     * Gets the light theme logo image path.
     * @return light theme logo path
     */
    String getLogoLightImage();

    /**
     * Gets the light theme logo alt text.
     * @return light theme logo alt text
     */
    String getLogoLightAlt();

    /**
     * Gets the logo link URL.
     * @return logo link URL
     */
    String getLogoLink();

    /**
     * Gets the header title text.
     * @return header title
     */
    String getHeaderTitle();

    /**
     * Gets the header subtitle text.
     * @return header subtitle
     */
    String getHeaderSubtitle();

    /**
     * Gets the list of menu items (Level 1 navigation).
     * @return list of menu items
     */
    List<MenuItem> getMenuItems();

    /**
     * Gets the list of menu options (Column 2 options).
     * @return list of menu options
     */
    List<MenuOption> getMenuOptions();

    /**
     * Gets the social section title.
     * @return social section title
     */
    String getSocialSectionTitle();

    /**
     * Gets the Twitter/X link.
     * @return Twitter link
     */
    String getTwitterLink();

    /**
     * Gets the Facebook link.
     * @return Facebook link
     */
    String getFacebookLink();

    /**
     * Gets the Instagram link.
     * @return Instagram link
     */
    String getInstagramLink();

    /**
     * Gets the LinkedIn link.
     * @return LinkedIn link
     */
    String getLinkedinLink();

    /**
     * Gets the Pinterest link.
     * @return Pinterest link
     */
    String getPinterestLink();

    /**
     * Gets the YouTube link.
     * @return YouTube link
     */
    String getYoutubeLink();

    /**
     * Checks if social links are configured.
     * @return true if any social link is configured
     */
    boolean hasSocialLinks();

    /**
     * Represents a Level 1 menu item.
     */
    interface MenuItem {
        /**
         * Gets the menu item type ('leaf' or 'container').
         * @return menu item type
         */
        String getMenuItemType();

        /**
         * Gets the menu title.
         * @return menu title
         */
        String getMenuTitle();

        /**
         * Gets the menu description.
         * @return menu description
         */
        String getMenuDescription();

        /**
         * Gets the menu link (for leaf items).
         * @return menu link
         */
        String getMenuLink();

        /**
         * Checks if this is a leaf menu item.
         * @return true if leaf item
         */
        boolean isLeaf();

        /**
         * Checks if this is a container menu item.
         * @return true if container item
         */
        boolean isContainer();

        /**
         * Gets the sub menu items (Level 2).
         * @return list of sub menu items
         */
        List<SubMenuItem> getSubMenuItems();
    }

    /**
     * Represents a Level 2 sub menu item.
     */
    interface SubMenuItem {
        /**
         * Gets the sub menu item type ('leaf' or 'container').
         * @return sub menu item type
         */
        String getSubMenuItemType();

        /**
         * Gets the sub menu title.
         * @return sub menu title
         */
        String getSubMenuTitle();

        /**
         * Gets the sub menu description.
         * @return sub menu description
         */
        String getSubMenuDescription();

        /**
         * Gets the sub menu link (for leaf items).
         * @return sub menu link
         */
        String getSubMenuLink();

        /**
         * Checks if this is a leaf sub menu item.
         * @return true if leaf item
         */
        boolean isLeaf();

        /**
         * Checks if this is a container sub menu item.
         * @return true if container item
         */
        boolean isContainer();

        /**
         * Gets the Level 3 menu items.
         * @return list of Level 3 menu items
         */
        List<Level3MenuItem> getLevel3MenuItems();
    }

    /**
     * Represents a Level 3 menu item.
     */
    interface Level3MenuItem {
        /**
         * Gets the Level 3 menu title.
         * @return Level 3 menu title
         */
        String getLevel3MenuTitle();

        /**
         * Gets the Level 3 menu description.
         * @return Level 3 menu description
         */
        String getLevel3MenuDescription();

        /**
         * Gets the Level 3 menu link.
         * @return Level 3 menu link
         */
        String getLevel3MenuLink();
    }

    /**
     * Represents a menu option item (Column 2).
     */
    interface MenuOption {
        /**
         * Gets the option title.
         * @return option title
         */
        String getOptionTitle();

        /**
         * Gets the option description.
         * @return option description
         */
        String getOptionDescription();

        /**
         * Gets the option link.
         * @return option link
         */
        String getOptionLink();

        /**
         * Checks if the option should open in a new tab.
         * @return true if should open in new tab
         */
        boolean isOptionNewTab();
    }
}


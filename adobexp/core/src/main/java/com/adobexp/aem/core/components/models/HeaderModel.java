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
     * Gets the title text shown under the logo in the sidebar brand area.
     * Defaults to {@code Anamnesis} when not authored.
     * @return sidebar brand title
     */
    String getSidebarTitle();

    /**
     * Gets the list of menu items (Level 1 navigation) for the overlay variation.
     * @return list of menu items
     */
    List<MenuItem> getMenuItems();

    /**
     * Gets the list of sidebar menu items (Level 1 navigation) for the sidebar variation.
     * Leaf items render as independent links; container items render as accordions.
     * @return list of sidebar menu items
     */
    List<MenuItem> getSidebarMenuItems();

    /**
     * Gets the list of menu options (Column 2 options).
     * @return list of menu options
     */
    List<MenuOption> getMenuOptions();

    /**
     * Gets the list of article teasers (Column 3).
     * @return list of article teasers
     */
    List<ArticleTeaser> getArticleTeasers();

    /**
     * Checks if article teasers are configured.
     * @return true if any article teaser is configured
     */
    boolean hasArticleTeasers();

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
     * Gets the default theme mode for the page.
     * Accepted values: "dark", "light", "auto".
     * @return default theme string
     */
    String getDefaultTheme();

    /**
     * Gets the menu presentation variation.
     * Accepted values: {@code overlay} (fullscreen overlay) or {@code sidebar} (resizable left sidebar).
     * @return menu variation string
     */
    String getMenuVariant();

    /**
     * Whether the sidebar should start open by default on Desktop viewports.
     * Authored via the SideBar Menu Options dialog checkbox; defaults to {@code false}.
     * Only used when the visitor has no saved open/closed preference in localStorage.
     * @return {@code true} when the desktop sidebar should start open by default
     */
    boolean isSidebarDefaultOpen();

    /**
     * Whether sidebar Menu Container accordions start expanded.
     * Authored via the SideBar Menu Options dialog checkbox; defaults to {@code true}.
     * @return {@code true} when accordion groups should be open by default
     */
    boolean isSidebarAccordionExpanded();

    /**
     * @return {@code true} when the sidebar menu variation is selected
     */
    default boolean isSidebarMenu() {
        return "sidebar".equalsIgnoreCase(getMenuVariant());
    }

    /**
     * @return {@code true} when the overlay menu variation is selected (default)
     */
    default boolean isOverlayMenu() {
        return !isSidebarMenu();
    }

    /**
     * @return {@code true} when at least one Level-1 leaf menu item exists in the overlay menu
     */
    default boolean hasLeafMenuItems() {
        List<MenuItem> items = getMenuItems();
        if (items == null) {
            return false;
        }
        for (MenuItem item : items) {
            if (item != null && item.isLeaf()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} when at least one Level-1 leaf item exists in the sidebar menu
     */
    default boolean hasLeafSidebarMenuItems() {
        List<MenuItem> items = getSidebarMenuItems();
        if (items == null) {
            return false;
        }
        for (MenuItem item : items) {
            if (item != null && item.isLeaf()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the list of top navigation buttons displayed to the left of the theme toggle.
     * @return list of top nav buttons
     */
    List<TopNavButton> getTopNavButtons();

    /**
     * Checks if top navigation buttons are configured.
     * @return true if any top nav button is configured
     */
    boolean hasTopNavButtons();

    /**
     * Whether the language selector dropdown is enabled.
     * @return {@code true} when the language selector should be shown
     */
    boolean isEnableLanguageSelector();

    /**
     * Sibling language-root links for the current site branch.
     * Empty when the selector is disabled or language roots cannot be resolved.
     * @return list of language links
     */
    List<LanguageLink> getLanguageLinks();

    /**
     * Language code of the current page's language root (e.g. {@code EN}), for the selector toggle.
     * @return current language code, or empty string when unavailable
     */
    String getCurrentLanguageCode();

    /**
     * Represents a top navigation button displayed in the header.
     */
    interface TopNavButton {
        /**
         * Gets the button link (page path).
         * @return button link
         */
        String getButtonLink();

        /**
         * Gets the button label text.
         * @return button label
         */
        String getButtonLabel();

        /**
         * Checks if the button should open in a new tab.
         * @return true if should open in new tab
         */
        boolean isButtonNewTab();
    }

    /**
     * Represents a language-root link in the language selector.
     */
    interface LanguageLink {
        /**
         * Short language code (e.g. {@code EN}, {@code FR}).
         * @return language code
         */
        String getCode();

        /**
         * Display label (e.g. language display name).
         * @return language label
         */
        String getLabel();

        /**
         * Mapped href for the equivalent page in this language root.
         * @return page href ending with {@code .html}
         */
        String getHref();

        /**
         * Whether this link is the current language root.
         * @return {@code true} when this is the active language
         */
        boolean isCurrent();
    }

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

    /**
     * Represents an article teaser item (Column 3).
     */
    interface ArticleTeaser {
        /**
         * Gets the article title.
         * @return article title
         */
        String getArticleTitle();

        /**
         * Gets the article description.
         * @return article description
         */
        String getArticleDescription();

        /**
         * Gets the article link.
         * @return article link
         */
        String getArticleLink();

        /**
         * Gets the article teaser image path.
         * @return article image path
         */
        String getArticleImage();

        /**
         * Gets the article image alt text.
         * @return article image alt text
         */
        String getArticleImageAlt();
    }
}


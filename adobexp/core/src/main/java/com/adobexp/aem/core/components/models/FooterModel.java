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
 * Sling Model interface for the Footer component.
 * Provides access to footer configuration including heading, lead text, email,
 * main menu items, useful menu items, and legal/copyright information.
 */
public interface FooterModel {

    /**
     * Gets the footer heading text.
     * @return footer heading
     */
    String getFooterHeading();

    /**
     * Gets the footer lead text.
     * @return footer lead text
     */
    String getFooterLead();

    /**
     * Gets the footer email address.
     * @return footer email
     */
    String getFooterEmail();

    /**
     * Gets the main menu title.
     * @return main menu title
     */
    String getMainMenuTitle();

    /**
     * Gets the list of main menu items.
     * @return list of main menu items
     */
    List<FooterMenuItem> getMainMenuItems();

    /**
     * Checks if main menu items are configured.
     * @return true if any main menu item is configured
     */
    boolean hasMainMenuItems();

    /**
     * Gets the useful menu title.
     * @return useful menu title
     */
    String getUsefulMenuTitle();

    /**
     * Gets the list of useful menu items.
     * @return list of useful menu items
     */
    List<FooterMenuItem> getUsefulMenuItems();

    /**
     * Checks if useful menu items are configured.
     * @return true if any useful menu item is configured
     */
    boolean hasUsefulMenuItems();

    /**
     * Gets the privacy policy title.
     * @return privacy policy title
     */
    String getPrivacyPolicyTitle();

    /**
     * Gets the privacy policy link.
     * @return privacy policy link
     */
    String getPrivacyPolicyLink();

    /**
     * Gets the copyright text.
     * @return copyright text
     */
    String getCopyrightText();

    /**
     * Represents a footer menu item (for both main menu and useful menu).
     */
    interface FooterMenuItem {
        /**
         * Gets the menu item title.
         * @return menu item title
         */
        String getMenuItemTitle();

        /**
         * Gets the useful item title (for useful menu items).
         * @return useful item title
         */
        String getUsefulItemTitle();

        /**
         * Gets the menu item link.
         * @return menu item link
         */
        String getMenuItemLink();

        /**
         * Gets the useful item link (for useful menu items).
         * @return useful item link
         */
        String getUsefulItemLink();

        /**
         * Checks if this is an external link.
         * @return true if external link
         */
        boolean isExternal();
    }
}


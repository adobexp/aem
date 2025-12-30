/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2024 Adobe
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
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-header__editor";
    
    // Selectors for Level 1 menu items
    var level1MenuTypeSelector = '[data-cmp-header-dialog-hook="level1MenuType"]';
    var level1MenuLinkSelector = '[data-cmp-header-dialog-hook="level1MenuLink"]';
    var level1SubMenuItemsSelector = '[data-cmp-header-dialog-hook="level1SubMenuItems"]';
    
    // Selectors for Level 2 sub menu items
    var level2MenuTypeSelector = '[data-cmp-header-dialog-hook="level2MenuType"]';
    var level2MenuLinkSelector = '[data-cmp-header-dialog-hook="level2MenuLink"]';
    var level2Level3MenuItemsSelector = '[data-cmp-header-dialog-hook="level2Level3MenuItems"]';

    /**
     * Shows or hides fields based on the menu type selection
     * @param {HTMLElement} selectElement - The dropdown select element
     * @param {string} linkSelector - The selector for the link field to show/hide
     * @param {string} subMenuSelector - The selector for the sub menu items section to show/hide
     */
    function updateMenuFieldsVisibility(selectElement, linkSelector, subMenuSelector) {
        var $select = $(selectElement);
        var $multifieldItem = $select.closest("coral-multifield-item");
        var $linkField = $multifieldItem.find(linkSelector);
        var $subMenuSection = $multifieldItem.find(subMenuSelector);
        
        var selectedValue = $select.val();
        
        // Handle Menu Link field visibility
        if ($linkField.length) {
            var $linkWrapper = $linkField.closest(".coral-Form-fieldwrapper");
            
            if (selectedValue === "container") {
                // Hide the Menu Link field when "Menu Container" is selected
                $linkWrapper.hide();
            } else {
                // Show the Menu Link field when "Leaf Menu Option" is selected
                $linkWrapper.show();
            }
        }
        
        // Handle Sub Menu Items section visibility
        if ($subMenuSection.length) {
            // Find the parent container that includes the multifield and its label/wrapper
            var $subMenuWrapper = $subMenuSection.closest(".coral-Form-fieldwrapper");
            
            // If no fieldwrapper found, try to hide the entire container
            if (!$subMenuWrapper.length) {
                $subMenuWrapper = $subMenuSection.parent();
            }
            
            if (selectedValue === "leaf") {
                // Hide the Sub Menu Items section when "Leaf Menu Option" is selected
                $subMenuWrapper.hide();
            } else {
                // Show the Sub Menu Items section when "Menu Container" is selected
                $subMenuWrapper.show();
            }
        }
    }

    /**
     * Initialize visibility for all existing menu items in the dialog
     * @param {jQuery} $dialogContent - The dialog content element
     */
    function initializeAllMenuItems($dialogContent) {
        // Initialize Level 1 menu items
        $dialogContent.find(level1MenuTypeSelector).each(function() {
            updateMenuFieldsVisibility(this, level1MenuLinkSelector, level1SubMenuItemsSelector);
        });
        
        // Initialize Level 2 sub menu items
        $dialogContent.find(level2MenuTypeSelector).each(function() {
            updateMenuFieldsVisibility(this, level2MenuLinkSelector, level2Level3MenuItemsSelector);
        });
    }

    /**
     * Handle change events on menu type dropdowns
     * @param {Event} event - The change event
     */
    function handleMenuTypeChange(event) {
        var $target = $(event.target);
        
        // Check if this is a Level 1 menu type dropdown
        if ($target.is(level1MenuTypeSelector)) {
            updateMenuFieldsVisibility(event.target, level1MenuLinkSelector, level1SubMenuItemsSelector);
        }
        // Check if this is a Level 2 sub menu type dropdown
        else if ($target.is(level2MenuTypeSelector)) {
            updateMenuFieldsVisibility(event.target, level2MenuLinkSelector, level2Level3MenuItemsSelector);
        }
    }

    /**
     * Handle multifield item addition - initialize visibility for newly added items
     * @param {Event} event - The coral-collection:add event
     */
    function handleMultifieldAdd(event) {
        // Small delay to ensure the DOM is updated
        setTimeout(function() {
            // Find any new menu type dropdowns in the added item and initialize them
            var $addedItem = $(event.detail.item);
            
            // Check for Level 1 dropdowns
            $addedItem.find(level1MenuTypeSelector).each(function() {
                updateMenuFieldsVisibility(this, level1MenuLinkSelector, level1SubMenuItemsSelector);
            });
            
            // Check for Level 2 dropdowns
            $addedItem.find(level2MenuTypeSelector).each(function() {
                updateMenuFieldsVisibility(this, level2MenuLinkSelector, level2Level3MenuItemsSelector);
            });
        }, 100);
    }

    // Listen for dialog-loaded event
    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        
        if ($dialogContent.length > 0) {
            // Initialize visibility for all existing menu items
            initializeAllMenuItems($dialogContent);
            
            // Listen for change events on menu type dropdowns
            $dialogContent.on("change", level1MenuTypeSelector + ", " + level2MenuTypeSelector, handleMenuTypeChange);
            
            // Listen for multifield item additions
            $dialogContent.on("coral-collection:add", "coral-multifield", handleMultifieldAdd);
        }
    });

})(jQuery);

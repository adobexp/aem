/**
 * Marquee Carousel Dialog Editor JavaScript
 * Handles conditional visibility of fields based on image source selection (Manual/DAM Folder)
 * 
 * When "Add Images Manually" is selected: Shows the multifield for manual image addition
 * When "Select DAM Folder" is selected: Shows the DAM folder path field
 */
(function(document, $, Coral) {
    'use strict';

    // CSS class for hiding elements
    var HIDDEN_CLASS = 'cmp-marquee-carousel--hidden';

    // Add CSS rule for hiding elements
    var style = document.createElement('style');
    style.textContent = '.' + HIDDEN_CLASS + ' { display: none !important; }';
    document.head.appendChild(style);

    /**
     * Find a field's container by the field's name attribute
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} fieldName - The name attribute value (e.g., "./imageSource")
     * @returns {jQuery} The field container element
     */
    function getFieldContainer(dialog, fieldName) {
        var field = dialog.find('[name="' + fieldName + '"]');
        
        if (!field.length) {
            console.warn('[MarqueeCarousel] Field not found:', fieldName);
            return $();
        }

        // In AEM dialogs, fields are wrapped in .coral-Form-fieldwrapper
        // which contains both the label and the input
        var wrapper = field.closest('.coral-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }

        // For Coral 3 dialogs
        wrapper = field.closest('.coral3-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }

        // Fallback: get the coral component and its parent
        var coral = field.closest('coral-select, coral-pathfield, coral-textfield, coral-numberfield, coral-checkbox');
        if (coral.length) {
            return coral.parent();
        }

        return field.parent();
    }

    /**
     * Find the multifield container element
     * This finds the coral-multifield element and its wrapping container
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} multifieldFieldName - The name attribute on the field inside multifield (e.g., "./carouselImages")
     * @returns {jQuery} The multifield's parent container element
     */
    function getMultifieldContainer(dialog, multifieldFieldName) {
        // Find the field inside multifield by its name
        var field = dialog.find('coral-multifield [name="' + multifieldFieldName + '"]');
        
        if (!field.length) {
            console.warn('[MarqueeCarousel] Multifield field not found:', multifieldFieldName);
            return $();
        }

        // Get the coral-multifield element
        var multifield = field.closest('coral-multifield');
        if (!multifield.length) {
            console.warn('[MarqueeCarousel] coral-multifield not found for:', multifieldFieldName);
            return $();
        }

        // Get the parent container that wraps the multifield (includes label)
        // First try coral-Form-fieldwrapper
        var wrapper = multifield.closest('.coral-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }

        // Try coral3-Form-fieldwrapper
        wrapper = multifield.closest('.coral3-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }

        // Get the foundation container that wraps the multifield
        // This is typically the container created by granite/ui/components/foundation/container
        var foundationContainer = multifield.parent();
        if (foundationContainer.length) {
            // Go up one more level to include the container wrapper
            var containerWrapper = foundationContainer.parent();
            if (containerWrapper.length) {
                return containerWrapper;
            }
            return foundationContainer;
        }

        return multifield;
    }

    /**
     * Show a field by removing the hidden class
     */
    function showField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.removeClass(HIDDEN_CLASS);
            console.log('[MarqueeCarousel] Showing:', fieldName);
        }
    }

    /**
     * Hide a field by adding the hidden class
     */
    function hideField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.addClass(HIDDEN_CLASS);
            console.log('[MarqueeCarousel] Hiding:', fieldName);
        }
    }

    /**
     * Show the multifield by removing the hidden class from its container
     */
    function showMultifield(dialog, multifieldFieldName) {
        var container = getMultifieldContainer(dialog, multifieldFieldName);
        if (container.length) {
            container.removeClass(HIDDEN_CLASS);
            console.log('[MarqueeCarousel] Showing multifield:', multifieldFieldName);
        }
    }

    /**
     * Hide the multifield by adding the hidden class to its container
     */
    function hideMultifield(dialog, multifieldFieldName) {
        var container = getMultifieldContainer(dialog, multifieldFieldName);
        if (container.length) {
            container.addClass(HIDDEN_CLASS);
            console.log('[MarqueeCarousel] Hiding multifield:', multifieldFieldName);
        }
    }

    /**
     * Get the current value of the image source select
     */
    function getImageSourceValue(dialog) {
        var selectField = dialog.find('[name="./imageSource"]').closest('coral-select');
        
        if (selectField.length) {
            var coralSelect = selectField[0];
            if (coralSelect && coralSelect.selectedItem) {
                return coralSelect.selectedItem.value || 'manual';
            }
        }
        
        return 'manual'; // default
    }

    /**
     * Toggle visibility of Manual Images Multifield / DAM Folder fields based on image source selection
     */
    function toggleImageSourceFields(dialog) {
        var imageSource = getImageSourceValue(dialog);
        
        console.log('[MarqueeCarousel] Toggling fields for image source:', imageSource);
        
        if (imageSource === 'damFolder') {
            // === SHOW DAM FOLDER FIELD ===
            showField(dialog, './damFolderPath');
            
            // === HIDE MANUAL IMAGES MULTIFIELD ===
            hideMultifield(dialog, './carouselImages');
        } else {
            // === SHOW MANUAL IMAGES MULTIFIELD (default) ===
            showMultifield(dialog, './carouselImages');
            
            // === HIDE DAM FOLDER FIELD ===
            hideField(dialog, './damFolderPath');
        }
    }

    /**
     * Initialize the dialog when loaded
     */
    function initDialog(dialog) {
        // Verify this is the marquee-carousel dialog
        if (!dialog.find('.cmp-marquee-carousel__editor').length) {
            return;
        }
        
        console.log('[MarqueeCarousel] ========================================');
        console.log('[MarqueeCarousel] Initializing dialog...');
        
        // Find the image source select element
        var imageSourceSelect = dialog.find('[name="./imageSource"]').closest('coral-select');
        
        if (!imageSourceSelect.length) {
            console.warn('[MarqueeCarousel] Image source select not found!');
            // List all fields found for debugging
            console.log('[MarqueeCarousel] Available fields:');
            dialog.find('[name^="./"]').each(function() {
                console.log('[MarqueeCarousel]   -', $(this).attr('name'));
            });
            return;
        }
        
        console.log('[MarqueeCarousel] Image source select found');
        
        // Get the native Coral Select component
        var coralSelect = imageSourceSelect[0];
        
        // Wait for Coral to be ready
        Coral.commons.ready(coralSelect, function() {
            console.log('[MarqueeCarousel] Coral select ready');
            
            // Apply initial toggle
            toggleImageSourceFields(dialog);
            
            // Listen for changes on the select
            coralSelect.on('change', function() {
                console.log('[MarqueeCarousel] Image source changed');
                toggleImageSourceFields(dialog);
            });
        });
    }

    // ========================================
    // Event Listeners
    // ========================================
    
    // Handle dialog-loaded event
    $(document).on('dialog-loaded', function(event) {
        var dialog = event.dialog;
        if (dialog && dialog.length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 150);
        }
    });
    
    // Handle foundation-contentloaded as backup
    $(document).on('foundation-contentloaded', function(event) {
        var container = $(event.target);
        var dialog = container.closest('.cq-dialog');
        if (dialog.length && dialog.find('.cmp-marquee-carousel__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);

/**
 * Lead Media Section Dialog Editor JavaScript
 * Handles conditional visibility of fields based on media type selection (Image/Video)
 * 
 * When "Image" is selected: Shows Image Path and Image Alt Text fields
 * When "Video" is selected: Shows Video Path and Video Start Time fields
 */
(function(document, $, Coral) {
    'use strict';

    // CSS class for hiding elements
    var HIDDEN_CLASS = 'cmp-lead-media-section--hidden';

    // Add CSS rule for hiding elements
    var style = document.createElement('style');
    style.textContent = '.' + HIDDEN_CLASS + ' { display: none !important; }';
    document.head.appendChild(style);

    /**
     * Find a field's container by the field's name attribute
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} fieldName - The name attribute value (e.g., "./mediaType")
     * @returns {jQuery} The field container element
     */
    function getFieldContainer(dialog, fieldName) {
        var field = dialog.find('[name="' + fieldName + '"]');
        
        if (!field.length) {
            console.warn('[LeadMediaSection] Field not found:', fieldName);
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
     * Show a field by removing the hidden class
     */
    function showField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.removeClass(HIDDEN_CLASS);
            console.log('[LeadMediaSection] Showing:', fieldName);
        }
    }

    /**
     * Hide a field by adding the hidden class
     */
    function hideField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.addClass(HIDDEN_CLASS);
            console.log('[LeadMediaSection] Hiding:', fieldName);
        }
    }

    /**
     * Get the current value of the media type select
     */
    function getMediaTypeValue(dialog) {
        var selectField = dialog.find('[name="./mediaType"]').closest('coral-select');
        
        if (selectField.length) {
            var coralSelect = selectField[0];
            if (coralSelect && coralSelect.selectedItem) {
                return coralSelect.selectedItem.value || 'image';
            }
        }
        
        return 'image'; // default
    }

    /**
     * Toggle visibility of Image/Video fields based on media type selection
     */
    function toggleMediaTypeFields(dialog) {
        var mediaType = getMediaTypeValue(dialog);
        
        console.log('[LeadMediaSection] Toggling fields for media type:', mediaType);
        
        if (mediaType === 'video') {
            // === SHOW VIDEO FIELDS ===
            showField(dialog, './videoPath');
            showField(dialog, './videoStartTime');
            
            // === HIDE IMAGE FIELDS ===
            hideField(dialog, './imagePath');
            hideField(dialog, './imageAlt');
        } else {
            // === SHOW IMAGE FIELDS (default) ===
            showField(dialog, './imagePath');
            showField(dialog, './imageAlt');
            
            // === HIDE VIDEO FIELDS ===
            hideField(dialog, './videoPath');
            hideField(dialog, './videoStartTime');
        }
    }

    /**
     * Initialize the dialog when loaded
     */
    function initDialog(dialog) {
        // Verify this is the lead-media-section dialog
        if (!dialog.find('.cmp-lead-media-section__editor').length) {
            return;
        }
        
        console.log('[LeadMediaSection] ========================================');
        console.log('[LeadMediaSection] Initializing dialog...');
        
        // Find the media type select element
        var mediaTypeSelect = dialog.find('[name="./mediaType"]').closest('coral-select');
        
        if (!mediaTypeSelect.length) {
            console.warn('[LeadMediaSection] Media type select not found!');
            // List all fields found for debugging
            console.log('[LeadMediaSection] Available fields:');
            dialog.find('[name^="./"]').each(function() {
                console.log('[LeadMediaSection]   -', $(this).attr('name'));
            });
            return;
        }
        
        console.log('[LeadMediaSection] Media type select found');
        
        // Get the native Coral Select component
        var coralSelect = mediaTypeSelect[0];
        
        // Wait for Coral to be ready
        Coral.commons.ready(coralSelect, function() {
            console.log('[LeadMediaSection] Coral select ready');
            
            // Apply initial toggle
            toggleMediaTypeFields(dialog);
            
            // Listen for changes on the select
            coralSelect.on('change', function() {
                console.log('[LeadMediaSection] Media type changed');
                toggleMediaTypeFields(dialog);
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
        if (dialog.length && dialog.find('.cmp-lead-media-section__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);

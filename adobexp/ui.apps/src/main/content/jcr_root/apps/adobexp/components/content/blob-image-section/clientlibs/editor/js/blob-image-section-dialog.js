/**
 * Blob Image Section Dialog Editor JavaScript
 * Handles conditional visibility of fields based on media type selection (Image/Video)
 * 
 * When "Image" is selected: Shows Image Settings (image path, alt text)
 * When "Video" is selected: Shows Video Settings (video path, alt text, start time)
 */
(function(document, $, Coral) {
    'use strict';

    // CSS class for hiding elements
    var HIDDEN_CLASS = 'cmp-blob-image-section--hidden';

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
            console.warn('[BlobImageSection] Field not found:', fieldName);
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
     * Find a heading element by its text content
     */
    function getHeading(dialog, headingText) {
        return dialog.find('coral-heading').filter(function() {
            return $(this).text().trim() === headingText;
        });
    }

    /**
     * Show a field by removing the hidden class
     */
    function showField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.removeClass(HIDDEN_CLASS);
            console.log('[BlobImageSection] Showing:', fieldName);
        }
    }

    /**
     * Hide a field by adding the hidden class
     */
    function hideField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        if (container.length) {
            container.addClass(HIDDEN_CLASS);
            console.log('[BlobImageSection] Hiding:', fieldName);
        }
    }

    /**
     * Show a heading by text
     */
    function showHeading(dialog, headingText) {
        var heading = getHeading(dialog, headingText);
        if (heading.length) {
            heading.removeClass(HIDDEN_CLASS);
            console.log('[BlobImageSection] Showing heading:', headingText);
        }
    }

    /**
     * Hide a heading by text
     */
    function hideHeading(dialog, headingText) {
        var heading = getHeading(dialog, headingText);
        if (heading.length) {
            heading.addClass(HIDDEN_CLASS);
            console.log('[BlobImageSection] Hiding heading:', headingText);
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
        
        console.log('[BlobImageSection] Toggling fields for media type:', mediaType);
        
        if (mediaType === 'video') {
            // === SHOW VIDEO FIELDS ===
            showHeading(dialog, 'Video Settings');
            showField(dialog, './primaryVideoPath');
            showField(dialog, './primaryVideoAlt');
            showField(dialog, './videoStartTime');
            
            // === HIDE IMAGE FIELDS ===
            hideHeading(dialog, 'Image Settings');
            hideField(dialog, './primaryAssetPath');
            hideField(dialog, './primaryAssetAlt');
        } else {
            // === SHOW IMAGE FIELDS (default) ===
            showHeading(dialog, 'Image Settings');
            showField(dialog, './primaryAssetPath');
            showField(dialog, './primaryAssetAlt');
            
            // === HIDE VIDEO FIELDS ===
            hideHeading(dialog, 'Video Settings');
            hideField(dialog, './primaryVideoPath');
            hideField(dialog, './primaryVideoAlt');
            hideField(dialog, './videoStartTime');
        }
    }

    /**
     * Initialize the dialog when loaded
     */
    function initDialog(dialog) {
        // Verify this is the blob-image-section dialog
        if (!dialog.find('.cmp-blob-image-section__editor').length) {
            return;
        }
        
        console.log('[BlobImageSection] ========================================');
        console.log('[BlobImageSection] Initializing dialog...');
        
        // Find the media type select element
        var mediaTypeSelect = dialog.find('[name="./mediaType"]').closest('coral-select');
        
        if (!mediaTypeSelect.length) {
            console.warn('[BlobImageSection] Media type select not found!');
            // List all fields found for debugging
            console.log('[BlobImageSection] Available fields:');
            dialog.find('[name^="./"]').each(function() {
                console.log('[BlobImageSection]   -', $(this).attr('name'));
            });
            return;
        }
        
        console.log('[BlobImageSection] Media type select found');
        
        // Get the native Coral Select component
        var coralSelect = mediaTypeSelect[0];
        
        // Wait for Coral to be ready
        Coral.commons.ready(coralSelect, function() {
            console.log('[BlobImageSection] Coral select ready');
            
            // Apply initial toggle
            toggleMediaTypeFields(dialog);
            
            // Listen for changes on the select
            coralSelect.on('change', function() {
                console.log('[BlobImageSection] Media type changed');
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
        if (dialog.length && dialog.find('.cmp-blob-image-section__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);

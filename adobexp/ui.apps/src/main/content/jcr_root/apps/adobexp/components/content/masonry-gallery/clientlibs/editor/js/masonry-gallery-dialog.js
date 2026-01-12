/**
 * Masonry Gallery Dialog Editor JavaScript
 * Handles conditional visibility of fields based on media source type selection
 * 
 * When "Add Media Manually" is selected: Shows manual media multifield
 * When "Select DAM Folder" is selected: Shows DAM folder path and default video start time
 */
(function(document, $, Coral) {
    'use strict';

    // CSS class for hiding elements
    var HIDDEN_CLASS = 'cmp-masonry-gallery--hidden';

    // Add CSS rule for hiding elements
    var style = document.createElement('style');
    style.textContent = '.' + HIDDEN_CLASS + ' { display: none !important; }';
    document.head.appendChild(style);

    /**
     * Find a field's container by the field's name attribute
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} fieldName - The name attribute value (e.g., "./damFolderPath")
     * @returns {jQuery} The field container element
     */
    function getFieldContainer(dialog, fieldName) {
        var field = dialog.find('[name="' + fieldName + '"]');
        
        if (!field.length) {
            console.warn('[MasonryGallery] Field not found:', fieldName);
            return $();
        }

        // Check if this field is inside a multifield - if so, we need the multifield's wrapper
        var multifield = field.closest('coral-multifield');
        if (multifield.length) {
            // Find the form field wrapper that contains the multifield
            var multifieldWrapper = multifield.closest('.coral-Form-fieldwrapper, .coral3-Form-fieldwrapper');
            if (multifieldWrapper.length) {
                return multifieldWrapper;
            }
            // Fallback: return the multifield's parent
            return multifield.parent();
        }

        // For regular fields, find the wrapper
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
            wrapper = coral.closest('.coral-Form-fieldwrapper, .coral3-Form-fieldwrapper');
            if (wrapper.length) {
                return wrapper;
            }
            return coral.parent();
        }

        return field.parent();
    }

    /**
     * Find a multifield by its fieldLabel
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} fieldLabel - The field label text
     * @returns {jQuery} The multifield wrapper element
     */
    function getMultifieldByLabel(dialog, fieldLabel) {
        // Find all multifields and check their labels
        var multifields = dialog.find('coral-multifield');
        var result = $();
        
        multifields.each(function() {
            var multifield = $(this);
            var wrapper = multifield.closest('.coral-Form-fieldwrapper, .coral3-Form-fieldwrapper');
            
            if (wrapper.length) {
                // Check if the wrapper has a label matching our target
                var label = wrapper.find('label').first();
                if (label.length && label.text().trim() === fieldLabel) {
                    result = wrapper;
                    return false; // break the loop
                }
            }
        });
        
        return result;
    }

    /**
     * Find a heading element by its text content
     * @param {jQuery} dialog - The dialog jQuery object
     * @param {string} headingText - The heading text to find
     * @returns {jQuery} The heading element
     */
    function getHeading(dialog, headingText) {
        return dialog.find('coral-heading').filter(function() {
            return $(this).text().trim() === headingText;
        });
    }

    /**
     * Show an element by removing the hidden class
     * @param {jQuery} element - The jQuery element to show
     * @param {string} description - Description for logging
     */
    function showElement(element, description) {
        if (element && element.length) {
            element.removeClass(HIDDEN_CLASS);
            console.log('[MasonryGallery] Showing:', description);
        } else {
            console.warn('[MasonryGallery] Element not found to show:', description);
        }
    }

    /**
     * Hide an element by adding the hidden class
     * @param {jQuery} element - The jQuery element to hide
     * @param {string} description - Description for logging
     */
    function hideElement(element, description) {
        if (element && element.length) {
            element.addClass(HIDDEN_CLASS);
            console.log('[MasonryGallery] Hiding:', description);
        } else {
            console.warn('[MasonryGallery] Element not found to hide:', description);
        }
    }

    /**
     * Show a field by removing the hidden class
     */
    function showField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        showElement(container, fieldName);
    }

    /**
     * Hide a field by adding the hidden class
     */
    function hideField(dialog, fieldName) {
        var container = getFieldContainer(dialog, fieldName);
        hideElement(container, fieldName);
    }

    /**
     * Show a heading by text
     */
    function showHeading(dialog, headingText) {
        var heading = getHeading(dialog, headingText);
        showElement(heading, 'heading: ' + headingText);
    }

    /**
     * Hide a heading by text
     */
    function hideHeading(dialog, headingText) {
        var heading = getHeading(dialog, headingText);
        hideElement(heading, 'heading: ' + headingText);
    }

    /**
     * Show a multifield by its label
     */
    function showMultifield(dialog, fieldLabel) {
        var multifield = getMultifieldByLabel(dialog, fieldLabel);
        showElement(multifield, 'multifield: ' + fieldLabel);
    }

    /**
     * Hide a multifield by its label
     */
    function hideMultifield(dialog, fieldLabel) {
        var multifield = getMultifieldByLabel(dialog, fieldLabel);
        hideElement(multifield, 'multifield: ' + fieldLabel);
    }

    /**
     * Get the current value of the media source type select
     */
    function getMediaSourceTypeValue(dialog) {
        var selectField = dialog.find('[name="./mediaSourceType"]').closest('coral-select');
        
        if (selectField.length) {
            var coralSelect = selectField[0];
            if (coralSelect && coralSelect.selectedItem) {
                return coralSelect.selectedItem.value || 'manual';
            }
        }
        
        return 'manual'; // default
    }

    /**
     * Toggle visibility of Manual/DAM Folder fields based on media source type selection
     */
    function toggleMediaSourceFields(dialog) {
        var mediaSourceType = getMediaSourceTypeValue(dialog);
        
        console.log('[MasonryGallery] ----------------------------------------');
        console.log('[MasonryGallery] Toggling fields for media source type:', mediaSourceType);
        
        if (mediaSourceType === 'damFolder') {
            // === SHOW DAM FOLDER FIELDS ===
            showHeading(dialog, 'DAM Folder Selection');
            showField(dialog, './damFolderPath');
            showField(dialog, './defaultVideoStartTime');
            showField(dialog, './assetCount');
            showField(dialog, './sortBy');
            showField(dialog, './sortOrder');
            
            // === HIDE MANUAL MEDIA FIELDS ===
            hideHeading(dialog, 'Manual Media Items');
            hideMultifield(dialog, 'Media Items');
        } else {
            // === SHOW MANUAL MEDIA FIELDS (default) ===
            showHeading(dialog, 'Manual Media Items');
            showMultifield(dialog, 'Media Items');
            
            // === HIDE DAM FOLDER FIELDS ===
            hideHeading(dialog, 'DAM Folder Selection');
            hideField(dialog, './damFolderPath');
            hideField(dialog, './defaultVideoStartTime');
            hideField(dialog, './assetCount');
            hideField(dialog, './sortBy');
            hideField(dialog, './sortOrder');
        }
    }

    /**
     * Initialize the dialog when loaded
     */
    function initDialog(dialog) {
        // Verify this is the masonry-gallery dialog
        if (!dialog.find('.cmp-masonry-gallery__editor').length) {
            return;
        }
        
        console.log('[MasonryGallery] ========================================');
        console.log('[MasonryGallery] Initializing dialog...');
        
        // Debug: List all fields and multifields
        console.log('[MasonryGallery] Available fields:');
        dialog.find('[name^="./"]').each(function() {
            console.log('[MasonryGallery]   - name:', $(this).attr('name'));
        });
        
        console.log('[MasonryGallery] Available multifields:');
        dialog.find('coral-multifield').each(function() {
            var wrapper = $(this).closest('.coral-Form-fieldwrapper, .coral3-Form-fieldwrapper');
            var label = wrapper.find('label').first().text().trim();
            console.log('[MasonryGallery]   - multifield label:', label);
        });
        
        console.log('[MasonryGallery] Available headings:');
        dialog.find('coral-heading').each(function() {
            console.log('[MasonryGallery]   - heading:', $(this).text().trim());
        });
        
        // Find the media source type select element
        var mediaSourceTypeSelect = dialog.find('[name="./mediaSourceType"]').closest('coral-select');
        
        if (!mediaSourceTypeSelect.length) {
            console.warn('[MasonryGallery] Media source type select not found!');
            return;
        }
        
        console.log('[MasonryGallery] Media source type select found');
        
        // Get the native Coral Select component
        var coralSelect = mediaSourceTypeSelect[0];
        
        // Wait for Coral to be ready
        Coral.commons.ready(coralSelect, function() {
            console.log('[MasonryGallery] Coral select ready');
            
            // Apply initial toggle
            toggleMediaSourceFields(dialog);
            
            // Listen for changes on the select
            coralSelect.on('change', function() {
                console.log('[MasonryGallery] Media source type changed');
                toggleMediaSourceFields(dialog);
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
        if (dialog.length && dialog.find('.cmp-masonry-gallery__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);

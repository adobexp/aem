/**
 * Comparison Component JavaScript
 * Handles initialization and any interactive behaviors for the comparison component
 */
(function() {
    'use strict';

    /**
     * Initialize the comparison component
     */
    function initComparison() {
        var components = document.querySelectorAll('[data-component="comparison"]');
        
        if (!components.length) {
            return;
        }

        components.forEach(function(component) {
            // Component is primarily CSS-driven
            // Add any future interactive functionality here
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initComparison);
    } else {
        initComparison();
    }

})();


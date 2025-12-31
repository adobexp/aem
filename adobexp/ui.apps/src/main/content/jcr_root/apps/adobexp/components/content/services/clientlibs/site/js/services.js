/**
 * Services Component JavaScript
 * Handles SVG inlining for theme-based color support
 * SVGs with data-svg-inline attribute are fetched and injected into the DOM,
 * allowing CSS to control their colors via currentColor.
 */
(function() {
    'use strict';

    /**
     * Fetches an SVG file and replaces the img element with inline SVG
     * @param {HTMLImageElement} img - The img element to replace with inline SVG
     */
    function inlineSvg(img) {
        var src = img.getAttribute('src');
        if (!src) return;

        fetch(src)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Failed to fetch SVG');
                }
                return response.text();
            })
            .then(function(svgText) {
                var parser = new DOMParser();
                var svgDoc = parser.parseFromString(svgText, 'image/svg+xml');
                var svg = svgDoc.querySelector('svg');

                if (!svg) return;

                // Transfer classes from img to svg
                var imgClasses = img.getAttribute('class');
                if (imgClasses) {
                    svg.setAttribute('class', imgClasses);
                }

                // Transfer aria-hidden attribute
                var ariaHidden = img.getAttribute('aria-hidden');
                if (ariaHidden) {
                    svg.setAttribute('aria-hidden', ariaHidden);
                }

                // Transfer alt text as aria-label for accessibility
                var altText = img.getAttribute('alt');
                if (altText && ariaHidden !== 'true') {
                    svg.setAttribute('aria-label', altText);
                    svg.setAttribute('role', 'img');
                }

                // Mark as inlined to prevent re-processing
                svg.setAttribute('data-svg-inlined', 'true');

                // Replace img with inline SVG
                if (img.parentNode) {
                    img.parentNode.replaceChild(svg, img);
                }
            })
            .catch(function(error) {
                // Silently fail - img will remain as fallback
                console.warn('Services: Failed to inline SVG from ' + src);
            });
    }

    /**
     * Initialize services component - inlines SVGs for theme support
     */
    function initServices() {
        var servicesComponents = document.querySelectorAll('[data-component="services"]');
        
        if (!servicesComponents.length) {
            return;
        }

        servicesComponents.forEach(function(component) {
            // Find all SVG images with data-svg-inline attribute and inline them
            var svgImages = component.querySelectorAll('img[data-svg-inline]');
            svgImages.forEach(function(img) {
                inlineSvg(img);
            });
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initServices);
    } else {
        initServices();
    }

})();



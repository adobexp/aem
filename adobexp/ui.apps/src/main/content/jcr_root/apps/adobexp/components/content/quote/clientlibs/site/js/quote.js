/**
 * Quote Component JavaScript
 * Handles SVG inline functionality for avatar icons
 */
(function() {
    'use strict';

    /**
     * Fetches an SVG from a URL and replaces the img element with the inline SVG
     * @param {HTMLImageElement} img - The image element to replace
     */
    async function inlineSvg(img) {
        const src = img.getAttribute('src');
        if (!src) return;

        try {
            const response = await fetch(src);
            if (!response.ok) return;

            const svgText = await response.text();
            const parser = new DOMParser();
            const svgDoc = parser.parseFromString(svgText, 'image/svg+xml');
            const svg = svgDoc.querySelector('svg');

            if (!svg) return;

            // Preserve classes from the original img
            const imgClasses = img.getAttribute('class');
            if (imgClasses) {
                svg.setAttribute('class', imgClasses);
            }

            // Preserve aria-hidden attribute
            const ariaHidden = img.getAttribute('aria-hidden');
            if (ariaHidden) {
                svg.setAttribute('aria-hidden', ariaHidden);
            }

            // Add accessibility attributes if needed
            const altText = img.getAttribute('alt');
            if (altText && ariaHidden !== 'true') {
                svg.setAttribute('aria-label', altText);
                svg.setAttribute('role', 'img');
            }

            // Mark as inlined
            svg.setAttribute('data-svg-inlined', 'true');

            // Replace img with SVG
            img.parentNode?.replaceChild(svg, img);
        } catch (error) {
            // Silently fail - img will remain as fallback
        }
    }

    /**
     * Initialize quote component functionality
     */
    function initQuoteComponent() {
        const quoteSection = document.querySelector('[data-component="quote"]');
        if (!quoteSection) return;

        // Find all SVG images that should be inlined
        const svgImages = quoteSection.querySelectorAll('img[data-svg-inline]');
        svgImages.forEach(function(img) {
            inlineSvg(img);
        });
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initQuoteComponent);
    } else {
        initQuoteComponent();
    }

})();


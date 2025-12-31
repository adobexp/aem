/**
 * Footer Component JavaScript
 * Handles the curtain reveal effect for the footer
 */
(function() {
    'use strict';

    var footers = document.querySelectorAll('[data-component="footer"]');
    if (!footers.length) return;

    /**
     * Detect if we're running in an AEM context.
     * The curtain effect is DISABLED in AEM because:
     * - AEM's DOM structure uses container wrappers (top, main-content, bottom)
     * - The footer is nested inside experience fragment containers
     * - This is fundamentally incompatible with the curtain effect which requires
     *   the footer to be a direct child of body
     * 
     * The curtain effect is ONLY ENABLED for:
     * - Non-AEM pages (standard HTML pages in st-static-site framework)
     */
    var isAEMContext = function() {
        // In AEM, this script runs inside AEM - so always return true
        // The curtain effect is designed for the standalone st-static-site framework only
        return true;
    };

    var rootFooter = footers[0];
    var surface = rootFooter.querySelector('.footer__surface') || rootFooter;

    /**
     * Ensure curtain wrapper exists for the reveal effect
     * Wraps all content above the footer in a curtain div
     * 
     * IMPORTANT: We skip this in AEM context - DOM structure is incompatible.
     */
    var ensureCurtainWrapper = function() {
        // Skip curtain wrapper in AEM context - DOM structure is incompatible
        if (isAEMContext()) {
            return;
        }

        if (document.querySelector('.footer-curtain')) return;

        var curtain = document.createElement('div');
        curtain.className = 'footer-curtain';

        // CRITICAL FIX: Always use the immediate parent of rootFooter as the container.
        // Using .closest() could return an ancestor that is NOT the direct parent,
        // causing "Failed to execute 'insertBefore' on 'Node'" error.
        // The rootFooter must be a direct child of container for insertBefore to work.
        var container = rootFooter.parentElement;

        if (!container) {
            // Footer has no parent - cannot create curtain wrapper
            return;
        }

        // Safety check: Verify rootFooter is actually a child of container
        if (rootFooter.parentElement !== container) {
            return;
        }

        var containerChildren = Array.from(container.children);

        containerChildren.forEach(function(child) {
            if (child === rootFooter) return;
            curtain.appendChild(child);
        });

        container.insertBefore(curtain, rootFooter);
    };

    /**
     * Apply footer height as CSS variable for curtain effect
     */
    var applyHeight = function() {
        // Skip height application in AEM context
        if (isAEMContext()) {
            return;
        }

        var height = Math.ceil(surface.getBoundingClientRect().height);
        document.body.style.setProperty('--footer-curtain-height-auto', height + 'px');
        document.body.classList.add('has-curtain-footer');
    };

    // Initialize - only if not in AEM context (curtain disabled in AEM)
    if (!isAEMContext()) {
        ensureCurtainWrapper();
        applyHeight();

        // Use ResizeObserver to track footer height changes
        if ('ResizeObserver' in window) {
            var ro = new ResizeObserver(function() {
                applyHeight();
            });
            ro.observe(surface);
        }

        // Fallback: handle window resize
        window.addEventListener('resize', applyHeight);
    }

})();


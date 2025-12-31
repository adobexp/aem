/**
 * Looping Circle Gallery Component JavaScript
 * Handles random flip animations for the gallery cards
 */
(function() {
    'use strict';

    /**
     * Initialize the looping circle gallery component
     */
    function initLoopingCircleGallery() {
        var roots = document.querySelectorAll('[data-component="looping-circle-gallery"]');
        
        if (!roots.length) {
            return;
        }

        // Check for reduced motion preference
        var prefersReduced = typeof window !== 'undefined' 
            && window.matchMedia 
            && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        
        if (prefersReduced) {
            return;
        }

        // Initialize each gallery instance
        roots.forEach(function(root) {
            initGallery(root);
        });
    }

    /**
     * Initialize a single gallery instance
     * @param {HTMLElement} root - The gallery root element
     */
    function initGallery(root) {
        var items = Array.from(root.querySelectorAll('.looping-circle-gallery__item'));
        
        if (!items.length) {
            return;
        }

        /**
         * Trigger a flip animation on an element
         * @param {HTMLElement} el - The item element to flip
         */
        function flipOnce(el) {
            if (el.classList.contains('is-flipping')) {
                return;
            }
            
            el.classList.add('is-flipping');
            var cleanedUp = false;

            /**
             * Clean up after animation completes
             */
            function cleanup() {
                if (cleanedUp) {
                    return;
                }
                cleanedUp = true;
                el.classList.remove('is-flipping');
            }

            var card = el.querySelector('.looping-circle-gallery__card');
            
            if (card) {
                /**
                 * Handle animation end event
                 * @param {AnimationEvent} evt - The animation event
                 */
                function onEnd(evt) {
                    if (evt.animationName !== 'looping-circle-gallery-flip') {
                        return;
                    }
                    cleanup();
                }
                
                card.addEventListener('animationend', onEnd, { once: true });
            }

            // Fallback timeout to ensure cleanup
            window.setTimeout(cleanup, 3000);
        }

        /**
         * Schedule random flip animations
         */
        function schedule() {
            var delay = 1800 + Math.random() * 2600;
            
            window.setTimeout(function() {
                var idx = Math.floor(Math.random() * items.length);
                flipOnce(items[idx]);
                schedule();
            }, delay);
        }

        // Start the animation schedule
        schedule();
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initLoopingCircleGallery);
    } else {
        initLoopingCircleGallery();
    }

})();


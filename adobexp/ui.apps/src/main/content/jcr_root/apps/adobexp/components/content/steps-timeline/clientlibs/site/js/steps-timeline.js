/**
 * Steps Timeline Component - AEM Client-Side JavaScript
 *
 * Numbered walkthrough steps connected by a vertical rail that fills as the
 * reader scrolls. Each step reveals itself when it enters the viewport.
 */
(function() {
    'use strict';

    var INIT_FLAG = 'stepsTimelineInitialized';

    /**
     * Detect the reduced motion user preference, tolerating browsers without matchMedia
     * @returns {boolean} - True when the reader asked for reduced motion
     */
    function prefersReducedMotion() {
        if (!window.matchMedia) return false;
        var query = window.matchMedia('(prefers-reduced-motion: reduce)');
        return !!(query && query.matches);
    }

    /**
     * Show every step at once and fill the rail completely
     * @param {Array} steps - Step elements of one timeline
     * @param {HTMLElement} rail - The rail fill element, may be null
     */
    function revealAll(steps, rail) {
        steps.forEach(function(step) {
            step.classList.add('is-visible');
        });
        if (rail) {
            rail.style.transform = 'scaleY(1)';
        }
    }

    /**
     * Grows the rail so its filled portion tracks the last revealed step, which
     * keeps the connector visually anchored to the reader's position.
     * @param {Array} steps - Step elements of one timeline
     * @param {HTMLElement} rail - The rail fill element
     */
    function trackRail(steps, rail) {
        var frame = 0;

        function update() {
            frame = 0;
            var anchor = window.innerHeight * 0.62;
            var reached = 0;
            steps.forEach(function(step, index) {
                if (step.getBoundingClientRect().top <= anchor) {
                    reached = index + 1;
                }
            });
            var ratio = steps.length > 0 ? Math.min(reached / steps.length, 1) : 0;
            rail.style.transform = 'scaleY(' + ratio + ')';
        }

        function onScroll() {
            if (frame) return;
            frame = requestAnimationFrame(update);
        }

        window.addEventListener('scroll', onScroll, { passive: true });
        window.addEventListener('resize', onScroll, { passive: true });
        update();
    }

    /**
     * Initialize a single steps timeline section
     * @param {HTMLElement} section - The steps timeline section element
     */
    function initStepsTimelineComponent(section) {
        if (!section) return;

        // Guard against duplicate scroll listeners when the author editor re-inits
        if (section.dataset && section.dataset[INIT_FLAG] === 'true') return;

        var steps = Array.prototype.slice.call(section.querySelectorAll('.steps-timeline__step'));
        var rail = section.querySelector('.steps-timeline__rail-fill');
        if (steps.length === 0) return;

        if (section.dataset) {
            section.dataset[INIT_FLAG] = 'true';
        }

        // Exposes the step index to CSS so reveals can be staggered
        steps.forEach(function(step, index) {
            step.style.setProperty('--i', String(index));
        });

        if (prefersReducedMotion() || !('IntersectionObserver' in window)) {
            revealAll(steps, rail);
            return;
        }

        var observer = new IntersectionObserver(
            function(entries) {
                entries.forEach(function(entry) {
                    if (!entry.isIntersecting) return;
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                });
            },
            {
                threshold: 0.3,
                rootMargin: '0px 0px -70px 0px'
            }
        );

        steps.forEach(function(step) {
            observer.observe(step);
        });

        if (rail) {
            trackRail(steps, rail);
        }
    }

    /**
     * Initialize all steps timeline components on the page
     */
    function initAllStepsTimelineComponents() {
        var sections = document.querySelectorAll('[data-component="steps-timeline"]');
        if (sections.length === 0) return;

        Array.prototype.forEach.call(sections, function(section) {
            initStepsTimelineComponent(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllStepsTimelineComponents);
    } else {
        initAllStepsTimelineComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllStepsTimelineComponents();
        });
    }
})();

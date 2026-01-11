/**
 * Count Up Component - AEM Client-Side JavaScript
 * 
 * Provides animated count-up functionality for statistics counters.
 * Uses IntersectionObserver for triggering animations when elements come into view.
 */
(function() {
    'use strict';

    /**
     * Easing function for smooth animation (ease-out cubic)
     * @param {number} t - Progress value between 0 and 1
     * @returns {number} - Eased progress value
     */
    function easeOutCubic(t) {
        return 1 - Math.pow(1 - t, 3);
    }

    /**
     * Format number with specified decimal places
     * @param {number} value - Number to format
     * @param {number} decimals - Number of decimal places
     * @returns {string} - Formatted number string
     */
    function formatNumber(value, decimals) {
        return value.toFixed(decimals);
    }

    /**
     * Animate a counter element from start to end value
     * @param {HTMLElement} element - DOM element to animate
     * @param {Object} options - Animation options
     */
    function animateCounter(element, options) {
        var start = options.start;
        var end = options.end;
        var unit = options.unit;
        var duration = options.duration;
        var decimals = options.decimals;
        var startTime = performance.now();
        var range = end - start;

        function updateCounter(currentTime) {
            var elapsed = currentTime - startTime;
            var progress = Math.min(elapsed / duration, 1);
            var easedProgress = easeOutCubic(progress);
            var currentValue = start + range * easedProgress;

            element.textContent = formatNumber(currentValue, decimals) + unit;

            if (progress < 1) {
                requestAnimationFrame(updateCounter);
            }
        }

        requestAnimationFrame(updateCounter);
    }

    /**
     * Parse counter options from element data attributes
     * @param {HTMLElement} element - DOM element with data attributes
     * @param {number} globalDuration - Global duration from component settings
     * @returns {Object} - Parsed counter options
     */
    function parseCounterOptions(element, globalDuration) {
        var start = parseFloat(element.dataset.start || '0');
        var end = parseFloat(element.dataset.end || '0');
        var unit = element.dataset.unit || '';
        var duration = globalDuration || parseInt(element.dataset.duration || '2000', 10);
        var decimals = parseInt(element.dataset.decimals || '0', 10);

        return {
            start: start,
            end: end,
            unit: unit,
            duration: duration,
            decimals: decimals
        };
    }

    /**
     * Initialize count-up component
     * @param {HTMLElement} countUpSection - The count-up section element
     */
    function initCountUpComponent(countUpSection) {
        if (!countUpSection) return;

        var globalDuration = parseInt(countUpSection.dataset.duration || '2000', 10);
        var counterElements = countUpSection.querySelectorAll('[data-count-up]');

        if (counterElements.length === 0) return;

        // Track which elements have been animated
        var animatedElements = new WeakSet();

        // Set initial values
        counterElements.forEach(function(element) {
            var options = parseCounterOptions(element, globalDuration);
            element.textContent = formatNumber(options.start, options.decimals) + options.unit;
        });

        // Create intersection observer for triggering animations
        var observer = new IntersectionObserver(
            function(entries) {
                entries.forEach(function(entry) {
                    if (entry.isIntersecting) {
                        var element = entry.target;

                        // Skip if already animated
                        if (animatedElements.has(element)) return;
                        animatedElements.add(element);

                        var options = parseCounterOptions(element, globalDuration);
                        animateCounter(element, options);
                    }
                });
            },
            {
                threshold: 0.2, // Trigger when 20% of the element is visible
                rootMargin: '0px 0px -50px 0px' // Slight offset from bottom
            }
        );

        // Observe all counter elements
        counterElements.forEach(function(element) {
            observer.observe(element);
        });
    }

    /**
     * Initialize all count-up components on the page
     */
    function initAllCountUpComponents() {
        var countUpSections = document.querySelectorAll('[data-component="count-up"]');
        countUpSections.forEach(function(section) {
            initCountUpComponent(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllCountUpComponents);
    } else {
        initAllCountUpComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllCountUpComponents();
        });
    }
})();

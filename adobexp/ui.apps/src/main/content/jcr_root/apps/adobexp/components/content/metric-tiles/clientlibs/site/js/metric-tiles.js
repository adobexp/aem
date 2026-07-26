/**
 * Metric Tiles Component - AEM Client-Side JavaScript
 *
 * KPI tiles with a gradient face, an icon, and a value that counts up when the
 * tile scrolls into view.
 *
 *   data-metric-value    number   - final value
 *   data-metric-prefix   string   - rendered before the number (e.g. "$")
 *   data-metric-suffix   string   - rendered after the number (e.g. "TB", "%")
 *   data-metric-decimals number   - decimal places, default 0
 *   data-metric-compact  "true"   - abbreviate large numbers (1.2M) instead of 1,200,000
 */
(function() {
    'use strict';

    var DURATION = 1600;

    /**
     * Whether the visitor asked for reduced motion. Animations are skipped and
     * final values are written immediately when true.
     * @returns {boolean} - True when reduced motion is preferred
     */
    function prefersReducedMotion() {
        if (!window.matchMedia) {
            return false;
        }
        var query = window.matchMedia('(prefers-reduced-motion: reduce)');
        return !!(query && query.matches);
    }

    /**
     * Abbreviate large numbers, e.g. 4820000 becomes "4.8M"
     * @param {number} value - Number to abbreviate
     * @param {number} decimals - Decimal places used for values below 1000
     * @returns {string} - Abbreviated number string
     */
    function formatCompact(value, decimals) {
        var abs = Math.abs(value);
        if (abs >= 1e9) {
            return (value / 1e9).toFixed(1) + 'B';
        }
        if (abs >= 1e6) {
            return (value / 1e6).toFixed(1) + 'M';
        }
        if (abs >= 1e3) {
            return (value / 1e3).toFixed(abs >= 1e4 ? 0 : 1) + 'K';
        }
        return value.toFixed(decimals);
    }

    /**
     * Format a value either compactly or with thousands separators
     * @param {number} value - Number to format
     * @param {number} decimals - Number of decimal places
     * @param {boolean} compact - Whether to abbreviate large numbers
     * @returns {string} - Formatted number string
     */
    function format(value, decimals, compact) {
        if (compact) {
            return formatCompact(value, decimals);
        }
        return value.toLocaleString('en-US', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        });
    }

    /**
     * Read a data attribute, falling back to a default when it is missing
     * @param {HTMLElement} element - DOM element with data attributes
     * @param {string} key - Camel-cased dataset key
     * @param {string} fallback - Value returned when the attribute is absent
     * @returns {string} - The attribute value or the fallback
     */
    function readData(element, key, fallback) {
        if (!element.dataset) {
            return fallback;
        }
        var value = element.dataset[key];
        if (value === undefined || value === null) {
            return fallback;
        }
        return value;
    }

    /**
     * Count a single value element up to its target, once
     * @param {HTMLElement} node - The element carrying data-metric-value
     */
    function animate(node) {
        if (!node || !node.dataset) {
            return;
        }
        if (node.dataset.counted === 'true') {
            return;
        }
        node.dataset.counted = 'true';

        var target = parseFloat(readData(node, 'metricValue', '0'));
        if (isNaN(target)) {
            target = 0;
        }
        var decimals = parseInt(readData(node, 'metricDecimals', '0'), 10);
        if (isNaN(decimals)) {
            decimals = 0;
        }
        var compact = readData(node, 'metricCompact', '') === 'true';
        var prefix = readData(node, 'metricPrefix', '');
        var suffix = readData(node, 'metricSuffix', '');

        function write(value) {
            node.textContent = prefix + format(value, decimals, compact) + suffix;
        }

        if (prefersReducedMotion() || typeof requestAnimationFrame !== 'function') {
            write(target);
            return;
        }

        var start = performance.now();

        function tick(now) {
            var progress = Math.min((now - start) / DURATION, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            write(target * eased);
            if (progress < 1) {
                requestAnimationFrame(tick);
            }
        }

        requestAnimationFrame(tick);
    }

    /**
     * Reveal a tile and count up every value it contains
     * @param {HTMLElement} tile - The tile element
     */
    function revealTile(tile) {
        tile.classList.add('is-visible');
        var values = tile.querySelectorAll('[data-metric-value]');
        Array.prototype.forEach.call(values, function(value) {
            animate(value);
        });
    }

    /**
     * Initialize a single metric tiles section
     * @param {HTMLElement} section - The metric tiles section element
     */
    function initMetricTilesSection(section) {
        if (!section) {
            return;
        }
        // Guard against observing the same section twice on author re-init
        if (section.getAttribute('data-metric-tiles-ready') === 'true') {
            return;
        }
        section.setAttribute('data-metric-tiles-ready', 'true');

        var tiles = section.querySelectorAll('.metric-tiles__tile');
        if (tiles.length === 0) {
            return;
        }

        if (prefersReducedMotion() || !('IntersectionObserver' in window)) {
            Array.prototype.forEach.call(tiles, function(tile) {
                revealTile(tile);
            });
            return;
        }

        var observer = new IntersectionObserver(
            function(entries) {
                entries.forEach(function(entry) {
                    if (!entry.isIntersecting) {
                        return;
                    }
                    var tile = entry.target;
                    revealTile(tile);
                    observer.unobserve(tile);
                });
            },
            {
                threshold: 0.25, // Trigger when a quarter of the tile is visible
                rootMargin: '0px 0px -50px 0px' // Slight offset from bottom
            }
        );

        // --i staggers the reveal transition of each tile via CSS
        Array.prototype.forEach.call(tiles, function(tile, index) {
            tile.style.setProperty('--i', String(index));
            observer.observe(tile);
        });
    }

    /**
     * Initialize all metric tiles components on the page
     */
    function initAllMetricTilesComponents() {
        var sections = document.querySelectorAll('[data-component="metric-tiles"]');
        Array.prototype.forEach.call(sections, function(section) {
            initMetricTilesSection(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllMetricTilesComponents);
    } else {
        initAllMetricTilesComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllMetricTilesComponents();
        });
    }
})();

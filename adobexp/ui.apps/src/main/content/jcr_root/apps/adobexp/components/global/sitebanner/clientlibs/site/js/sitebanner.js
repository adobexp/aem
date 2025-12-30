/**
 * Site Banner Component JavaScript
 * Handles scrolling marquee banner with cycling messages and HTML sanitization
 */
(function() {
    'use strict';

    /**
     * Initialize all site banners on the page
     */
    var initSiteBanners = function() {
        var banners = document.querySelectorAll('[data-component="site-banner"]');
        if (!banners.length) return;

        /**
         * Sanitize HTML input to allow only specific inline tags
         * @param {string} input - Raw HTML string
         * @returns {string} - Sanitized HTML string
         */
        var sanitizeInlineHtml = function(input) {
            var ALLOWED = ['STRONG', 'B', 'EM', 'I', 'SPAN'];
            var ALLOWED_SPAN_CLASSES = [
                'site-badge',
                'success',
                'info',
                'warning',
                'error',
                'primary',
                'secondary',
                'purple',
                'pink',
                'teal',
                'neutral'
            ];

            var tpl = document.createElement('template');
            tpl.innerHTML = input;
            var out = document.createElement('div');

            /**
             * Walk through DOM nodes and sanitize
             * @param {Node} node - Current node
             * @param {Element} parent - Parent element to append to
             */
            var walk = function(node, parent) {
                if (node.nodeType === Node.TEXT_NODE) {
                    parent.appendChild(document.createTextNode(node.textContent || ''));
                    return;
                }
                if (node.nodeType !== Node.ELEMENT_NODE) return;

                var el = node;
                if (ALLOWED.indexOf(el.tagName) !== -1) {
                    var clean = document.createElement(el.tagName.toLowerCase());
                    if (el.tagName === 'SPAN') {
                        var classAttr = el.getAttribute('class') || '';
                        var classes = classAttr.split(/\s+/)
                            .map(function(c) { return c.trim(); })
                            .filter(function(c) { return c && ALLOWED_SPAN_CLASSES.indexOf(c) !== -1; });
                        if (classes.length) {
                            clean.setAttribute('class', classes.join(' '));
                        }
                    }
                    Array.prototype.forEach.call(el.childNodes, function(child) {
                        walk(child, clean);
                    });
                    parent.appendChild(clean);
                } else {
                    parent.appendChild(document.createTextNode(el.textContent || ''));
                }
            };

            Array.prototype.forEach.call(tpl.content.childNodes, function(n) {
                walk(n, out);
            });
            return out.innerHTML;
        };

        /**
         * Parse messages from banner data attribute
         * @param {Element} banner - Banner element
         * @returns {Array<string>} - Array of message strings
         */
        var parseMessages = function(banner) {
            var raw = (banner.getAttribute('data-messages') || '').trim();
            if (raw) {
                try {
                    var parsed = JSON.parse(raw);
                    if (Array.isArray(parsed)) {
                        return parsed
                            .map(function(v) { return String(v).trim(); })
                            .filter(function(v) { return v; });
                    }
                } catch (e) {
                    // JSON parse failed, return empty array
                }
            }
            return [];
        };

        /**
         * Initialize each banner
         */
        banners.forEach(function(banner) {
            var messages = parseMessages(banner);
            if (!messages.length) return;

            var textEl = banner.querySelector('[data-site-banner-text="1"]');
            if (!textEl) return;

            // Set cycle duration CSS variable
            var cycleDuration = (banner.getAttribute('data-cycle-duration') || '').trim();
            if (cycleDuration) {
                banner.style.setProperty('--site-banner-duration', cycleDuration);
            }

            var idx = 0;

            /**
             * Set text content with sanitization
             * @param {number} i - Message index
             */
            var setText = function(i) {
                var raw = messages[i] || '';
                textEl.innerHTML = sanitizeInlineHtml(raw);
            };

            // Set initial text
            setText(idx);

            // Cycle through messages on animation iteration
            textEl.addEventListener('animationiteration', function() {
                idx = (idx + 1) % messages.length;
                setText(idx);
            });
        });
    };

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initSiteBanners);
    } else {
        initSiteBanners();
    }

})();


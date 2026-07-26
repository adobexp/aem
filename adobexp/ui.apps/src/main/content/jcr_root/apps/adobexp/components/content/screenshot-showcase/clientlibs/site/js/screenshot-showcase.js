/**
 * Screenshot Showcase Component - AEM Client-Side JavaScript
 *
 * Presents product screenshots inside a browser chrome frame. Frames drift
 * slightly as the section scrolls through the viewport (parallax) and tilt
 * toward the pointer on devices that support hover.
 *
 *   data-parallax  number  - drift strength in px, 0 disables. Default 26.
 *   data-tilt      "false" - opt out of pointer tilt.
 */
(function() {
    'use strict';

    var DEFAULT_PARALLAX = 26;
    var DEFAULT_DEPTH = 1;

    /**
     * Safe media query test that never throws on older browsers.
     * @param {string} query - Media query to evaluate
     * @returns {boolean} - True when the query matches
     */
    function matchesMedia(query) {
        if (!window.matchMedia) {
            return false;
        }
        var result = window.matchMedia(query);
        return !!(result && result.matches);
    }

    /**
     * Reads a numeric data attribute, falling back when absent or unparseable.
     * @param {string} raw - Raw attribute value
     * @param {number} fallback - Value used when raw is not a usable number
     * @returns {number} - Parsed number
     */
    function toNumber(raw, fallback) {
        if (raw === null || raw === undefined || raw === '') {
            return fallback;
        }
        var parsed = parseFloat(raw);
        if (isNaN(parsed)) {
            return fallback;
        }
        return parsed;
    }

    /**
     * Maps the section's travel through the viewport to -1..1 and offsets each
     * frame by its own depth so stacked frames separate as the reader scrolls.
     * @param {HTMLElement} section - The showcase section
     * @param {Array} frames - Frame elements inside the section
     * @param {number} strength - Drift strength in pixels
     */
    function bindParallax(section, frames, strength) {
        var frameId = 0;

        function update() {
            frameId = 0;
            var box = section.getBoundingClientRect();
            var viewport = window.innerHeight || 1;
            if (box.bottom < -200 || box.top > viewport + 200) {
                return;
            }

            var centre = box.top + box.height / 2;
            var progress = (centre - viewport / 2) / (viewport / 2 + box.height / 2);
            var clamped = Math.max(-1, Math.min(1, progress));

            frames.forEach(function(frame) {
                var depth = toNumber(frame.getAttribute('data-depth'), DEFAULT_DEPTH) || DEFAULT_DEPTH;
                frame.style.setProperty('--parallax-y', (-clamped * strength * depth).toFixed(2) + 'px');
            });
        }

        function onScroll() {
            if (frameId) {
                return;
            }
            frameId = requestAnimationFrame(update);
        }

        window.addEventListener('scroll', onScroll, { passive: true });
        window.addEventListener('resize', onScroll, { passive: true });
        update();
    }

    /**
     * Tilts a single frame toward the pointer and resets on leave.
     * @param {HTMLElement} frame - Frame element to bind
     */
    function bindTilt(frame) {
        function reset() {
            frame.style.setProperty('--tilt-x', '0deg');
            frame.style.setProperty('--tilt-y', '0deg');
        }

        frame.addEventListener('pointermove', function(event) {
            var box = frame.getBoundingClientRect();
            var ratioX = (event.clientX - box.left) / box.width - 0.5;
            var ratioY = (event.clientY - box.top) / box.height - 0.5;
            frame.style.setProperty('--tilt-y', (ratioX * 7).toFixed(2) + 'deg');
            frame.style.setProperty('--tilt-x', (-ratioY * 5).toFixed(2) + 'deg');
        });

        frame.addEventListener('pointerleave', reset);
        reset();
    }

    /**
     * Reveals frames as they scroll into view, falling back to showing them all.
     * @param {Array} frames - Frame elements to observe
     */
    function bindReveal(frames) {
        if (!('IntersectionObserver' in window)) {
            frames.forEach(function(frame) {
                frame.classList.add('is-visible');
            });
            return;
        }

        var observer = new IntersectionObserver(
            function(entries) {
                entries.forEach(function(entry) {
                    if (!entry.isIntersecting) {
                        return;
                    }
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                });
            },
            {
                threshold: 0.2,
                rootMargin: '0px 0px -60px 0px'
            }
        );

        frames.forEach(function(frame) {
            observer.observe(frame);
        });
    }

    /**
     * Initialize a single screenshot showcase section
     * @param {HTMLElement} section - The screenshot showcase section element
     */
    function initScreenshotShowcaseComponent(section) {
        if (!section || section.dataset.screenshotShowcaseReady === 'true') {
            return;
        }

        var frames = Array.prototype.slice.call(section.querySelectorAll('.screenshot-showcase__frame'));
        if (frames.length === 0) {
            return;
        }

        section.dataset.screenshotShowcaseReady = 'true';

        // Staggers the reveal transition of each frame via the CSS --i index
        frames.forEach(function(frame, index) {
            frame.style.setProperty('--i', String(index));
        });

        if (matchesMedia('(prefers-reduced-motion: reduce)')) {
            frames.forEach(function(frame) {
                frame.classList.add('is-visible');
            });
            return;
        }

        bindReveal(frames);

        var strength = toNumber(section.getAttribute('data-parallax'), DEFAULT_PARALLAX);
        if (strength > 0) {
            bindParallax(section, frames, strength);
        }

        if (matchesMedia('(hover: hover) and (pointer: fine)') && section.getAttribute('data-tilt') !== 'false') {
            frames.forEach(bindTilt);
        }
    }

    /**
     * Initialize all screenshot showcase components on the page
     */
    function initAllScreenshotShowcaseComponents() {
        var sections = document.querySelectorAll('[data-component="screenshot-showcase"]');
        if (sections.length === 0) {
            return;
        }
        Array.prototype.forEach.call(sections, function(section) {
            initScreenshotShowcaseComponent(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllScreenshotShowcaseComponents);
    } else {
        initAllScreenshotShowcaseComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllScreenshotShowcaseComponents();
        });
    }
})();

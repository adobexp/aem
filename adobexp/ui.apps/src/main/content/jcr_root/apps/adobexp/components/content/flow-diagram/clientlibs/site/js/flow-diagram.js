/**
 * Flow Diagram Component - AEM Client-Side JavaScript
 *
 * Draws an animated architecture diagram. Stages are authored as plain HTML so the
 * content stays accessible and responsive; the connectors between them are measured
 * from the live DOM and drawn into an SVG overlay, then animated with a flowing dash
 * to suggest data movement.
 *
 * Markup contract:
 *   .flow-diagram__canvas
 *     .flow-diagram__stage        (one per pipeline stage, left -> right)
 *       .flow-diagram__node       (one or more boxes inside the stage)
 */
(function() {
    'use strict';

    var SVG_NS = 'http://www.w3.org/2000/svg';
    var INITIALISED_FLAG = 'flowDiagramInitialised';

    /**
     * Convert a NodeList into a real array
     * @param {NodeList} nodeList - Collection to convert
     * @returns {Array} - Array of elements
     */
    function toArray(nodeList) {
        return Array.prototype.slice.call(nodeList);
    }

    /**
     * Evaluate a media query without throwing on unsupported browsers
     * @param {string} query - Media query string
     * @returns {boolean} - Whether the query currently matches
     */
    function matchesMedia(query) {
        if (!window.matchMedia) {
            return false;
        }
        var mediaQuery = window.matchMedia(query);
        return !!(mediaQuery && mediaQuery.matches);
    }

    /**
     * Whether the visitor asked for reduced motion
     * @returns {boolean} - True when animations should be suppressed
     */
    function prefersReducedMotion() {
        return matchesMedia('(prefers-reduced-motion: reduce)');
    }

    /**
     * Stages stack vertically on narrow screens, so connectors must follow suit
     * @returns {boolean} - True when the stages are stacked vertically
     */
    function isVerticalLayout() {
        return matchesMedia('(max-width: 899px)');
    }

    /**
     * Create an SVG element with the given attributes
     * @param {string} name - SVG tag name
     * @param {Object} attrs - Attribute name/value pairs
     * @returns {SVGElement} - The created element
     */
    function svgEl(name, attrs) {
        var element = document.createElementNS(SVG_NS, name);
        var keys = Object.keys(attrs || {});
        keys.forEach(function(key) {
            element.setAttribute(key, String(attrs[key]));
        });
        return element;
    }

    /**
     * Cubic bezier that leaves the source horizontally and enters the target horizontally
     * @param {Object} from - Source point with x and y
     * @param {Object} to - Target point with x and y
     * @param {boolean} vertical - Whether the layout is stacked vertically
     * @returns {string} - SVG path data
     */
    function connectorPath(from, to, vertical) {
        if (vertical) {
            var midY = (from.y + to.y) / 2;
            return 'M' + from.x + ',' + from.y + ' C' + from.x + ',' + midY + ' ' + to.x + ',' + midY + ' ' + to.x + ',' + to.y;
        }
        var midX = (from.x + to.x) / 2;
        return 'M' + from.x + ',' + from.y + ' C' + midX + ',' + from.y + ' ' + midX + ',' + to.y + ' ' + to.x + ',' + to.y;
    }

    /**
     * Measure the stages and draw the connector overlay into the canvas
     * @param {HTMLElement} section - The flow diagram section element
     */
    function draw(section) {
        if (!section) return;

        var canvas = section.querySelector('.flow-diagram__canvas');
        if (!canvas) return;

        var stages = toArray(canvas.querySelectorAll('.flow-diagram__stage'));
        if (stages.length < 2) return;

        var previous = canvas.querySelector('.flow-diagram__links');
        if (previous && previous.parentNode) {
            previous.parentNode.removeChild(previous);
        }

        var bounds = canvas.getBoundingClientRect();
        var svg = svgEl('svg', {
            'class': 'flow-diagram__links',
            'viewBox': '0 0 ' + Math.round(bounds.width) + ' ' + Math.round(bounds.height),
            'width': Math.round(bounds.width),
            'height': Math.round(bounds.height),
            'aria-hidden': 'true'
        });

        var vertical = isVerticalLayout();
        var reducedMotion = prefersReducedMotion();
        var pathIndex = 0;

        for (var i = 0; i < stages.length - 1; i += 1) {
            var sources = toArray(stages[i].querySelectorAll('.flow-diagram__node'));
            var targets = toArray(stages[i + 1].querySelectorAll('.flow-diagram__node'));

            for (var s = 0; s < sources.length; s += 1) {
                var sourceBox = sources[s].getBoundingClientRect();

                for (var t = 0; t < targets.length; t += 1) {
                    var targetBox = targets[t].getBoundingClientRect();

                    var from = vertical
                        ? { x: sourceBox.left + sourceBox.width / 2 - bounds.left, y: sourceBox.bottom - bounds.top }
                        : { x: sourceBox.right - bounds.left, y: sourceBox.top + sourceBox.height / 2 - bounds.top };

                    var to = vertical
                        ? { x: targetBox.left + targetBox.width / 2 - bounds.left, y: targetBox.top - bounds.top }
                        : { x: targetBox.left - bounds.left, y: targetBox.top + targetBox.height / 2 - bounds.top };

                    var d = connectorPath(from, to, vertical);

                    svg.appendChild(svgEl('path', { 'd': d, 'class': 'flow-diagram__link' }));

                    var pulse = svgEl('path', { 'd': d, 'class': 'flow-diagram__pulse' });
                    if (!reducedMotion) {
                        pulse.style.animationDelay = ((pathIndex % 6) * 0.28) + 's';
                    }
                    svg.appendChild(pulse);
                    pathIndex += 1;
                }
            }
        }

        if (canvas.firstChild) {
            canvas.insertBefore(svg, canvas.firstChild);
        } else {
            canvas.appendChild(svg);
        }
    }

    /**
     * Initialize a single flow diagram section
     * @param {HTMLElement} section - The flow diagram section element
     */
    function initFlowDiagramComponent(section) {
        if (!section) return;

        var nodes = toArray(section.querySelectorAll('.flow-diagram__node'));
        nodes.forEach(function(node, index) {
            node.style.setProperty('--i', String(index));
        });

        var render = function() {
            draw(section);
        };

        // Author mode replaces markup in place, so a second pass only needs a redraw.
        if (section.dataset[INITIALISED_FLAG] === 'true') {
            render();
            return;
        }
        section.dataset[INITIALISED_FLAG] = 'true';

        // Fonts and images shift box metrics, so redraw once the page settles.
        render();
        window.addEventListener('load', render);

        var resizeFrame = 0;
        window.addEventListener('resize', function() {
            if (resizeFrame) {
                cancelAnimationFrame(resizeFrame);
            }
            resizeFrame = requestAnimationFrame(render);
        }, { passive: true });

        if (typeof ResizeObserver !== 'undefined') {
            var canvas = section.querySelector('.flow-diagram__canvas');
            if (canvas) {
                new ResizeObserver(render).observe(canvas);
            }
        }

        if (prefersReducedMotion() || !('IntersectionObserver' in window)) {
            section.classList.add('is-visible');
            nodes.forEach(function(node) {
                node.classList.add('is-visible');
            });
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
                threshold: 0.2, // Trigger when 20% of the element is visible
                rootMargin: '0px 0px -50px 0px' // Slight offset from bottom
            }
        );

        observer.observe(section);
        nodes.forEach(function(node) {
            observer.observe(node);
        });
    }

    /**
     * Initialize all flow diagram components on the page
     */
    function initAllFlowDiagramComponents() {
        var sections = toArray(document.querySelectorAll('[data-component="flow-diagram"]'));
        sections.forEach(function(section) {
            initFlowDiagramComponent(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllFlowDiagramComponents);
    } else {
        initAllFlowDiagramComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllFlowDiagramComponents();
        });
    }
})();

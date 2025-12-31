/**
 * Lead Banner Component JavaScript
 * Handles text animation cycling and image stack transitions
 */
(function() {
    'use strict';

    const FADE_DURATION = 700;
    const CHARACTER_DURATION = 50;
    const DISPLAY_DURATION = 2000;

    /**
     * Fade out an element by reducing opacity
     * @param {HTMLElement} element - Element to fade out
     * @param {number} duration - Duration in milliseconds
     * @returns {Promise} Resolves when animation completes
     */
    const fadeOut = function(element, duration) {
        return new Promise(function(resolve) {
            element.style.transition = 'opacity ' + duration + 'ms ease-out';
            element.style.opacity = '0';
            setTimeout(function() {
                resolve();
            }, duration);
        });
    };

    /**
     * Animate text character by character
     * @param {HTMLElement} element - Element to animate text in
     * @param {string} targetText - Text to animate
     * @returns {Promise} Resolves when animation completes
     */
    const animateText = function(element, targetText) {
        return new Promise(function(resolve) {
            var targetLength = targetText.length;
            var currentIndex = 0;
            element.innerHTML = '';
            element.style.opacity = '1';
            element.style.transition = 'none';

            var addNextCharacter = function() {
                if (currentIndex < targetLength) {
                    var currentChar = targetText[currentIndex];
                    var charSpan = document.createElement('span');
                    charSpan.className = 'char';
                    if (currentChar === ' ') {
                        charSpan.innerHTML = '&nbsp;';
                    } else {
                        charSpan.textContent = currentChar;
                    }
                    element.appendChild(charSpan);
                    currentIndex++;
                    setTimeout(function() {
                        if (currentIndex < targetLength) {
                            addNextCharacter();
                        } else {
                            setTimeout(function() {
                                resolve();
                            }, 300);
                        }
                    }, CHARACTER_DURATION);
                } else {
                    resolve();
                }
            };
            addNextCharacter();
        });
    };

    /**
     * Initialize text animation for an element
     * @param {HTMLElement} element - Element with data-strings attribute
     * @param {Function} onCycle - Callback when text cycles
     */
    var initializeTextAnimation = function(element, onCycle) {
        var stringsAttr = element.getAttribute('data-strings');
        var strings = [];

        if (stringsAttr) {
            try {
                strings = JSON.parse(stringsAttr);
                if (!Array.isArray(strings) || strings.length === 0) {
                    console.warn('LeadBanner: data-strings must be a non-empty array');
                    return;
                }
            } catch (error) {
                console.warn('LeadBanner: Invalid data-strings JSON:', error);
                return;
            }
        } else {
            var currentText = element.textContent || '';
            if (currentText) {
                strings = [currentText];
            } else {
                return;
            }
        }

        // Remove duplicates and empty strings
        var uniqueStrings = [];
        strings.forEach(function(s) {
            if (s && s.trim().length > 0 && uniqueStrings.indexOf(s) === -1) {
                uniqueStrings.push(s);
            }
        });
        strings = uniqueStrings;

        if (strings.length === 0) {
            return;
        }

        if (strings.length === 1) {
            element.textContent = strings[0];
            element.style.opacity = '1';
            return;
        }

        element.style.opacity = '1';
        element.style.transition = '';

        animateText(element, strings[0]).then(function() {
            var currentStringIndex = 0;

            var cycleToNext = function() {
                setTimeout(function() {
                    fadeOut(element, FADE_DURATION).then(function() {
                        currentStringIndex = (currentStringIndex + 1) % strings.length;
                        var nextString = strings[currentStringIndex];
                        if (onCycle) {
                            onCycle(currentStringIndex);
                        }
                        animateText(element, nextString).then(function() {
                            cycleToNext();
                        });
                    });
                }, DISPLAY_DURATION);
            };
            cycleToNext();
        });
    };

    /**
     * Initialize a single lead banner component
     * @param {HTMLElement} leadBanner - The lead banner element
     */
    var initLeadBanner = function(leadBanner) {
        var secondaryHeadline = leadBanner.querySelector('.lead-banner__headline-secondary');
        var stack = leadBanner.querySelector('[data-lead-banner-image-stack]');
        var prefersReducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        var isStackAnimating = false;

        /**
         * Apply stack layout positioning to images
         */
        var applyStackLayout = function() {
            if (!stack) return;
            var items = Array.prototype.slice.call(stack.querySelectorAll('.lead-banner__stack-item'));
            items.forEach(function(item, i) {
                item.style.setProperty('--stack-x', (-i * 10) + 'px');
                item.style.setProperty('--stack-y', (-i * 8) + 'px');
                item.style.setProperty('--stack-scale', String(1 - i * 0.04));
                item.style.setProperty('--stack-opacity', String(Math.max(0.62, 1 - i * 0.12)));
                item.style.setProperty('--stack-z', String(100 - i));
                item.style.setProperty('--stack-shadow-alpha', String(Math.max(0.08, 0.22 - i * 0.03)));
            });
        };

        /**
         * Cycle the image stack once
         */
        var cycleStackOnce = function() {
            if (!stack) return;
            var items = Array.prototype.slice.call(stack.querySelectorAll('.lead-banner__stack-item'));
            if (items.length < 2) return;
            if (isStackAnimating) return;

            var top = items[0];

            if (prefersReducedMotion) {
                stack.appendChild(top);
                applyStackLayout();
                return;
            }

            isStackAnimating = true;
            top.classList.add('lead-banner__stack-item--moving');

            var onEnd = function() {
                top.removeEventListener('animationend', onEnd);
                top.classList.remove('lead-banner__stack-item--moving');
                top.style.opacity = '0';
                stack.appendChild(top);
                requestAnimationFrame(function() {
                    applyStackLayout();
                    requestAnimationFrame(function() {
                        top.style.opacity = '';
                        isStackAnimating = false;
                    });
                });
            };

            top.addEventListener('animationend', onEnd);
        };

        // Initialize
        applyStackLayout();

        if (secondaryHeadline) {
            initializeTextAnimation(secondaryHeadline, function() {
                cycleStackOnce();
            });
        }
    };

    /**
     * Initialize all lead banner components on the page
     */
    var initAllLeadBanners = function() {
        var leadBannerElements = document.querySelectorAll('[data-component="lead-banner"]');
        if (leadBannerElements.length === 0) return;

        leadBannerElements.forEach(function(leadBanner) {
            initLeadBanner(leadBanner);
        });
    };

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllLeadBanners);
    } else {
        initAllLeadBanners();
    }
})();


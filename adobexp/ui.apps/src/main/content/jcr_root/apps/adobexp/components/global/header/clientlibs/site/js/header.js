/**
 * Header Component JavaScript
 * Handles theme toggling, menu overlay, accordion navigation, and responsive behavior
 */
(function() {
    'use strict';

    const DEFAULT_BACKGROUND = '#0f172a';
    const THEME_STORAGE_KEY = 'header-theme';
    const THEME_DARK = 'dark';
    const THEME_LIGHT = 'light';

    const header = document.querySelector('header[data-component="header"]');
    if (!header) return;

    // Theme root detection
    const explicitThemeRoot = header.closest('.theme-light') || header.closest('.theme-dark');
    const themeRoot = explicitThemeRoot ?? document.documentElement;
    const usesExplicitThemeRoot = !!explicitThemeRoot;

    /**
     * Get stored theme from localStorage
     */
    const getStoredTheme = function() {
        try {
            return localStorage.getItem(THEME_STORAGE_KEY) || THEME_DARK;
        } catch (e) {
            return THEME_DARK;
        }
    };

    /**
     * Set theme on theme root element
     */
    const setTheme = function(theme) {
        themeRoot.classList.remove('theme-dark', 'theme-light');
        themeRoot.classList.add('theme-' + theme);
        try {
            if (!usesExplicitThemeRoot) {
                localStorage.setItem(THEME_STORAGE_KEY, theme);
            }
        } catch (e) {
            // localStorage not available
        }
    };

    /**
     * Resolve initial theme based on existing classes or stored preference
     */
    const resolveInitialTheme = function() {
        if (themeRoot.classList.contains('theme-' + THEME_LIGHT)) return THEME_LIGHT;
        if (themeRoot.classList.contains('theme-' + THEME_DARK)) return THEME_DARK;
        return getStoredTheme();
    };

    // Set initial theme
    setTheme(resolveInitialTheme());

    // Logo elements
    const logoDark = header.querySelector('.header__logo-img-dark');
    const logoLight = header.querySelector('.header__logo-img-light');

    // Set default alt text if missing
    if (logoDark && !logoDark.alt) {
        logoDark.alt = 'Logo';
    }
    if (logoLight && !logoLight.alt) {
        logoLight.alt = 'Logo';
    }

    // Overlay and button elements
    const overlay = document.querySelector('.header__overlay');
    const menuBtn = document.querySelector('.header__menu-btn');
    const themeToggle = document.querySelector('.header__theme-toggle');

    /**
     * Update overlay position based on header height
     */
    const updateOverlayPosition = function() {
        if (!overlay) return;
        const rect = header.getBoundingClientRect();
        const topPx = Math.max(0, Math.round(rect.bottom));
        const heightPx = Math.max(0, window.innerHeight - topPx);
        overlay.style.setProperty('--header-overlay-top', topPx + 'px');
        overlay.style.setProperty('--header-overlay-height', heightPx + 'px');
    };

    /**
     * Resolve header background color
     */
    const resolveHeaderBackground = function() {
        const computed = getComputedStyle(header);
        return computed.background || computed.backgroundColor || DEFAULT_BACKGROUND;
    };

    /**
     * Update theme toggle button appearance
     */
    const updateThemeToggleButton = function() {
        if (!themeToggle) return;
        const isDark = themeRoot.classList.contains('theme-' + THEME_DARK);
        const isLight = themeRoot.classList.contains('theme-' + THEME_LIGHT);

        themeToggle.classList.remove('btn-theme-dark', 'btn-theme-light');
        if (isDark) {
            themeToggle.classList.add('btn-theme-dark');
        } else if (isLight) {
            themeToggle.classList.add('btn-theme-light');
        } else {
            themeToggle.classList.add('btn-theme-dark');
        }

        const sunIcon = themeToggle.querySelector('.header__theme-icon-sun');
        const moonIcon = themeToggle.querySelector('.header__theme-icon-moon');

        if (sunIcon && moonIcon) {
            if (isDark) {
                sunIcon.style.display = 'block';
                moonIcon.style.display = 'none';
            } else {
                sunIcon.style.display = 'none';
                moonIcon.style.display = 'block';
            }
        }
    };

    /**
     * Update menu button appearance based on theme
     */
    const updateMenuButton = function() {
        if (!menuBtn) return;
        const isDark = themeRoot.classList.contains('theme-' + THEME_DARK);
        const isLight = themeRoot.classList.contains('theme-' + THEME_LIGHT);

        menuBtn.classList.remove('btn-theme-dark', 'btn-theme-light');
        if (isDark) {
            menuBtn.classList.add('btn-theme-dark');
        } else if (isLight) {
            menuBtn.classList.add('btn-theme-light');
        } else {
            menuBtn.classList.add('btn-theme-dark');
        }
    };

    /**
     * Update social media button appearances
     */
    const updateOverlaySocialButtons = function() {
        var socialLinks = document.querySelectorAll('.header__overlay-social-link');
        if (!socialLinks.length) return;

        var isDark = themeRoot.classList.contains('theme-' + THEME_DARK);
        var isLight = themeRoot.classList.contains('theme-' + THEME_LIGHT);

        socialLinks.forEach(function(el) {
            el.classList.remove('btn-theme-dark', 'btn-theme-light');
            if (isDark) {
                el.classList.add('btn-theme-dark');
            } else if (isLight) {
                el.classList.add('btn-theme-light');
            } else {
                el.classList.add('btn-theme-dark');
            }
        });
    };

    /**
     * Update logo visibility based on theme
     */
    const updateLogo = function() {
        var isDark = themeRoot.classList.contains('theme-' + THEME_DARK);
        var isLight = themeRoot.classList.contains('theme-' + THEME_LIGHT);

        if (logoDark && logoLight) {
            if (isDark) {
                logoDark.style.display = 'block';
                logoLight.style.display = 'none';
            } else if (isLight) {
                logoDark.style.display = 'none';
                logoLight.style.display = 'block';
            } else {
                logoDark.style.display = 'block';
                logoLight.style.display = 'none';
            }
        }
    };

    /**
     * Toggle between light and dark theme
     */
    const toggleTheme = function() {
        var isDark = themeRoot.classList.contains('theme-' + THEME_DARK);
        var newTheme = isDark ? THEME_LIGHT : THEME_DARK;
        setTheme(newTheme);
        updateThemeToggleButton();
        updateMenuButton();
        updateOverlaySocialButtons();
        updateLogo();
    };

    /**
     * Update menu button icon (hamburger/close)
     */
    const updateMenuButtonIcon = function() {
        if (!menuBtn || !overlay) return;
        var isOpen = overlay.classList.contains('header__overlay--open');
        var hamburgerIcon = menuBtn.querySelector('.header__menu-icon-hamburger');
        var closeIcon = menuBtn.querySelector('.header__menu-icon-close');

        if (hamburgerIcon && closeIcon) {
            if (isOpen) {
                hamburgerIcon.style.display = 'none';
                closeIcon.style.display = 'block';
                menuBtn.setAttribute('aria-label', 'Close menu');
            } else {
                hamburgerIcon.style.display = 'block';
                closeIcon.style.display = 'none';
                menuBtn.setAttribute('aria-label', 'Open menu');
            }
        }
    };

    /**
     * Lock body scroll when overlay is open
     */
    const lockBodyScroll = function() {
        document.body.style.overflow = 'hidden';
    };

    /**
     * Unlock body scroll when overlay is closed
     */
    const unlockBodyScroll = function() {
        document.body.style.overflow = '';
    };

    /**
     * Toggle overlay visibility
     */
    const toggleOverlay = function() {
        if (!overlay) return;
        var isOpen = overlay.classList.contains('header__overlay--open');

        if (isOpen) {
            overlay.classList.remove('header__overlay--open');
            unlockBodyScroll();
        } else {
            updateOverlayPosition();
            overlay.classList.add('header__overlay--open');
            lockBodyScroll();
        }
        updateMenuButtonIcon();
    };

    /**
     * Close overlay
     */
    const closeOverlay = function() {
        if (overlay) {
            overlay.classList.remove('header__overlay--open');
        }
        unlockBodyScroll();
        updateMenuButtonIcon();
    };

    /**
     * Update article tile collapsed heights for animation
     */
    const updateArticleTileCollapsedHeights = function() {
        var overlays = document.querySelectorAll('.header__overlay-article-overlay');
        overlays.forEach(function(overlayEl) {
            var titleEl = overlayEl.querySelector('.header__overlay-article-title');
            if (!titleEl) return;
            var titleHeight = Math.ceil(titleEl.getBoundingClientRect().height);
            var collapsedHeight = Math.max(60, titleHeight + 20);
            overlayEl.style.setProperty('--article-collapsed-height', collapsedHeight + 'px');
        });
    };

    /**
     * Schedule article tile height update
     */
    const scheduleArticleTileCollapsedHeightUpdate = function() {
        requestAnimationFrame(function() {
            requestAnimationFrame(function() {
                updateArticleTileCollapsedHeights();
            });
        });
    };

    /**
     * Initialize accordion menu functionality
     */
    const initMenu = function() {
        var allLinks = document.querySelectorAll('.header__overlay-menu-link, .header__overlay-submenu-link');
        var allItems = document.querySelectorAll('.header__overlay-menu-item, .header__overlay-submenu-item');

        // Add has-submenu class to links with submenus
        allLinks.forEach(function(link) {
            var parentItem = link.closest('.header__overlay-menu-item, .header__overlay-submenu-item');
            if (!parentItem) return;
            var submenu = parentItem.querySelector('.header__overlay-submenu');

            if (submenu) {
                if (link.classList.contains('header__overlay-menu-link')) {
                    link.classList.add('header__overlay-menu-link--has-submenu');
                } else {
                    link.classList.add('header__overlay-submenu-link--has-submenu');
                }
            }
        });

        /**
         * Handle accordion click behavior
         */
        var handleAccordion = function() {
            allLinks.forEach(function(link) {
                var parentItem = link.closest('.header__overlay-menu-item, .header__overlay-submenu-item');
                if (!parentItem) return;
                var submenu = parentItem.querySelector('.header__overlay-submenu');

                if (!submenu) {
                    return;
                }

                // Only add click handler if not an anchor tag
                if (link.tagName !== 'A') {
                    link.addEventListener('click', function(e) {
                        e.preventDefault();
                        var isOpen = submenu.classList.contains('header__overlay-submenu--open');

                        if (isOpen) {
                            // Close this submenu
                            submenu.classList.remove('header__overlay-submenu--open');
                            link.classList.remove('header__overlay-menu-link--open', 'header__overlay-submenu-link--open');

                            // Close nested submenus
                            var nestedSubmenus = submenu.querySelectorAll('.header__overlay-submenu');
                            var nestedLinks = submenu.querySelectorAll('.header__overlay-submenu-link');
                            nestedSubmenus.forEach(function(nested) {
                                nested.classList.remove('header__overlay-submenu--open');
                            });
                            nestedLinks.forEach(function(nestedLink) {
                                nestedLink.classList.remove('header__overlay-submenu-link--open');
                            });
                        } else {
                            // Open this submenu
                            submenu.classList.add('header__overlay-submenu--open');
                            if (link.classList.contains('header__overlay-menu-link')) {
                                link.classList.add('header__overlay-menu-link--open');
                            } else {
                                link.classList.add('header__overlay-submenu-link--open');
                            }
                        }
                    });
                }
            });
        };

        handleAccordion();
    };

    // Initial setup
    updateThemeToggleButton();
    updateMenuButton();
    updateOverlaySocialButtons();
    updateLogo();
    updateMenuButtonIcon();
    updateOverlayPosition();
    initMenu();
    scheduleArticleTileCollapsedHeightUpdate();

    // Event listeners
    if (menuBtn) {
        menuBtn.addEventListener('click', toggleOverlay);
    }

    if (themeToggle) {
        themeToggle.addEventListener('click', toggleTheme);
    }

    if (overlay) {
        overlay.addEventListener('click', function(evt) {
            if (evt.target === overlay) {
                closeOverlay();
            }
        });
    }

    // Handle window resize
    var resizeTimeout;
    window.addEventListener('resize', function() {
        if (resizeTimeout) {
            window.clearTimeout(resizeTimeout);
        }
        resizeTimeout = window.setTimeout(function() {
            scheduleArticleTileCollapsedHeightUpdate();
            updateOverlayPosition();
        }, 150);
    });

    // Observe header size changes
    if (typeof ResizeObserver !== 'undefined') {
        var ro = new ResizeObserver(function() {
            updateOverlayPosition();
        });
        ro.observe(header);
    }

})();


(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/Header/header.ts
  var require_header = __commonJS({
    "src/static-components/Header/header.ts"() {
      (() => {
        const DEFAULT_BACKGROUND = "#0f172a";
        const THEME_STORAGE_KEY = "header-theme";
        const THEME_DARK = "dark";
        const THEME_LIGHT = "light";
        const THEME_AUTO = "auto";
        const VALID_MODES = [THEME_DARK, THEME_LIGHT, THEME_AUTO];
        const header = document.querySelector('header[data-component="header"]');
        if (!header) return;
        const explicitThemeRoot = header.closest(".theme-light") || header.closest(".theme-dark");
        const themeRoot = explicitThemeRoot ?? document.documentElement;
        const usesExplicitThemeRoot = !!explicitThemeRoot;
        let currentMode = THEME_DARK;
        const resolveAutoTheme = () => {
          const hour = (/* @__PURE__ */ new Date()).getHours();
          return hour >= 7 && hour < 18 ? THEME_LIGHT : THEME_DARK;
        };
        const getEffectiveTheme = (mode) => {
          return mode === THEME_AUTO ? resolveAutoTheme() : mode;
        };
        const getStoredMode = () => {
          try {
            return localStorage.getItem(THEME_STORAGE_KEY);
          } catch {
            return null;
          }
        };
        const applyTheme = (mode) => {
          currentMode = mode;
          const effectiveTheme = getEffectiveTheme(mode);
          themeRoot.classList.remove("theme-dark", "theme-light");
          themeRoot.classList.add(`theme-${effectiveTheme}`);
          try {
            if (!usesExplicitThemeRoot) {
              localStorage.setItem(THEME_STORAGE_KEY, mode);
            }
          } catch {
          }
        };
        const resolveInitialMode = () => {
          if (!usesExplicitThemeRoot) {
            const stored = getStoredMode();
            if (stored && VALID_MODES.includes(stored)) {
              return stored;
            }
          }
          const dataDefault = header.getAttribute("data-default-theme");
          if (dataDefault && VALID_MODES.includes(dataDefault)) {
            return dataDefault;
          }
          if (themeRoot.classList.contains("theme-light")) return THEME_LIGHT;
          if (themeRoot.classList.contains("theme-dark")) return THEME_DARK;
          return THEME_DARK;
        };
        applyTheme(resolveInitialMode());
        const logoDark = header.querySelector(".header__logo-img-dark");
        const logoLight = header.querySelector(".header__logo-img-light");
        if (logoDark && !logoDark.alt) {
          logoDark.alt = "Logo";
        }
        if (logoLight && !logoLight.alt) {
          logoLight.alt = "Logo";
        }
        const overlay = document.querySelector(".header__overlay");
        const menuBtn = document.querySelector(".header__menu-btn");
        const themeToggle = document.querySelector(".header__theme-toggle");
        const updateOverlayPosition = () => {
          if (!overlay) return;
          const rect = header.getBoundingClientRect();
          const topPx = Math.max(0, Math.round(rect.bottom));
          const heightPx = Math.max(0, window.innerHeight - topPx);
          overlay.style.setProperty("--header-overlay-top", `${topPx}px`);
          overlay.style.setProperty("--header-overlay-height", `${heightPx}px`);
        };
        const resolveHeaderBackground = () => {
          const computed = getComputedStyle(header);
          return computed.background || computed.backgroundColor || DEFAULT_BACKGROUND;
        };
        const updateThemeToggleButton = () => {
          if (!themeToggle) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          themeToggle.classList.remove("btn-theme-dark", "btn-theme-light");
          themeToggle.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
          const sunIcon = themeToggle.querySelector(".header__theme-icon-sun");
          const moonIcon = themeToggle.querySelector(".header__theme-icon-moon");
          const autoIcon = themeToggle.querySelector(".header__theme-icon-auto");
          if (sunIcon) sunIcon.style.display = "none";
          if (moonIcon) moonIcon.style.display = "none";
          if (autoIcon) autoIcon.style.display = "none";
          if (currentMode === THEME_DARK && moonIcon) {
            moonIcon.style.display = "block";
          } else if (currentMode === THEME_LIGHT && sunIcon) {
            sunIcon.style.display = "block";
          } else if (currentMode === THEME_AUTO && autoIcon) {
            autoIcon.style.display = "block";
          }
          const labelMap = {
            [THEME_DARK]: "Theme: Dark \u2014 click for Light",
            [THEME_LIGHT]: "Theme: Light \u2014 click for Auto",
            [THEME_AUTO]: `Theme: Auto (${getEffectiveTheme(THEME_AUTO)}) \u2014 click for Dark`
          };
          themeToggle.setAttribute("aria-label", labelMap[currentMode] || "Toggle theme");
        };
        const updateMenuButton = () => {
          if (!menuBtn) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          menuBtn.classList.remove("btn-theme-dark", "btn-theme-light");
          menuBtn.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
        };
        const updateOverlaySocialButtons = () => {
          const socialLinks = document.querySelectorAll(".header__overlay-social-link");
          if (!socialLinks.length) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          socialLinks.forEach((el) => {
            el.classList.remove("btn-theme-dark", "btn-theme-light");
            el.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
          });
        };
        const updateActionButtons = () => {
          const actionBtns = header.querySelectorAll(".header__action-btn");
          if (!actionBtns.length) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          actionBtns.forEach((btn) => {
            btn.classList.remove("btn-theme-dark", "btn-theme-light");
            btn.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
          });
        };
        const updateLogo = () => {
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          if (logoDark && logoLight) {
            if (isDark) {
              logoDark.style.display = "block";
              logoLight.style.display = "none";
            } else {
              logoDark.style.display = "none";
              logoLight.style.display = "block";
            }
          }
        };
        const toggleTheme = () => {
          let newMode;
          if (currentMode === THEME_DARK) {
            newMode = THEME_LIGHT;
          } else if (currentMode === THEME_LIGHT) {
            newMode = THEME_AUTO;
          } else {
            newMode = THEME_DARK;
          }
          applyTheme(newMode);
          updateThemeToggleButton();
          updateMenuButton();
          updateOverlaySocialButtons();
          updateActionButtons();
          updateLogo();
        };
        const updateMenuButtonIcon = () => {
          if (!menuBtn || !overlay) return;
          const isOpen = overlay.classList.contains("header__overlay--open");
          const hamburgerIcon = menuBtn.querySelector(".header__menu-icon-hamburger");
          const closeIcon = menuBtn.querySelector(".header__menu-icon-close");
          if (hamburgerIcon && closeIcon) {
            if (isOpen) {
              hamburgerIcon.style.display = "none";
              closeIcon.style.display = "block";
              menuBtn.setAttribute("aria-label", "Close menu");
            } else {
              hamburgerIcon.style.display = "block";
              closeIcon.style.display = "none";
              menuBtn.setAttribute("aria-label", "Open menu");
            }
          }
        };
        const lockBodyScroll = () => {
          document.body.style.overflow = "hidden";
        };
        const unlockBodyScroll = () => {
          document.body.style.overflow = "";
        };
        const toggleOverlay = () => {
          if (!overlay) return;
          const isOpen = overlay.classList.contains("header__overlay--open");
          if (isOpen) {
            overlay.classList.remove("header__overlay--open");
            unlockBodyScroll();
          } else {
            updateOverlayPosition();
            overlay.classList.add("header__overlay--open");
            lockBodyScroll();
          }
          updateMenuButtonIcon();
        };
        const closeOverlay = () => {
          overlay?.classList.remove("header__overlay--open");
          unlockBodyScroll();
          updateMenuButtonIcon();
        };
        const updateArticleTileCollapsedHeights = () => {
          const overlays = document.querySelectorAll(".header__overlay-article-overlay");
          overlays.forEach((overlayEl) => {
            const titleEl = overlayEl.querySelector(".header__overlay-article-title");
            if (!titleEl) return;
            const titleHeight = Math.ceil(titleEl.getBoundingClientRect().height);
            const collapsedHeight = Math.max(60, titleHeight + 20);
            overlayEl.style.setProperty("--article-collapsed-height", `${collapsedHeight}px`);
          });
        };
        const scheduleArticleTileCollapsedHeightUpdate = () => {
          requestAnimationFrame(() => {
            requestAnimationFrame(() => {
              updateArticleTileCollapsedHeights();
            });
          });
        };
        const initMenu = () => {
          const allLinks = document.querySelectorAll(".header__overlay-menu-link, .header__overlay-submenu-link");
          const allItems = document.querySelectorAll(".header__overlay-menu-item, .header__overlay-submenu-item");
          allLinks.forEach((link) => {
            const parentItem = link.closest(".header__overlay-menu-item, .header__overlay-submenu-item");
            if (!parentItem) return;
            const submenu = parentItem.querySelector(".header__overlay-submenu");
            if (submenu) {
              link.classList.add(link.classList.contains("header__overlay-menu-link") ? "header__overlay-menu-link--has-submenu" : "header__overlay-submenu-link--has-submenu");
            }
          });
          const handleAccordion = () => {
            allLinks.forEach((link) => {
              const parentItem = link.closest(".header__overlay-menu-item, .header__overlay-submenu-item");
              if (!parentItem) return;
              const submenu = parentItem.querySelector(".header__overlay-submenu");
              if (!submenu) {
                return;
              }
              if (link.tagName !== "A") {
                link.addEventListener("click", (e) => {
                  e.preventDefault();
                  const isOpen = submenu.classList.contains("header__overlay-submenu--open");
                  if (isOpen) {
                    submenu.classList.remove("header__overlay-submenu--open");
                    link.classList.remove("header__overlay-menu-link--open", "header__overlay-submenu-link--open");
                    const nestedSubmenus = submenu.querySelectorAll(".header__overlay-submenu");
                    const nestedLinks = submenu.querySelectorAll(".header__overlay-submenu-link");
                    nestedSubmenus.forEach((nested) => nested.classList.remove("header__overlay-submenu--open"));
                    nestedLinks.forEach((nestedLink) => nestedLink.classList.remove("header__overlay-submenu-link--open"));
                  } else {
                    submenu.classList.add("header__overlay-submenu--open");
                    if (link.classList.contains("header__overlay-menu-link")) {
                      link.classList.add("header__overlay-menu-link--open");
                    } else {
                      link.classList.add("header__overlay-submenu-link--open");
                    }
                  }
                });
              }
            });
          };
          handleAccordion();
        };
        updateThemeToggleButton();
        updateMenuButton();
        updateOverlaySocialButtons();
        updateActionButtons();
        updateLogo();
        updateMenuButtonIcon();
        updateOverlayPosition();
        initMenu();
        scheduleArticleTileCollapsedHeightUpdate();
        menuBtn?.addEventListener("click", toggleOverlay);
        themeToggle?.addEventListener("click", toggleTheme);
        overlay?.addEventListener("click", (evt) => {
          if (evt.target === overlay) {
            closeOverlay();
          }
        });
        let resizeTimeout;
        window.addEventListener("resize", () => {
          if (resizeTimeout) window.clearTimeout(resizeTimeout);
          resizeTimeout = window.setTimeout(() => {
            scheduleArticleTileCollapsedHeightUpdate();
            updateOverlayPosition();
          }, 150);
        });
        if (typeof ResizeObserver !== "undefined") {
          const ro = new ResizeObserver(() => {
            updateOverlayPosition();
          });
          ro.observe(header);
        }
      })();
    }
  });
  require_header();
})();

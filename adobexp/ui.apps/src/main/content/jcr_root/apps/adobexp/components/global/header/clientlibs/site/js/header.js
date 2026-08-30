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
        const SIDEBAR_WIDTH_KEY = "header-sidebar-width";
        const SIDEBAR_OPEN_KEY = "header-sidebar-open";
        const THEME_DARK = "dark";
        const THEME_LIGHT = "light";
        const THEME_AUTO = "auto";
        const VALID_MODES = [THEME_DARK, THEME_LIGHT, THEME_AUTO];
        const MENU_VARIANT_OVERLAY = "overlay";
        const MENU_VARIANT_SIDEBAR = "sidebar";
        const SIDEBAR_MIN_WIDTH = 220;
        const SIDEBAR_MAX_WIDTH = 560;
        const SIDEBAR_DEFAULT_WIDTH = 300;
        const SPLIT_CLASS = "header-sidebar-split";
        const RESIZING_CLASS = "header-sidebar-resizing";
        const SMALL_DEVICE_MQ = "(max-width: 1024px)";
        const ACTIONS_GROUP_OPEN_CLASS = "header__actions-group--open";
        const header = document.querySelector('header[data-component="header"]');
        if (!header) return;
        const menuVariantAttr = (header.getAttribute("data-menu-variant") || "").toLowerCase();
        const menuVariant = menuVariantAttr === MENU_VARIANT_SIDEBAR ? MENU_VARIANT_SIDEBAR : MENU_VARIANT_OVERLAY;
        const sidebarDefaultOpen = (header.getAttribute("data-sidebar-default-open") || "").toLowerCase() === "true";
        const isSmallDevice = () => window.matchMedia(SMALL_DEVICE_MQ).matches;
        const closestThemed = header.closest(".theme-light, .theme-dark");
        const usesExplicitThemeRoot = !!(closestThemed && closestThemed !== document.documentElement && closestThemed !== document.body);
        const themeRoot = usesExplicitThemeRoot ? closestThemed : document.documentElement;
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
          const central = window.AdobeXPTheme;
          if (!usesExplicitThemeRoot && central?.apply) {
            central.apply(mode, { silent: false });
          } else {
            themeRoot.classList.remove("theme-dark", "theme-light");
            themeRoot.classList.add(`theme-${effectiveTheme}`);
            if (!usesExplicitThemeRoot) {
              try {
                localStorage.setItem(THEME_STORAGE_KEY, mode);
              } catch {
              }
              document.documentElement.setAttribute("data-theme", effectiveTheme);
              if (document.body) {
                document.body.classList.remove("theme-dark", "theme-light");
                document.body.setAttribute("data-theme", effectiveTheme);
              }
            }
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
        const sidebar = document.querySelector(".header__sidebar");
        const sidebarBackdrop = document.querySelector("[data-header-sidebar-backdrop]");
        const sidebarResizer = document.querySelector(".header__sidebar-resizer");
        const sidebarCollapseBtn = document.querySelector(".header__sidebar-collapse");
        const sidebarSearchInput = document.querySelector(".header__sidebar-search-input");
        const menuBtn = document.querySelector(".header__menu-btn");
        const themeToggle = document.querySelector(".header__theme-toggle");
        const kebabBtn = document.querySelector(".header__kebab-btn");
        const actionsGroup = document.querySelector(".header__actions-group");
        const langNav = header.querySelector(".header__lang");
        const langToggle = header.querySelector(".header__lang-toggle");
        const langMenu = header.querySelector(".header__lang-menu");
        const LANG_OPEN_CLASS = "header__lang--open";
        const LANG_COOKIE = "vw-lang";
        const VW_LANGS = ["en", "de", "es", "fr"];
        const isVwLang = (code) => !!code && VW_LANGS.includes(code);
        const isBrandedVwHost = () => /(^|\.)demo-vw(\.|$)/i.test(location.hostname);
        const isVwSite = () => isBrandedVwHost() || /\/content\/vw(?:\/|$)/.test(location.pathname) || /\/content\/experience-fragments\/vw(?:\/|$)/.test(location.pathname);
        const setLangCookie = (code) => {
          if (!isVwLang(code) || !isVwSite()) return;
          const secure = location.protocol === "https:" ? "; Secure" : "";
          document.cookie = `${LANG_COOKIE}=${code}; Path=/; Max-Age=31536000; SameSite=Lax${secure}`;
        };
        const langFromPath = () => {
          const match = location.pathname.match(/\/language-masters\/(en|de|es|fr)(?:\/|\.html|$)/) || location.pathname.match(/^\/(en|de|es|fr)(?:\/|\.html|$)/);
          return match && isVwLang(match[1]) ? match[1] : null;
        };
        const toPrettyLangHref = (href) => {
          try {
            const url = new URL(href, location.origin);
            const match = url.pathname.match(
              /^\/content\/vw\/language-masters\/(en|de|es|fr)(?:\.html)?(?:\/(.*))?$/
            );
            if (!match) return href;
            const rest = (match[2] || "").replace(/\/$/, "");
            return rest ? `/${match[1]}/${rest}` : `/${match[1]}`;
          } catch {
            return href;
          }
        };
        const persistLangFromPage = () => {
          if (!isVwSite()) return;
          const fromPath = langFromPath();
          if (fromPath) {
            setLangCookie(fromPath);
            return;
          }
          const current = header.querySelector(".header__lang-link--current");
          const code = current?.getAttribute("hreflang") || current?.getAttribute("lang");
          if (code) setLangCookie(code);
        };
        persistLangFromPage();
        if (isBrandedVwHost()) {
          langMenu?.querySelectorAll(".header__lang-link").forEach((link) => {
            const href = link.getAttribute("href");
            if (href) link.setAttribute("href", toPrettyLangHref(href));
          });
        }
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
        const updateLogoPair = (darkEl, lightEl) => {
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          if (darkEl && lightEl) {
            if (isDark) {
              darkEl.style.display = "block";
              lightEl.style.display = "none";
            } else {
              darkEl.style.display = "none";
              lightEl.style.display = "block";
            }
          }
        };
        const updateLogo = () => {
          updateLogoPair(logoDark, logoLight);
          const sidebarDark = document.querySelector(
            ".header__sidebar .header__logo-img-dark, .header__sidebar .header__sidebar-logo-img.header__logo-img-dark"
          );
          const sidebarLight = document.querySelector(
            ".header__sidebar .header__logo-img-light, .header__sidebar .header__sidebar-logo-img.header__logo-img-light"
          );
          updateLogoPair(sidebarDark, sidebarLight);
          const collapseBtn = sidebarCollapseBtn;
          if (collapseBtn) {
            const effectiveTheme = getEffectiveTheme(currentMode);
            const isDark = effectiveTheme === THEME_DARK;
            collapseBtn.classList.remove("btn-theme-dark", "btn-theme-light");
            collapseBtn.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
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
          updateKebabButton();
          updateLangToggle();
          updateOverlaySocialButtons();
          updateActionButtons();
          updateLogo();
        };
        const isMenuOpen = () => {
          if (menuVariant === MENU_VARIANT_SIDEBAR) {
            return !!sidebar?.classList.contains("header__sidebar--open");
          }
          return !!overlay?.classList.contains("header__overlay--open");
        };
        const updateMenuButtonIcon = () => {
          if (!menuBtn) return;
          const isOpen = isMenuOpen();
          const expandIcon = menuBtn.querySelector(".header__menu-icon-expand");
          const hamburgerIcon = menuBtn.querySelector(".header__menu-icon-hamburger");
          const closeIcon = menuBtn.querySelector(".header__menu-icon-close");
          const useHamburgerClose = menuVariant !== MENU_VARIANT_SIDEBAR || isSmallDevice() || !expandIcon;
          if (!useHamburgerClose && expandIcon) {
            expandIcon.style.display = "block";
            if (hamburgerIcon) hamburgerIcon.style.display = "none";
            if (closeIcon) closeIcon.style.display = "none";
            menuBtn.setAttribute("aria-label", isOpen ? "Close menu" : "Open menu");
          } else if (hamburgerIcon && closeIcon) {
            if (expandIcon) expandIcon.style.display = "none";
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
          menuBtn.setAttribute("aria-expanded", isOpen ? "true" : "false");
        };
        const isKebabOpen = () => !!actionsGroup?.classList.contains(ACTIONS_GROUP_OPEN_CLASS);
        const closeKebab = () => {
          if (!actionsGroup) return;
          actionsGroup.classList.remove(ACTIONS_GROUP_OPEN_CLASS);
          kebabBtn?.setAttribute("aria-expanded", "false");
        };
        const openKebab = () => {
          if (!actionsGroup) return;
          actionsGroup.classList.add(ACTIONS_GROUP_OPEN_CLASS);
          kebabBtn?.setAttribute("aria-expanded", "true");
        };
        const toggleKebab = () => {
          if (isKebabOpen()) {
            closeKebab();
          } else {
            openKebab();
          }
        };
        const updateKebabButton = () => {
          if (!kebabBtn) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          kebabBtn.classList.remove("btn-theme-dark", "btn-theme-light");
          kebabBtn.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
        };
        const isLangOpen = () => !!langNav?.classList.contains(LANG_OPEN_CLASS);
        let suppressOutsideCloseUntil = 0;
        const closeLang = () => {
          if (!langNav || !langToggle || !langMenu) return;
          langNav.classList.remove(LANG_OPEN_CLASS);
          langMenu.hidden = true;
          langToggle.setAttribute("aria-expanded", "false");
        };
        const openLang = () => {
          if (!langNav || !langToggle || !langMenu) return;
          langNav.classList.add(LANG_OPEN_CLASS);
          langMenu.hidden = false;
          langToggle.setAttribute("aria-expanded", "true");
          suppressOutsideCloseUntil = Date.now() + 400;
        };
        const toggleLang = () => {
          if (isLangOpen()) {
            closeLang();
          } else {
            openLang();
          }
        };
        const updateLangToggle = () => {
          if (!langToggle) return;
          const effectiveTheme = getEffectiveTheme(currentMode);
          const isDark = effectiveTheme === THEME_DARK;
          langToggle.classList.remove("btn-theme-dark", "btn-theme-light");
          langToggle.classList.add(isDark ? "btn-theme-dark" : "btn-theme-light");
        };
        const lockBodyScroll = () => {
          document.body.style.overflow = "hidden";
        };
        const unlockBodyScroll = () => {
          document.body.style.overflow = "";
        };
        const clampSidebarWidth = (width) => {
          const maxAllowed = Math.min(
            SIDEBAR_MAX_WIDTH,
            Math.max(SIDEBAR_MIN_WIDTH, Math.floor(window.innerWidth - 280))
          );
          return Math.max(SIDEBAR_MIN_WIDTH, Math.min(maxAllowed, Math.round(width)));
        };
        const persistSidebarOpen = (open) => {
          try {
            localStorage.setItem(SIDEBAR_OPEN_KEY, open ? "true" : "false");
          } catch {
          }
        };
        const readStoredSidebarOpen = () => {
          try {
            const stored = localStorage.getItem(SIDEBAR_OPEN_KEY);
            if (stored === null) return null;
            return stored === "true";
          } catch {
            return null;
          }
        };
        const resolveInitialSidebarOpen = () => {
          if (isSmallDevice()) return false;
          const stored = readStoredSidebarOpen();
          if (stored !== null) return stored;
          return sidebarDefaultOpen;
        };
        const applySidebarWidth = (width) => {
          if (!sidebar) return;
          const next = clampSidebarWidth(width);
          const value = `${next}px`;
          document.documentElement.style.setProperty("--header-sidebar-width", value);
          sidebar.style.setProperty("--header-sidebar-width", value);
          try {
            localStorage.setItem(SIDEBAR_WIDTH_KEY, String(next));
          } catch {
          }
        };
        const restoreSidebarWidth = () => {
          let width = SIDEBAR_DEFAULT_WIDTH;
          try {
            const stored = localStorage.getItem(SIDEBAR_WIDTH_KEY);
            if (stored) {
              const parsed = Number(stored);
              if (!Number.isNaN(parsed)) width = parsed;
            }
          } catch {
          }
          applySidebarWidth(width);
        };
        const openSidebar = () => {
          if (!sidebar) return;
          sidebar.hidden = false;
          sidebar.setAttribute("aria-hidden", "false");
          sidebar.classList.add("header__sidebar--open");
          if (isSmallDevice()) {
            document.documentElement.classList.remove(SPLIT_CLASS);
            lockBodyScroll();
          } else {
            document.documentElement.classList.add(SPLIT_CLASS);
          }
          if (sidebarBackdrop) {
            sidebarBackdrop.hidden = true;
            sidebarBackdrop.classList.remove("header__sidebar-backdrop--open");
          }
          if (!isSmallDevice()) {
            persistSidebarOpen(true);
          }
          updateMenuButtonIcon();
        };
        const closeSidebar = () => {
          if (!sidebar) return;
          sidebar.classList.remove("header__sidebar--open");
          sidebar.setAttribute("aria-hidden", "true");
          document.documentElement.classList.remove(SPLIT_CLASS);
          unlockBodyScroll();
          if (sidebarBackdrop) {
            sidebarBackdrop.hidden = true;
            sidebarBackdrop.classList.remove("header__sidebar-backdrop--open");
          }
          window.setTimeout(() => {
            if (!sidebar.classList.contains("header__sidebar--open")) {
              sidebar.hidden = true;
            }
          }, 280);
          if (!isSmallDevice()) {
            persistSidebarOpen(false);
          }
          updateMenuButtonIcon();
        };
        const toggleSidebar = () => {
          if (!sidebar) return;
          if (sidebar.classList.contains("header__sidebar--open")) {
            closeSidebar();
          } else {
            openSidebar();
          }
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
        const toggleMenu = () => {
          if (menuVariant === MENU_VARIANT_SIDEBAR) {
            toggleSidebar();
          } else {
            toggleOverlay();
          }
        };
        const closeMenu = () => {
          if (menuVariant === MENU_VARIANT_SIDEBAR) {
            closeSidebar();
          } else {
            closeOverlay();
          }
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
        const initOverlayMenu = () => {
          const allLinks = document.querySelectorAll(".header__overlay-menu-link, .header__overlay-submenu-link");
          allLinks.forEach((link) => {
            const parentItem = link.closest(".header__overlay-menu-item, .header__overlay-submenu-item");
            if (!parentItem) return;
            const submenu = parentItem.querySelector(".header__overlay-submenu");
            if (submenu) {
              link.classList.add(link.classList.contains("header__overlay-menu-link") ? "header__overlay-menu-link--has-submenu" : "header__overlay-submenu-link--has-submenu");
            }
          });
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
        const initSidebarMenu = () => {
          if (!sidebar) return;
          if (sidebarBackdrop) {
            sidebarBackdrop.hidden = true;
            sidebarBackdrop.setAttribute("aria-hidden", "true");
          }
          restoreSidebarWidth();
          if (resolveInitialSidebarOpen()) {
            openSidebar();
          } else {
            closeSidebar();
          }
          sidebar.querySelectorAll(".header__sidebar-group-toggle").forEach((toggle) => {
            toggle.addEventListener("click", () => {
              const group = toggle.closest(".header__sidebar-group");
              if (!group) return;
              const willOpen = !group.classList.contains("header__sidebar-group--open");
              group.classList.toggle("header__sidebar-group--open", willOpen);
              toggle.setAttribute("aria-expanded", willOpen ? "true" : "false");
            });
          });
          const buildHeaderTopnav = () => {
            const topnav = header.querySelector(".header__topnav");
            if (!topnav) return;
            topnav.replaceChildren();
            const wrapItem = (child) => {
              const wrap = document.createElement("div");
              wrap.className = "header__topnav-item";
              wrap.appendChild(child);
              return wrap;
            };
            const addStandaloneLink = (link) => {
              const label = link.querySelector(".header__sidebar-link-text")?.textContent?.trim() || link.textContent?.trim() || "";
              if (!label) return;
              const item = document.createElement("a");
              item.className = "header__topnav-link";
              item.href = link.getAttribute("href") || "#";
              item.textContent = label;
              if (link.classList.contains("header__sidebar-link--active") || link.getAttribute("aria-current") === "page") {
                item.classList.add("header__topnav-link--active");
                item.setAttribute("aria-current", "page");
              }
              topnav.appendChild(wrapItem(item));
            };
            const sidebarNav = sidebar.querySelector(".header__sidebar-nav");
            const navChildren = sidebarNav ? Array.from(sidebarNav.children) : [];
            let seenGroup = false;
            navChildren.forEach((child) => {
              if (child.classList.contains("header__sidebar-list--standalone")) {
                if (seenGroup) {
                  return;
                }
                child.querySelectorAll(".header__sidebar-link").forEach(addStandaloneLink);
                return;
              }
              if (!child.classList.contains("header__sidebar-group")) {
                return;
              }
              seenGroup = true;
              const group = child;
              const toggle = group.querySelector(".header__sidebar-group-toggle");
              const label = toggle?.querySelector("span")?.textContent?.trim() || toggle?.childNodes[0]?.textContent?.trim() || "";
              if (!label) return;
              const childLinks = Array.from(
                group.querySelectorAll(":scope > .header__sidebar-list .header__sidebar-link")
              );
              const wrap = document.createElement("div");
              wrap.className = "header__topnav-item header__topnav-item--has-dropdown";
              const trigger = document.createElement("button");
              trigger.type = "button";
              trigger.className = "header__topnav-link header__topnav-link--group";
              trigger.setAttribute("aria-haspopup", childLinks.length > 0 ? "true" : "false");
              trigger.setAttribute("aria-expanded", "false");
              trigger.textContent = label;
              trigger.addEventListener("click", () => {
                openSidebar();
                group.classList.add("header__sidebar-group--open");
                toggle?.setAttribute("aria-expanded", "true");
              });
              wrap.appendChild(trigger);
              if (childLinks.length > 0) {
                const dropdown = document.createElement("div");
                dropdown.className = "header__topnav-dropdown";
                dropdown.setAttribute("role", "menu");
                childLinks.forEach((link) => {
                  const childLabel = link.querySelector(".header__sidebar-link-text")?.textContent?.trim() || link.textContent?.trim() || "";
                  if (!childLabel) return;
                  const child = document.createElement("a");
                  child.className = "header__topnav-dropdown-link";
                  child.href = link.getAttribute("href") || "#";
                  child.setAttribute("role", "menuitem");
                  child.textContent = childLabel;
                  if (link.classList.contains("header__sidebar-link--active") || link.getAttribute("aria-current") === "page") {
                    child.classList.add("header__topnav-dropdown-link--active");
                    child.setAttribute("aria-current", "page");
                  }
                  dropdown.appendChild(child);
                });
                wrap.appendChild(dropdown);
                const syncExpanded = () => {
                  const open = wrap.matches(":hover, :focus-within");
                  trigger.setAttribute("aria-expanded", open ? "true" : "false");
                };
                wrap.addEventListener("mouseenter", syncExpanded);
                wrap.addEventListener("mouseleave", syncExpanded);
                wrap.addEventListener("focusin", syncExpanded);
                wrap.addEventListener("focusout", syncExpanded);
              }
              topnav.appendChild(wrap);
            });
          };
          buildHeaderTopnav();
          sidebarSearchInput?.addEventListener("input", () => {
            const query = sidebarSearchInput.value.trim().toLowerCase();
            sidebar.querySelectorAll(".header__sidebar-list--standalone .header__sidebar-item").forEach((item) => {
              const text = item.textContent?.toLowerCase() || "";
              const match = !query || text.includes(query);
              item.style.display = match ? "" : "none";
            });
            sidebar.querySelectorAll(".header__sidebar-group").forEach((group) => {
              const items = group.querySelectorAll(".header__sidebar-item");
              let visibleCount = 0;
              items.forEach((item) => {
                const text = item.textContent?.toLowerCase() || "";
                const match = !query || text.includes(query);
                item.style.display = match ? "" : "none";
                if (match) visibleCount += 1;
              });
              if (query) {
                group.style.display = visibleCount > 0 ? "" : "none";
                if (visibleCount > 0) {
                  group.classList.add("header__sidebar-group--open");
                  const toggle = group.querySelector(".header__sidebar-group-toggle");
                  toggle?.setAttribute("aria-expanded", "true");
                }
              } else {
                group.style.display = "";
              }
            });
          });
          if (sidebarResizer) {
            let dragging = false;
            const onPointerMove = (event) => {
              if (!dragging) return;
              applySidebarWidth(event.clientX);
            };
            const stopDragging = () => {
              if (!dragging) return;
              dragging = false;
              sidebarResizer.classList.remove("header__sidebar-resizer--dragging");
              document.documentElement.classList.remove(RESIZING_CLASS);
              document.body.classList.remove("header-sidebar-resizing");
              window.removeEventListener("pointermove", onPointerMove);
              window.removeEventListener("pointerup", stopDragging);
            };
            const startDragging = (event) => {
              event.preventDefault();
              if (!sidebar.classList.contains("header__sidebar--open")) {
                openSidebar();
              }
              dragging = true;
              sidebarResizer.classList.add("header__sidebar-resizer--dragging");
              document.documentElement.classList.add(RESIZING_CLASS);
              document.body.classList.add("header-sidebar-resizing");
              window.addEventListener("pointermove", onPointerMove);
              window.addEventListener("pointerup", stopDragging);
            };
            sidebarResizer.addEventListener("pointerdown", startDragging);
            sidebarResizer.addEventListener("keydown", (event) => {
              const current = Number.parseInt(
                getComputedStyle(document.documentElement).getPropertyValue("--header-sidebar-width"),
                10
              ) || SIDEBAR_DEFAULT_WIDTH;
              if (event.key === "ArrowLeft") {
                event.preventDefault();
                applySidebarWidth(current - 16);
              } else if (event.key === "ArrowRight") {
                event.preventDefault();
                applySidebarWidth(current + 16);
              } else if (event.key === "Home") {
                event.preventDefault();
                applySidebarWidth(SIDEBAR_MIN_WIDTH);
              } else if (event.key === "End") {
                event.preventDefault();
                applySidebarWidth(SIDEBAR_MAX_WIDTH);
              }
            });
          }
          sidebarCollapseBtn?.addEventListener("click", closeSidebar);
          document.addEventListener("keydown", (event) => {
            if (event.key === "Escape" && isMenuOpen()) {
              closeSidebar();
            }
          });
        };
        updateThemeToggleButton();
        updateMenuButton();
        updateKebabButton();
        updateLangToggle();
        updateOverlaySocialButtons();
        updateActionButtons();
        updateLogo();
        updateMenuButtonIcon();
        updateOverlayPosition();
        if (menuVariant === MENU_VARIANT_SIDEBAR) {
          initSidebarMenu();
        } else {
          initOverlayMenu();
          scheduleArticleTileCollapsedHeightUpdate();
        }
        menuBtn?.addEventListener("click", toggleMenu);
        themeToggle?.addEventListener("click", toggleTheme);
        kebabBtn?.addEventListener("click", (evt) => {
          evt.stopPropagation();
          closeLang();
          toggleKebab();
        });
        langToggle?.addEventListener("click", (evt) => {
          evt.preventDefault();
          evt.stopPropagation();
          toggleLang();
        });
        actionsGroup?.querySelectorAll(".header__action-btn").forEach((link) => {
          link.addEventListener("click", () => {
            closeKebab();
          });
        });
        langMenu?.querySelectorAll(".header__lang-link").forEach((link) => {
          link.addEventListener("click", () => {
            const code = link.getAttribute("hreflang") || link.getAttribute("lang") || langFromPath();
            if (code) setLangCookie(code);
            closeLang();
          });
        });
        document.addEventListener("click", (evt) => {
          const target = evt.target;
          if (!target) return;
          if (Date.now() < suppressOutsideCloseUntil) return;
          if (isLangOpen() && !langNav?.contains(target)) {
            closeLang();
          }
          if (!isKebabOpen()) return;
          if (actionsGroup?.contains(target) || kebabBtn?.contains(target) || langNav?.contains(target)) return;
          closeKebab();
        });
        document.addEventListener("keydown", (evt) => {
          if (evt.key === "Escape") {
            if (isLangOpen()) {
              closeLang();
            }
            if (isKebabOpen()) {
              closeKebab();
            }
          }
        });
        overlay?.addEventListener("click", (evt) => {
          if (evt.target === overlay) {
            closeMenu();
          }
        });
        let resizeTimeout;
        window.addEventListener("resize", () => {
          if (resizeTimeout) window.clearTimeout(resizeTimeout);
          resizeTimeout = window.setTimeout(() => {
            updateMenuButtonIcon();
            if (!isSmallDevice()) {
              closeKebab();
              closeLang();
            }
            if (menuVariant === MENU_VARIANT_OVERLAY) {
              scheduleArticleTileCollapsedHeightUpdate();
              updateOverlayPosition();
            } else if (sidebar) {
              if (isSmallDevice() && sidebar.classList.contains("header__sidebar--open")) {
                document.documentElement.classList.remove(SPLIT_CLASS);
              }
              const current = Number.parseInt(
                getComputedStyle(sidebar).getPropertyValue("--header-sidebar-width"),
                10
              ) || SIDEBAR_DEFAULT_WIDTH;
              applySidebarWidth(current);
            }
          }, 150);
        });
        if (menuVariant === MENU_VARIANT_OVERLAY && typeof ResizeObserver !== "undefined") {
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

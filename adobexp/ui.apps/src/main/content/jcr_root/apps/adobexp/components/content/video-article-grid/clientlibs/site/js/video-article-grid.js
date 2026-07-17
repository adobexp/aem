(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/VideoArticleGrid/video-article-grid.ts
  var require_video_article_grid = __commonJS({
    "src/static-components/VideoArticleGrid/video-article-grid.ts"() {
      var CONFIG_DEFAULTS = {
        cardsInSingleRow: 3,
        playOnHover: true,
        maxArticleInASinglePage: 9,
        defaultSortBy: "publishedDate",
        defaultOrder: "dsc"
      };
      var LABEL_DEFAULTS = {
        searchPlaceholder: "Search tutorials\u2026",
        prevPage: "Previous Page",
        nextPage: "Next Page",
        pageIndicator: "Page {currentPage} of {totalPages}",
        sortByLabel: "Sort By",
        sortByPublishedDate: "Published Date",
        sortByCreatedDate: "Created Date",
        sortByTitle: "Title",
        sortOrderLabel: "Order",
        sortOrderAsc: "ASC",
        sortOrderDsc: "DSC",
        noResults: "No tutorials match your search.",
        noItems: "No tutorials available."
      };
      var PLAY_SVG = `<svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M8 5v14l11-7z"/></svg>`;
      var SEARCH_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>`;
      var CHEVRON_LEFT_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>`;
      var CHEVRON_RIGHT_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 18l6-6-6-6"/></svg>`;
      var VideoArticleGrid = class {
        container;
        allItems;
        filteredItems;
        config;
        labels;
        currentPage = 1;
        searchTerm = "";
        sortField;
        sortOrder;
        // DOM refs
        gridEl;
        paginationEl;
        searchInput;
        sortBySelect;
        sortOrderSelect;
        pageIndicator;
        prevBtn;
        nextBtn;
        constructor(container) {
          this.container = container;
          this.allItems = this.parseItems();
          this.config = this.parseConfig();
          this.labels = this.parseLabels();
          this.sortField = this.config.defaultSortBy;
          this.sortOrder = this.config.defaultOrder;
          this.filteredItems = [...this.allItems];
          this.applySortToFiltered();
          this.build();
        }
        /* -------------------- data parsing -------------------- */
        parseItems() {
          try {
            const raw = this.container.getAttribute("data-items") || "[]";
            return JSON.parse(raw);
          } catch {
            console.warn("[VideoArticleGrid] Invalid data-items JSON");
            return [];
          }
        }
        parseConfig() {
          try {
            const raw = this.container.getAttribute("data-config") || "{}";
            return { ...CONFIG_DEFAULTS, ...JSON.parse(raw) };
          } catch {
            console.warn("[VideoArticleGrid] Invalid data-config JSON");
            return { ...CONFIG_DEFAULTS };
          }
        }
        parseLabels() {
          try {
            const raw = this.container.getAttribute("data-labels") || "{}";
            return { ...LABEL_DEFAULTS, ...JSON.parse(raw) };
          } catch {
            console.warn("[VideoArticleGrid] Invalid data-labels JSON");
            return { ...LABEL_DEFAULTS };
          }
        }
        /* -------------------- computed helpers -------------------- */
        get totalPages() {
          return Math.max(1, Math.ceil(this.filteredItems.length / this.config.maxArticleInASinglePage));
        }
        get pageItems() {
          const start = (this.currentPage - 1) * this.config.maxArticleInASinglePage;
          return this.filteredItems.slice(start, start + this.config.maxArticleInASinglePage);
        }
        /* -------------------- DOM scaffold -------------------- */
        build() {
          this.container.style.setProperty("--vag-cols", String(this.config.cardsInSingleRow));
          const toolbar = document.createElement("div");
          toolbar.className = "video-article-grid__toolbar";
          const searchWrap = document.createElement("div");
          searchWrap.className = "video-article-grid__search";
          const searchIcon = document.createElement("span");
          searchIcon.className = "video-article-grid__search-icon";
          searchIcon.innerHTML = SEARCH_SVG;
          this.searchInput = document.createElement("input");
          this.searchInput.type = "text";
          this.searchInput.className = "video-article-grid__search-input";
          this.searchInput.placeholder = this.labels.searchPlaceholder;
          this.searchInput.setAttribute("aria-label", this.labels.searchPlaceholder);
          this.searchInput.addEventListener("input", () => this.onSearch());
          searchWrap.appendChild(searchIcon);
          searchWrap.appendChild(this.searchInput);
          toolbar.appendChild(searchWrap);
          this.sortBySelect = document.createElement("select");
          this.sortBySelect.className = "video-article-grid__sort-select";
          this.sortBySelect.setAttribute("aria-label", this.labels.sortByLabel);
          const sortByOptions = [
            { value: "publishedDate", text: this.labels.sortByPublishedDate },
            { value: "createdDate", text: this.labels.sortByCreatedDate },
            { value: "title", text: this.labels.sortByTitle }
          ];
          sortByOptions.forEach((opt) => {
            const option = document.createElement("option");
            option.value = opt.value;
            option.textContent = opt.text;
            this.sortBySelect.appendChild(option);
          });
          this.sortBySelect.value = this.sortField;
          this.sortBySelect.addEventListener("change", () => this.onSortChange());
          toolbar.appendChild(this.sortBySelect);
          this.sortOrderSelect = document.createElement("select");
          this.sortOrderSelect.className = "video-article-grid__sort-select";
          this.sortOrderSelect.setAttribute("aria-label", this.labels.sortOrderLabel);
          const sortOrderOptions = [
            { value: "asc", text: this.labels.sortOrderAsc },
            { value: "dsc", text: this.labels.sortOrderDsc }
          ];
          sortOrderOptions.forEach((opt) => {
            const option = document.createElement("option");
            option.value = opt.value;
            option.textContent = opt.text;
            this.sortOrderSelect.appendChild(option);
          });
          this.sortOrderSelect.value = this.sortOrder;
          this.sortOrderSelect.addEventListener("change", () => this.onSortChange());
          toolbar.appendChild(this.sortOrderSelect);
          this.container.appendChild(toolbar);
          this.gridEl = document.createElement("div");
          this.gridEl.className = "video-article-grid__cards";
          this.container.appendChild(this.gridEl);
          this.paginationEl = document.createElement("nav");
          this.paginationEl.className = "video-article-grid__pagination";
          this.paginationEl.setAttribute("aria-label", "Pagination");
          this.prevBtn = document.createElement("button");
          this.prevBtn.className = "video-article-grid__page-btn video-article-grid__page-btn--prev";
          this.prevBtn.innerHTML = `${CHEVRON_LEFT_SVG}<span>${this.escapeHtml(this.labels.prevPage)}</span>`;
          this.prevBtn.addEventListener("click", () => this.goToPage(this.currentPage - 1));
          this.pageIndicator = document.createElement("span");
          this.pageIndicator.className = "video-article-grid__page-indicator";
          this.nextBtn = document.createElement("button");
          this.nextBtn.className = "video-article-grid__page-btn video-article-grid__page-btn--next";
          this.nextBtn.innerHTML = `<span>${this.escapeHtml(this.labels.nextPage)}</span>${CHEVRON_RIGHT_SVG}`;
          this.nextBtn.addEventListener("click", () => this.goToPage(this.currentPage + 1));
          this.paginationEl.appendChild(this.prevBtn);
          this.paginationEl.appendChild(this.pageIndicator);
          this.paginationEl.appendChild(this.nextBtn);
          this.container.appendChild(this.paginationEl);
          this.renderCards();
          this.renderPagination();
        }
        /* -------------------- search -------------------- */
        onSearch() {
          this.searchTerm = this.searchInput.value.trim().toLowerCase();
          this.applyFilterAndSort();
        }
        /* -------------------- sorting -------------------- */
        onSortChange() {
          this.sortField = this.sortBySelect.value;
          this.sortOrder = this.sortOrderSelect.value;
          this.applyFilterAndSort();
        }
        applySortToFiltered() {
          const dir = this.sortOrder === "asc" ? 1 : -1;
          this.filteredItems.sort((a, b) => {
            let cmp = 0;
            if (this.sortField === "title") {
              cmp = a.title.localeCompare(b.title);
            } else {
              const da = new Date(a[this.sortField]).getTime() || 0;
              const db = new Date(b[this.sortField]).getTime() || 0;
              cmp = da - db;
            }
            return cmp * dir;
          });
        }
        applyFilterAndSort() {
          if (this.searchTerm === "") {
            this.filteredItems = [...this.allItems];
          } else {
            this.filteredItems = this.allItems.filter((item) => {
              const haystack = [item.title, item.description || "", item.badge || ""].join(" ").toLowerCase();
              return haystack.includes(this.searchTerm);
            });
          }
          this.applySortToFiltered();
          this.currentPage = 1;
          this.renderCards();
          this.renderPagination();
        }
        /* -------------------- pagination -------------------- */
        goToPage(page) {
          if (page < 1 || page > this.totalPages) return;
          this.currentPage = page;
          this.renderCards();
          this.renderPagination();
          this.gridEl.scrollIntoView({ behavior: "smooth", block: "start" });
        }
        renderPagination() {
          const text = this.labels.pageIndicator.replace("{currentPage}", String(this.currentPage)).replace("{totalPages}", String(this.totalPages));
          this.pageIndicator.textContent = text;
          this.prevBtn.disabled = this.currentPage <= 1;
          this.nextBtn.disabled = this.currentPage >= this.totalPages;
          this.paginationEl.style.display = this.totalPages <= 1 ? "none" : "";
        }
        /* -------------------- card rendering -------------------- */
        renderCards() {
          this.gridEl.innerHTML = "";
          const items = this.pageItems;
          if (items.length === 0) {
            const empty = document.createElement("div");
            empty.className = "video-article-grid__empty";
            empty.textContent = this.searchTerm ? this.labels.noResults : this.labels.noItems;
            this.gridEl.appendChild(empty);
            return;
          }
          items.forEach((item) => this.gridEl.appendChild(this.createCard(item)));
        }
        createCard(item) {
          const article = document.createElement("article");
          article.className = "video-article-grid__card";
          const thumbLink = document.createElement("a");
          thumbLink.className = "video-article-grid__thumbnail";
          thumbLink.href = item.url || "#";
          thumbLink.setAttribute("aria-label", `Play: ${item.title}`);
          if (item.url && item.url !== "#") {
            thumbLink.target = "_blank";
            thumbLink.rel = "noopener noreferrer";
          }
          if (item.thumbnail) {
            if (this.config.playOnHover && this.isVideoSource(item.thumbnail)) {
              const video = document.createElement("video");
              video.className = "video-article-grid__thumb-video";
              video.src = item.thumbnail;
              video.muted = true;
              video.loop = true;
              video.playsInline = true;
              video.preload = "metadata";
              video.setAttribute("aria-hidden", "true");
              thumbLink.addEventListener("mouseenter", () => {
                video.play().catch(() => {
                });
              });
              thumbLink.addEventListener("mouseleave", () => {
                video.pause();
                video.currentTime = 0;
              });
              thumbLink.appendChild(video);
            } else {
              const img = document.createElement("img");
              img.src = item.thumbnail;
              img.alt = "";
              img.loading = "lazy";
              thumbLink.appendChild(img);
            }
          }
          const playBtn = document.createElement("div");
          playBtn.className = "video-article-grid__play-btn";
          playBtn.innerHTML = PLAY_SVG;
          thumbLink.appendChild(playBtn);
          article.appendChild(thumbLink);
          const body = document.createElement("div");
          body.className = "video-article-grid__body";
          if (item.badge) {
            const badge = document.createElement("span");
            badge.className = "video-article-grid__badge";
            badge.textContent = item.badge;
            body.appendChild(badge);
          }
          const titleEl = document.createElement("h3");
          titleEl.className = "video-article-grid__card-title";
          const titleLink = document.createElement("a");
          titleLink.href = item.url || "#";
          titleLink.textContent = item.title;
          if (item.url && item.url !== "#") {
            titleLink.target = "_blank";
            titleLink.rel = "noopener noreferrer";
          }
          titleEl.appendChild(titleLink);
          body.appendChild(titleEl);
          if (item.description) {
            const desc = document.createElement("p");
            desc.className = "video-article-grid__description";
            desc.textContent = item.description;
            body.appendChild(desc);
          }
          article.appendChild(body);
          return article;
        }
        /* -------------------- utils -------------------- */
        isVideoSource(src) {
          return /\.(mp4|webm|ogg)(\?|$)/i.test(src);
        }
        escapeHtml(str) {
          const el = document.createElement("span");
          el.textContent = str;
          return el.innerHTML;
        }
      };
      function initVideoArticleGrids() {
        const grids = document.querySelectorAll(".video-article-grid__grid[data-items]");
        grids.forEach((grid) => new VideoArticleGrid(grid));
      }
      if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initVideoArticleGrids);
      } else {
        initVideoArticleGrids();
      }
      window.VideoArticleGridComponent = { init: initVideoArticleGrids };
    }
  });
  require_video_article_grid();
})();

/**
 * Video Article Grid Component JavaScript
 * Handles searchable, sortable, paginated grid of article cards.
 * Reads data-items, data-config, and data-labels from the grid element.
 */
(function () {
    'use strict';

    var CONFIG_DEFAULTS = {
        cardsInSingleRow: 3,
        playOnHover: true,
        maxArticleInASinglePage: 9,
        defaultSortBy: 'publishedDate',
        defaultOrder: 'dsc'
    };

    var LABEL_DEFAULTS = {
        searchPlaceholder: 'Search tutorials\u2026',
        prevPage: 'Previous Page',
        nextPage: 'Next Page',
        pageIndicator: 'Page {currentPage} of {totalPages}',
        sortByLabel: 'Sort By',
        sortByPublishedDate: 'Published Date',
        sortByCreatedDate: 'Created Date',
        sortByTitle: 'Title',
        sortOrderLabel: 'Order',
        sortOrderAsc: 'ASC',
        sortOrderDsc: 'DSC',
        noResults: 'No tutorials match your search.',
        noItems: 'No tutorials available.'
    };

    var PLAY_SVG = '<svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M8 5v14l11-7z"/></svg>';
    var SEARCH_SVG = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>';
    var CHEVRON_LEFT_SVG = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>';
    var CHEVRON_RIGHT_SVG = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 18l6-6-6-6"/></svg>';

    function VideoArticleGrid(container) {
        this.container = container;
        this.allItems = this.parseItems();
        this.config = this.parseConfig();
        this.labels = this.parseLabels();
        this.sortField = this.config.defaultSortBy;
        this.sortOrder = this.config.defaultOrder;
        this.currentPage = 1;
        this.searchTerm = '';
        this.filteredItems = this.allItems.slice();
        this.applySortToFiltered();
        this.build();
    }

    /* -------------------- data parsing -------------------- */
    VideoArticleGrid.prototype.parseItems = function () {
        try {
            var raw = this.container.getAttribute('data-items') || '[]';
            return JSON.parse(raw);
        } catch (e) {
            console.warn('[VideoArticleGrid] Invalid data-items JSON');
            return [];
        }
    };

    VideoArticleGrid.prototype.parseConfig = function () {
        try {
            var raw = this.container.getAttribute('data-config') || '{}';
            var parsed = JSON.parse(raw);
            var merged = {};
            for (var key in CONFIG_DEFAULTS) { merged[key] = CONFIG_DEFAULTS[key]; }
            for (var key in parsed) { merged[key] = parsed[key]; }
            return merged;
        } catch (e) {
            console.warn('[VideoArticleGrid] Invalid data-config JSON');
            var fallback = {};
            for (var key in CONFIG_DEFAULTS) { fallback[key] = CONFIG_DEFAULTS[key]; }
            return fallback;
        }
    };

    VideoArticleGrid.prototype.parseLabels = function () {
        try {
            var raw = this.container.getAttribute('data-labels') || '{}';
            var parsed = JSON.parse(raw);
            var merged = {};
            for (var key in LABEL_DEFAULTS) { merged[key] = LABEL_DEFAULTS[key]; }
            for (var key in parsed) { merged[key] = parsed[key]; }
            return merged;
        } catch (e) {
            console.warn('[VideoArticleGrid] Invalid data-labels JSON');
            var fallback = {};
            for (var key in LABEL_DEFAULTS) { fallback[key] = LABEL_DEFAULTS[key]; }
            return fallback;
        }
    };

    /* -------------------- computed helpers -------------------- */
    VideoArticleGrid.prototype.getTotalPages = function () {
        return Math.max(1, Math.ceil(this.filteredItems.length / this.config.maxArticleInASinglePage));
    };

    VideoArticleGrid.prototype.getPageItems = function () {
        var start = (this.currentPage - 1) * this.config.maxArticleInASinglePage;
        return this.filteredItems.slice(start, start + this.config.maxArticleInASinglePage);
    };

    /* -------------------- DOM scaffold -------------------- */
    VideoArticleGrid.prototype.build = function () {
        var self = this;
        this.container.style.setProperty('--vag-cols', String(this.config.cardsInSingleRow));

        // Toolbar
        var toolbar = document.createElement('div');
        toolbar.className = 'video-article-grid__toolbar';

        var searchWrap = document.createElement('div');
        searchWrap.className = 'video-article-grid__search';
        var searchIcon = document.createElement('span');
        searchIcon.className = 'video-article-grid__search-icon';
        searchIcon.innerHTML = SEARCH_SVG;
        this.searchInput = document.createElement('input');
        this.searchInput.type = 'text';
        this.searchInput.className = 'video-article-grid__search-input';
        this.searchInput.placeholder = this.labels.searchPlaceholder;
        this.searchInput.setAttribute('aria-label', this.labels.searchPlaceholder);
        this.searchInput.addEventListener('input', function () { self.onSearch(); });
        searchWrap.appendChild(searchIcon);
        searchWrap.appendChild(this.searchInput);
        toolbar.appendChild(searchWrap);

        // Sort-by select
        this.sortBySelect = document.createElement('select');
        this.sortBySelect.className = 'video-article-grid__sort-select';
        this.sortBySelect.setAttribute('aria-label', this.labels.sortByLabel);
        var sortByOptions = [
            { value: 'publishedDate', text: this.labels.sortByPublishedDate },
            { value: 'createdDate', text: this.labels.sortByCreatedDate },
            { value: 'title', text: this.labels.sortByTitle }
        ];
        sortByOptions.forEach(function (opt) {
            var option = document.createElement('option');
            option.value = opt.value;
            option.textContent = opt.text;
            self.sortBySelect.appendChild(option);
        });
        this.sortBySelect.value = this.sortField;
        this.sortBySelect.addEventListener('change', function () { self.onSortChange(); });
        toolbar.appendChild(this.sortBySelect);

        // Sort-order select
        this.sortOrderSelect = document.createElement('select');
        this.sortOrderSelect.className = 'video-article-grid__sort-select';
        this.sortOrderSelect.setAttribute('aria-label', this.labels.sortOrderLabel);
        var sortOrderOptions = [
            { value: 'asc', text: this.labels.sortOrderAsc },
            { value: 'dsc', text: this.labels.sortOrderDsc }
        ];
        sortOrderOptions.forEach(function (opt) {
            var option = document.createElement('option');
            option.value = opt.value;
            option.textContent = opt.text;
            self.sortOrderSelect.appendChild(option);
        });
        this.sortOrderSelect.value = this.sortOrder;
        this.sortOrderSelect.addEventListener('change', function () { self.onSortChange(); });
        toolbar.appendChild(this.sortOrderSelect);

        this.container.appendChild(toolbar);

        // Grid
        this.gridEl = document.createElement('div');
        this.gridEl.className = 'video-article-grid__cards';
        this.container.appendChild(this.gridEl);

        // Pagination
        this.paginationEl = document.createElement('nav');
        this.paginationEl.className = 'video-article-grid__pagination';
        this.paginationEl.setAttribute('aria-label', 'Pagination');

        this.prevBtn = document.createElement('button');
        this.prevBtn.className = 'video-article-grid__page-btn video-article-grid__page-btn--prev';
        this.prevBtn.innerHTML = CHEVRON_LEFT_SVG + '<span>' + this.escapeHtml(this.labels.prevPage) + '</span>';
        this.prevBtn.addEventListener('click', function () { self.goToPage(self.currentPage - 1); });

        this.pageIndicator = document.createElement('span');
        this.pageIndicator.className = 'video-article-grid__page-indicator';

        this.nextBtn = document.createElement('button');
        this.nextBtn.className = 'video-article-grid__page-btn video-article-grid__page-btn--next';
        this.nextBtn.innerHTML = '<span>' + this.escapeHtml(this.labels.nextPage) + '</span>' + CHEVRON_RIGHT_SVG;
        this.nextBtn.addEventListener('click', function () { self.goToPage(self.currentPage + 1); });

        this.paginationEl.appendChild(this.prevBtn);
        this.paginationEl.appendChild(this.pageIndicator);
        this.paginationEl.appendChild(this.nextBtn);
        this.container.appendChild(this.paginationEl);

        this.renderCards();
        this.renderPagination();
    };

    /* -------------------- search -------------------- */
    VideoArticleGrid.prototype.onSearch = function () {
        this.searchTerm = this.searchInput.value.trim().toLowerCase();
        this.applyFilterAndSort();
    };

    /* -------------------- sorting -------------------- */
    VideoArticleGrid.prototype.onSortChange = function () {
        this.sortField = this.sortBySelect.value;
        this.sortOrder = this.sortOrderSelect.value;
        this.applyFilterAndSort();
    };

    VideoArticleGrid.prototype.applySortToFiltered = function () {
        var sortField = this.sortField;
        var dir = this.sortOrder === 'asc' ? 1 : -1;
        this.filteredItems.sort(function (a, b) {
            var cmp = 0;
            if (sortField === 'title') {
                cmp = (a.title || '').localeCompare(b.title || '');
            } else {
                var da = new Date(a[sortField]).getTime() || 0;
                var db = new Date(b[sortField]).getTime() || 0;
                cmp = da - db;
            }
            return cmp * dir;
        });
    };

    VideoArticleGrid.prototype.applyFilterAndSort = function () {
        var self = this;
        if (this.searchTerm === '') {
            this.filteredItems = this.allItems.slice();
        } else {
            this.filteredItems = this.allItems.filter(function (item) {
                var haystack = [item.title, item.description || '', item.badge || ''].join(' ').toLowerCase();
                return haystack.indexOf(self.searchTerm) !== -1;
            });
        }
        this.applySortToFiltered();
        this.currentPage = 1;
        this.renderCards();
        this.renderPagination();
    };

    /* -------------------- pagination -------------------- */
    VideoArticleGrid.prototype.goToPage = function (page) {
        if (page < 1 || page > this.getTotalPages()) return;
        this.currentPage = page;
        this.renderCards();
        this.renderPagination();
        this.gridEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
    };

    VideoArticleGrid.prototype.renderPagination = function () {
        var totalPages = this.getTotalPages();
        var text = this.labels.pageIndicator
            .replace('{currentPage}', String(this.currentPage))
            .replace('{totalPages}', String(totalPages));
        this.pageIndicator.textContent = text;
        this.prevBtn.disabled = this.currentPage <= 1;
        this.nextBtn.disabled = this.currentPage >= totalPages;
        this.paginationEl.style.display = totalPages <= 1 ? 'none' : '';
    };

    /* -------------------- card rendering -------------------- */
    VideoArticleGrid.prototype.renderCards = function () {
        this.gridEl.innerHTML = '';
        var items = this.getPageItems();
        if (items.length === 0) {
            var empty = document.createElement('div');
            empty.className = 'video-article-grid__empty';
            empty.textContent = this.searchTerm ? this.labels.noResults : this.labels.noItems;
            this.gridEl.appendChild(empty);
            return;
        }
        var self = this;
        items.forEach(function (item) {
            self.gridEl.appendChild(self.createCard(item));
        });
    };

    VideoArticleGrid.prototype.createCard = function (item) {
        var self = this;
        var article = document.createElement('article');
        article.className = 'video-article-grid__card';

        var thumbLink = document.createElement('a');
        thumbLink.className = 'video-article-grid__thumbnail';
        thumbLink.href = item.url || '#';
        thumbLink.setAttribute('aria-label', 'Play: ' + item.title);
        if (item.url && item.url !== '#') {
            thumbLink.target = '_blank';
            thumbLink.rel = 'noopener noreferrer';
        }

        if (item.videoSrc) {
            // Video asset is available — create a video element for the player
            var video = document.createElement('video');
            video.className = 'video-article-grid__thumb-video';
            video.src = item.videoSrc;
            video.muted = true;
            video.loop = true;
            video.playsInline = true;
            video.preload = 'metadata';
            video.setAttribute('aria-hidden', 'true');

            if (item.thumbnail) {
                // Page image is configured — use it as video poster
                video.poster = item.thumbnail;
            }
            // If no thumbnail, the browser shows the first frame via preload="metadata"

            if (this.config.playOnHover) {
                thumbLink.addEventListener('mouseenter', function () {
                    video.play().catch(function () {});
                });
                thumbLink.addEventListener('mouseleave', function () {
                    video.pause();
                    video.currentTime = 0;
                });
            }

            thumbLink.appendChild(video);
        } else if (item.thumbnail) {
            // No video source, only a static thumbnail image
            var img = document.createElement('img');
            img.src = item.thumbnail;
            img.alt = '';
            img.loading = 'lazy';
            thumbLink.appendChild(img);
        }

        var playBtn = document.createElement('div');
        playBtn.className = 'video-article-grid__play-btn';
        playBtn.innerHTML = PLAY_SVG;
        thumbLink.appendChild(playBtn);
        article.appendChild(thumbLink);

        var body = document.createElement('div');
        body.className = 'video-article-grid__body';

        if (item.badge) {
            var badge = document.createElement('span');
            badge.className = 'video-article-grid__badge';
            badge.textContent = item.badge;
            body.appendChild(badge);
        }

        var titleEl = document.createElement('h3');
        titleEl.className = 'video-article-grid__card-title';
        var titleLink = document.createElement('a');
        titleLink.href = item.url || '#';
        titleLink.textContent = item.title;
        if (item.url && item.url !== '#') {
            titleLink.target = '_blank';
            titleLink.rel = 'noopener noreferrer';
        }
        titleEl.appendChild(titleLink);
        body.appendChild(titleEl);

        if (item.description) {
            var desc = document.createElement('p');
            desc.className = 'video-article-grid__description';
            desc.textContent = item.description;
            body.appendChild(desc);
        }

        article.appendChild(body);
        return article;
    };

    /* -------------------- utils -------------------- */
    VideoArticleGrid.prototype.isVideoSource = function (src) {
        return /\.(mp4|webm|ogg)(\?|$)/i.test(src);
    };

    VideoArticleGrid.prototype.escapeHtml = function (str) {
        var el = document.createElement('span');
        el.textContent = str;
        return el.innerHTML;
    };

    /* -------------------- init -------------------- */
    function initVideoArticleGrids() {
        var grids = document.querySelectorAll('.video-article-grid__grid[data-items]');
        grids.forEach(function (grid) {
            new VideoArticleGrid(grid);
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initVideoArticleGrids);
    } else {
        initVideoArticleGrids();
    }

})();

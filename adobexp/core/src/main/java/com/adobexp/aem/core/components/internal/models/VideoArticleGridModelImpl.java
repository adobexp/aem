/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 AdobeXP
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobexp.aem.core.components.internal.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.models.VideoArticleGridModel;

/**
 * Sling Model implementation for the Video Article Grid component.
 * Reads articles from children of a configured content path and
 * builds JSON strings for data-items, data-config, and data-labels attributes.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = VideoArticleGridModel.class,
    resourceType = VideoArticleGridModelImpl.RESOURCE_TYPE
)
public class VideoArticleGridModelImpl implements VideoArticleGridModel {

    private static final Logger LOG = LoggerFactory.getLogger(VideoArticleGridModelImpl.class);

    protected static final String RESOURCE_TYPE = "adobexp/components/content/video-article-grid";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    // ── Content fields ──────────────────────────────────────────────────────

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String componentTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String componentSubTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String articlesPath;

    // ── Configuration fields (data-config) ──────────────────────────────────

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 3)
    private int cardsInSingleRow;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = true)
    private boolean playOnHover;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 9)
    private int maxArticleInASinglePage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "publishedDate")
    private String defaultSortBy;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "dsc")
    private String defaultOrder;

    // ── Label fields (data-labels) ──────────────────────────────────────────

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Search tutorials\u2026")
    private String searchPlaceholder;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Previous Page")
    private String prevPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Next Page")
    private String nextPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Page {currentPage} of {totalPages}")
    private String pageIndicator;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Sort By")
    private String sortByLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Published Date")
    private String sortByPublishedDate;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Created Date")
    private String sortByCreatedDate;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Title")
    private String sortByTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Order")
    private String sortOrderLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "ASC")
    private String sortOrderAsc;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "DSC")
    private String sortOrderDsc;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No tutorials match your search.")
    private String noResults;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No tutorials available.")
    private String noItems;

    // ── Style fields ────────────────────────────────────────────────────────

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean withBackground;

    // ── Computed fields ─────────────────────────────────────────────────────

    private String dataItemsJson;
    private String dataConfigJson;
    private String dataLabelsJson;
    private boolean articlesAvailable;

    @PostConstruct
    protected void init() {
        List<ArticleItem> articles = parseArticles();
        articlesAvailable = !articles.isEmpty();
        dataItemsJson = buildDataItemsJson(articles);
        dataConfigJson = buildDataConfigJson();
        dataLabelsJson = buildDataLabelsJson();
    }

    // ── Article parsing ─────────────────────────────────────────────────────

    private ResourceResolver getResourceResolver() {
        Resource resource = getResource();
        if (resource != null) {
            return resource.getResourceResolver();
        }
        return null;
    }

    private Resource getResource() {
        if (currentResource != null) {
            return currentResource;
        }
        if (request != null) {
            return request.getResource();
        }
        return null;
    }

    /**
     * Parse article items from children of the configured articles path.
     * Supports both page nodes (with jcr:content) and flat resource nodes.
     */
    private List<ArticleItem> parseArticles() {
        if (StringUtils.isBlank(articlesPath)) {
            return Collections.emptyList();
        }

        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            LOG.warn("[VideoArticleGrid] ResourceResolver not available");
            return Collections.emptyList();
        }

        Resource articlesResource = resolver.getResource(articlesPath);
        if (articlesResource == null) {
            LOG.warn("[VideoArticleGrid] Articles path not found: {}", articlesPath);
            return Collections.emptyList();
        }

        List<ArticleItem> items = new ArrayList<>();
        for (Resource child : articlesResource.getChildren()) {
            String name = child.getName();
            // Skip JCR system nodes
            if (name.startsWith("jcr:") || name.startsWith("rep:") || name.startsWith("cq:")) {
                continue;
            }

            ArticleItem item = parseArticleItem(child);
            if (item != null) {
                items.add(item);
            }
        }

        return items;
    }

    /**
     * Parse a single article item from a resource.
     * If the resource is a page (has jcr:content), reads properties from jcr:content.
     *
     * Title priority: pageTitle (Page Title) -> jcr:title (Title) -> title
     * Thumbnail: image/fileReference from page properties (page image)
     * Video source: videoAssetPath from page properties (Video tab)
     */
    private ArticleItem parseArticleItem(Resource childResource) {
        // Check if it's a page node (has jcr:content child)
        Resource contentResource = childResource.getChild("jcr:content");
        ValueMap props = (contentResource != null) ? contentResource.getValueMap() : childResource.getValueMap();

        // Title: pageTitle first (Page Title from Page Properties),
        // then jcr:title (Title from Page Properties), then title
        String title = props.get("pageTitle", String.class);
        if (StringUtils.isBlank(title)) {
            title = props.get("jcr:title", String.class);
        }
        if (StringUtils.isBlank(title)) {
            title = props.get("title", String.class);
        }
        // Skip items without a title
        if (StringUtils.isBlank(title)) {
            return null;
        }

        // Description: try jcr:description first, then description
        String description = props.get("jcr:description", String.class);
        if (StringUtils.isBlank(description)) {
            description = props.get("description", String.class);
        }

        // URL: use explicit url property, fallback to page path + .html
        String url = props.get("url", String.class);
        if (StringUtils.isBlank(url)) {
            url = childResource.getPath() + ".html";
        }

        // Badge
        String badge = props.get("badge", String.class);

        // Published date
        Calendar publishedDateCal = props.get("publishedDate", Calendar.class);
        String publishedDate = formatDate(publishedDateCal);

        // Created date: try createdDate first, then jcr:created
        Calendar createdDateCal = props.get("createdDate", Calendar.class);
        if (createdDateCal == null) {
            createdDateCal = props.get("jcr:created", Calendar.class);
        }
        String createdDate = formatDate(createdDateCal);

        // Thumbnail: read from page image (image/fileReference under jcr:content)
        String thumbnail = null;
        if (contentResource != null) {
            Resource imageResource = contentResource.getChild("image");
            if (imageResource != null) {
                thumbnail = imageResource.getValueMap().get("fileReference", String.class);
            }
        }
        // Fallback: try direct thumbnail property (for flat nodes)
        if (StringUtils.isBlank(thumbnail)) {
            thumbnail = props.get("thumbnail", String.class);
        }

        // Video source: videoAssetPath from page properties (Video tab in page dialog)
        String videoSrc = props.get("videoAssetPath", String.class);

        return new ArticleItem(url, badge, title, description, publishedDate, createdDate, thumbnail, videoSrc);
    }

    // ── JSON builders ───────────────────────────────────────────────────────

    private String buildDataItemsJson(List<ArticleItem> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            ArticleItem item = items.get(i);
            sb.append("{");
            appendJsonField(sb, "url", item.url, true);
            appendJsonField(sb, "badge", item.badge, true);
            appendJsonField(sb, "title", item.title, true);
            appendJsonField(sb, "description", item.description, true);
            appendJsonField(sb, "publishedDate", item.publishedDate, true);
            appendJsonField(sb, "createdDate", item.createdDate, true);
            appendJsonField(sb, "thumbnail", item.thumbnail, true);
            appendJsonField(sb, "videoSrc", item.videoSrc, false);
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String buildDataConfigJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"cardsInSingleRow\":").append(cardsInSingleRow).append(",");
        sb.append("\"playOnHover\":").append(playOnHover).append(",");
        sb.append("\"maxArticleInASinglePage\":").append(maxArticleInASinglePage).append(",");
        appendJsonField(sb, "defaultSortBy", defaultSortBy, true);
        appendJsonField(sb, "defaultOrder", defaultOrder, false);
        sb.append("}");
        return sb.toString();
    }

    private String buildDataLabelsJson() {
        StringBuilder sb = new StringBuilder("{");
        appendJsonField(sb, "searchPlaceholder", searchPlaceholder, true);
        appendJsonField(sb, "prevPage", prevPage, true);
        appendJsonField(sb, "nextPage", nextPage, true);
        appendJsonField(sb, "pageIndicator", pageIndicator, true);
        appendJsonField(sb, "sortByLabel", sortByLabel, true);
        appendJsonField(sb, "sortByPublishedDate", sortByPublishedDate, true);
        appendJsonField(sb, "sortByCreatedDate", sortByCreatedDate, true);
        appendJsonField(sb, "sortByTitle", sortByTitle, true);
        appendJsonField(sb, "sortOrderLabel", sortOrderLabel, true);
        appendJsonField(sb, "sortOrderAsc", sortOrderAsc, true);
        appendJsonField(sb, "sortOrderDsc", sortOrderDsc, true);
        appendJsonField(sb, "noResults", noResults, true);
        appendJsonField(sb, "noItems", noItems, false);
        sb.append("}");
        return sb.toString();
    }

    // ── JSON helpers ────────────────────────────────────────────────────────

    private void appendJsonField(StringBuilder sb, String key, String value, boolean hasNext) {
        sb.append("\"").append(key).append("\":");
        if (value != null) {
            sb.append("\"").append(escapeJson(value)).append("\"");
        } else {
            sb.append("null");
        }
        if (hasNext) {
            sb.append(",");
        }
    }

    /**
     * Escape special characters for JSON string values.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Format a Calendar to ISO 8601 UTC string.
     */
    private String formatDate(Calendar cal) {
        if (cal == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(cal.getTime());
    }

    // ── Getter implementations ──────────────────────────────────────────────

    @Override
    public String getComponentTitle() {
        return componentTitle;
    }

    @Override
    public String getComponentSubTitle() {
        return componentSubTitle;
    }

    @Override
    public String getDataItemsJson() {
        return dataItemsJson;
    }

    @Override
    public String getDataConfigJson() {
        return dataConfigJson;
    }

    @Override
    public String getDataLabelsJson() {
        return dataLabelsJson;
    }

    @Override
    public boolean hasArticles() {
        return articlesAvailable;
    }

    @Override
    public boolean isWithBackground() {
        return withBackground;
    }

    // ── Inner class for article data ────────────────────────────────────────

    /**
     * Simple data holder for an article item.
     */
    private static class ArticleItem {
        final String url;
        final String badge;
        final String title;
        final String description;
        final String publishedDate;
        final String createdDate;
        final String thumbnail;
        final String videoSrc;

        ArticleItem(String url, String badge, String title, String description,
                    String publishedDate, String createdDate, String thumbnail, String videoSrc) {
            this.url = url;
            this.badge = badge;
            this.title = title;
            this.description = description;
            this.publishedDate = publishedDate;
            this.createdDate = createdDate;
            this.thumbnail = thumbnail;
            this.videoSrc = videoSrc;
        }
    }
}

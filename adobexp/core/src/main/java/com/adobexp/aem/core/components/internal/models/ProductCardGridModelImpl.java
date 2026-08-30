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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.internal.LocalizationUtils;
import com.adobexp.aem.core.components.models.ProductCardGridModel;
import com.day.cq.wcm.api.Page;

/**
 * Sling Model implementation for the Product Card Grid component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ProductCardGridModel.class,
    resourceType = ProductCardGridModelImpl.RESOURCE_TYPE
)
public class ProductCardGridModelImpl implements ProductCardGridModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/product-card-grid";

    private static final String CF_DATA_PATH = "jcr:content/data/master";

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Page currentPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String productsFolder;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String categoryFilter;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String detailPageParent;

    private String resolvedProductsFolder;
    private List<ProductCardItem> items = Collections.emptyList();

    @PostConstruct
    protected void init() {
        resolvedProductsFolder = LocalizationUtils.localizeDamLanguageCopyPath(
            productsFolder, currentPage, resourceResolver);
        if (StringUtils.isBlank(resolvedProductsFolder)) {
            return;
        }
        Resource folder = resourceResolver.getResource(resolvedProductsFolder);
        if (folder == null) {
            return;
        }
        String resolvedDetailParent = resolveDetailPageParent();
        List<ProductCardItem> parsedItems = new ArrayList<>();
        for (Resource child : folder.getChildren()) {
            ProductCardItem item = toProductCardItem(child, resolvedDetailParent);
            if (item != null) {
                parsedItems.add(item);
            }
        }
        parsedItems.sort(Comparator.comparingInt(this::sortOrderForItem));
        items = parsedItems;
    }

    private String resolveDetailPageParent() {
        if (StringUtils.isNotBlank(detailPageParent)) {
            return StringUtils.removeEnd(detailPageParent, "/");
        }
        if (currentPage != null && currentPage.getParent() != null) {
            return currentPage.getParent().getPath();
        }
        return StringUtils.EMPTY;
    }

    private ProductCardItem toProductCardItem(Resource cfResource, String resolvedDetailParent) {
        Resource dataResource = cfResource.getChild(CF_DATA_PATH);
        if (dataResource == null) {
            return null;
        }
        ValueMap data = dataResource.getValueMap();
        Boolean isFeatured = data.get("isFeatured", Boolean.class);
        if (isFeatured == null) {
            Boolean isCurrentLineup = data.get("isCurrentLineup", Boolean.class);
            if (isCurrentLineup != null && !isCurrentLineup) {
                return null;
            }
        } else if (!isFeatured) {
            return null;
        }
        String category = data.get("category", String.class);
        if (StringUtils.isNotBlank(categoryFilter) && !StringUtils.equals(categoryFilter, category)) {
            return null;
        }
        return new ProductCardItemImpl(
            cfResource.getName(),
            data.get("displayTitle", String.class),
            data.get("tagline", String.class),
            firstNonBlank(data.get("primaryImage", String.class), data.get("jellyImage", String.class)),
            firstNonBlank(data.get("year", String.class), data.get("modelYear", String.class)),
            category,
            buildDetailLink(resolvedDetailParent, cfResource.getName()),
            data.get("sortOrder", String.class)
        );
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (StringUtils.isNotBlank(primary)) {
            return primary;
        }
        return fallback;
    }

    private String buildDetailLink(String parentPath, String nodeName) {
        if (StringUtils.isAnyBlank(parentPath, nodeName)) {
            return StringUtils.EMPTY;
        }
        return parentPath + "/" + nodeName;
    }

    private int sortOrderForItem(ProductCardItem item) {
        if (item instanceof ProductCardItemImpl) {
            return ((ProductCardItemImpl) item).getSortOrderValue();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public String getProductsFolder() {
        return resolvedProductsFolder;
    }

    @Override
    public String getCategoryFilter() {
        return categoryFilter;
    }

    @Override
    public String getDetailPageParent() {
        return resolveDetailPageParent();
    }

    @Override
    public List<ProductCardItem> getItems() {
        return items;
    }

    @Override
    public boolean hasItems() {
        return !items.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return hasItems();
    }

    private static final class ProductCardItemImpl implements ProductCardItem {

        private final String name;
        private final String displayTitle;
        private final String tagline;
        private final String primaryImage;
        private final String year;
        private final String category;
        private final String detailLink;
        private final int sortOrderValue;

        private ProductCardItemImpl(String name, String displayTitle, String tagline, String primaryImage,
                                    String year, String category, String detailLink, String sortOrder) {
            this.name = name;
            this.displayTitle = displayTitle;
            this.tagline = tagline;
            this.primaryImage = primaryImage;
            this.year = year;
            this.category = category;
            this.detailLink = detailLink;
            this.sortOrderValue = NumberUtils.toInt(sortOrder, Integer.MAX_VALUE);
        }

        private int getSortOrderValue() {
            return sortOrderValue;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDisplayTitle() {
            return displayTitle;
        }

        @Override
        public String getTagline() {
            return tagline;
        }

        @Override
        public String getPrimaryImage() {
            return primaryImage;
        }

        @Override
        public String getYear() {
            return year;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getDetailLink() {
            return detailLink;
        }
    }
}

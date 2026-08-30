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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import com.adobexp.aem.core.components.models.ProductDetailModel;
import com.day.cq.wcm.api.Page;

/**
 * Sling Model implementation for the Product Detail component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ProductDetailModel.class,
    resourceType = ProductDetailModelImpl.RESOURCE_TYPE
)
public class ProductDetailModelImpl implements ProductDetailModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/product-detail";

    private static final String CF_DATA_SUFFIX = "/jcr:content/data/master";

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Page currentPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentsRoot;

    private String resolvedFragmentPath;
    private ValueMap fragmentData;

    @PostConstruct
    protected void init() {
        resolvedFragmentPath = resolveFragmentPath();
        if (StringUtils.isNotBlank(resolvedFragmentPath)) {
            Resource dataResource = resourceResolver.getResource(resolvedFragmentPath + CF_DATA_SUFFIX);
            if (dataResource != null) {
                fragmentData = dataResource.getValueMap();
            }
        }
    }

    private String resolveFragmentPath() {
        String authored = fragmentPath;
        if (StringUtils.isBlank(authored) && StringUtils.isNotBlank(fragmentsRoot) && currentPage != null) {
            authored = StringUtils.removeEnd(fragmentsRoot, "/") + "/" + currentPage.getName();
        }
        String localized = LocalizationUtils.localizeDamLanguageCopyPath(authored, currentPage, resourceResolver);
        if (StringUtils.isNotBlank(localized) && resourceResolver.getResource(localized) != null) {
            return localized;
        }
        if (StringUtils.isNotBlank(authored) && resourceResolver.getResource(authored) != null) {
            return authored;
        }
        return null;
    }

    private String getFragmentValue(String propertyName) {
        return fragmentData != null ? fragmentData.get(propertyName, String.class) : null;
    }

    private String getFragmentValueWithFallback(String primary, String fallback) {
        String value = getFragmentValue(primary);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return getFragmentValue(fallback);
    }

    @Override
    public String getFragmentPath() {
        return resolvedFragmentPath;
    }

    @Override
    public String getProductName() {
        return getFragmentValueWithFallback("productName", "modelName");
    }

    @Override
    public String getProductSlug() {
        return getFragmentValueWithFallback("productSlug", "modelSlug");
    }

    @Override
    public String getDisplayTitle() {
        return getFragmentValue("displayTitle");
    }

    @Override
    public String getTagline() {
        return getFragmentValue("tagline");
    }

    @Override
    public String getYear() {
        return getFragmentValueWithFallback("year", "modelYear");
    }

    @Override
    public String getCategory() {
        return getFragmentValue("category");
    }

    @Override
    public String getShortBlurb() {
        return getFragmentValue("shortBlurb");
    }

    @Override
    public String getLongDescription() {
        return getFragmentValue("longDescription");
    }

    @Override
    public String getSeatingCapacity() {
        return getFragmentValue("seatingCapacity");
    }

    @Override
    public String getPower() {
        return getFragmentValueWithFallback("power", "horsepower");
    }

    @Override
    public String getTorque() {
        return getFragmentValue("torque");
    }

    @Override
    public String getTransmission() {
        return getFragmentValue("transmission");
    }

    @Override
    public String getEfficiency() {
        return getFragmentValueWithFallback("efficiency", "fuelOrRange");
    }

    @Override
    public String getCapacity() {
        return getFragmentValueWithFallback("capacity", "cargoVolume");
    }

    @Override
    public String getPrimaryImage() {
        return getFragmentValueWithFallback("primaryImage", "jellyImage");
    }

    @Override
    public String getHeroImage() {
        return getFragmentValue("heroImage");
    }

    @Override
    public String getPrimaryCtaLabel() {
        return getFragmentValue("primaryCtaLabel");
    }

    @Override
    public String getPrimaryCtaLink() {
        return getFragmentValue("primaryCtaLink");
    }

    @Override
    public String getSortOrder() {
        return getFragmentValue("sortOrder");
    }

    @Override
    public boolean isFeatured() {
        if (fragmentData == null) {
            return false;
        }
        Boolean featured = fragmentData.get("isFeatured", Boolean.class);
        if (featured != null) {
            return featured;
        }
        Boolean currentLineup = fragmentData.get("isCurrentLineup", Boolean.class);
        return currentLineup == null || currentLineup;
    }

    @Override
    public boolean hasContent() {
        return fragmentData != null
            && (StringUtils.isNotBlank(getDisplayTitle())
                || StringUtils.isNotBlank(getProductName())
                || StringUtils.isNotBlank(getShortBlurb()));
    }
}

/*
 *  Copyright 2024 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobexp.aem.core.components.internal.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.QuoteModel;

/**
 * Sling Model implementation for the Quote component.
 * Reads component configuration for quote text and author information.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = QuoteModel.class,
    resourceType = QuoteModelImpl.RESOURCE_TYPE
)
public class QuoteModelImpl implements QuoteModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/quote";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String quoteTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String quotePrimaryText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String quoteMutedText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String quoteAvatar;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String authorName;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String authorOrganisation;

    @Override
    public String getQuoteTitle() {
        return quoteTitle;
    }

    @Override
    public String getQuotePrimaryText() {
        return quotePrimaryText;
    }

    @Override
    public String getQuoteMutedText() {
        return quoteMutedText;
    }

    @Override
    public String getQuoteAvatar() {
        return quoteAvatar;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String getAuthorOrganisation() {
        return authorOrganisation;
    }
}


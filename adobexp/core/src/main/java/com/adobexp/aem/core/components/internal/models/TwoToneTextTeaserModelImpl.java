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
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.TwoToneTextTeaserModel;

/**
 * Sling Model implementation for the Two Tone Text Teaser component.
 * Reads component configuration for primary/secondary text and CTA.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = TwoToneTextTeaserModel.class,
    resourceType = TwoToneTextTeaserModelImpl.RESOURCE_TYPE
)
public class TwoToneTextTeaserModelImpl implements TwoToneTextTeaserModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/two-tone-text-teaser";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String secondaryText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean ctaLinkExternal;

    @Override
    public String getPrimaryText() {
        return primaryText;
    }

    @Override
    public String getSecondaryText() {
        return secondaryText;
    }

    @Override
    public String getCtaText() {
        return ctaText;
    }

    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    @Override
    public boolean isCtaLinkExternal() {
        return ctaLinkExternal;
    }
}


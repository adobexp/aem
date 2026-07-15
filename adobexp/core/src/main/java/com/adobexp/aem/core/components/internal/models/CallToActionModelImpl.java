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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.CallToActionModel;

/**
 * Sling Model implementation for the Call To Action component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = CallToActionModel.class,
    resourceType = CallToActionModelImpl.RESOURCE_TYPE
)
public class CallToActionModelImpl implements CallToActionModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/call-to-action";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryCtaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String primaryCtaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "false")
    private String primaryCtaExternal;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String secondaryCtaText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String secondaryCtaLink;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "false")
    private String secondaryCtaExternal;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public String getPrimaryCtaText() {
        return primaryCtaText;
    }

    @Override
    public String getPrimaryCtaLink() {
        return primaryCtaLink;
    }

    @Override
    public boolean isPrimaryCtaExternal() {
        return "true".equals(primaryCtaExternal);
    }

    @Override
    public boolean hasPrimaryCta() {
        return StringUtils.isNotBlank(primaryCtaText);
    }

    @Override
    public String getSecondaryCtaText() {
        return secondaryCtaText;
    }

    @Override
    public String getSecondaryCtaLink() {
        return secondaryCtaLink;
    }

    @Override
    public boolean isSecondaryCtaExternal() {
        return "true".equals(secondaryCtaExternal);
    }

    @Override
    public boolean hasSecondaryCta() {
        return StringUtils.isNotBlank(secondaryCtaText);
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || StringUtils.isNotBlank(subtitle)
            || hasPrimaryCta()
            || hasSecondaryCta();
    }
}

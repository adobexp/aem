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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobexp.aem.core.components.models.ContainerItem;
import com.adobexp.aem.core.components.models.LayoutContainer;
import com.adobexp.aem.core.components.util.ComponentUtils;

/**
 * Layout container model implementation.
 */
@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = LayoutContainer.class,
    resourceType = {
        LayoutContainerImpl.RESOURCE_TYPE_V1,
        LayoutContainerImpl.RESOURCE_TYPE_PROXY
    }
)
public class LayoutContainerImpl implements LayoutContainer {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE_V1 = "adobexp/components/container/v1/container";
    protected static final String RESOURCE_TYPE_PROXY = "adobexp/components/container";
    protected static final String GHOST_COMPONENT_RESOURCE_TYPE = "wcm/msm/components/ghost";

    @Self
    private SlingHttpServletRequest request;

    /**
     * The current resource.
     */
    @SlingObject
    protected Resource resource;

    /**
     * The layout type.
     */
    private LayoutType layout;

    private List<ContainerItem> items;

    /**
     * The accessibility label.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String accessibilityLabel;

    /**
     * The role attribute.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String roleAttribute;

    /**
     * The id attribute.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String id;

    /**
     * The current style for this component.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Nullable
    protected Style currentStyle;

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        this.layout = Optional.ofNullable(
            Optional.ofNullable(resource.getValueMap().get(LayoutContainer.PN_LAYOUT, String.class))
                .orElseGet(() -> Optional.ofNullable(currentStyle)
                    .map(style -> style.get(LayoutContainer.PN_LAYOUT, String.class))
                    .orElse(null)
                ))
            .map(LayoutType::getLayoutType)
            .orElse(LayoutType.RESPONSIVE_GRID);
    }

    @Override
    public @NotNull LayoutType getLayout() {
        return layout;
    }

    @Override
    @NotNull
    public List<ContainerItem> getChildren() {
        if (items == null) {
            WCMMode wcmMode = WCMMode.fromRequest(request);
            boolean showGhostComponent = wcmMode == WCMMode.EDIT;
            items = ComponentUtils.getChildComponents(resource, request).stream()
                .filter(item -> showGhostComponent || !GHOST_COMPONENT_RESOURCE_TYPE.equals(item.getResourceType()))
                .map(ContainerItemImpl::new)
                .collect(Collectors.toList());
        }
        return Collections.unmodifiableList(items);
    }

    @Override
    @Nullable
    public String getAccessibilityLabel() {
        return accessibilityLabel;
    }

    @Override
    @Nullable
    public String getRoleAttribute() {
        return roleAttribute;
    }

    @Override
    @Nullable
    public String getId() {
        return id;
    }

    private static final class ContainerItemImpl implements ContainerItem {
        private final Resource resource;

        private ContainerItemImpl(@NotNull Resource resource) {
            this.resource = resource;
        }

        @Override
        public Resource getResource() {
            return resource;
        }

        @Override
        public @NotNull String getName() {
            return resource.getName();
        }

        @Override
        public String getPath() {
            return resource.getPath();
        }
    }
}

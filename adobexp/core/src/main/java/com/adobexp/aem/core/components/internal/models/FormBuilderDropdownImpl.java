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
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobexp.aem.core.components.models.FormBuilderDropdown;

/**
 * Sling Model implementation for the Form Builder Dropdown component.
 * Reads multifield configurations for dropdown options.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = FormBuilderDropdown.class,
    resourceType = FormBuilderDropdownImpl.RESOURCE_TYPE
)
public class FormBuilderDropdownImpl implements FormBuilderDropdown {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/form-builder/components/dropdown";
    
    private static final String OPTIONS_NODE = "dropdownOptions";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    private List<DropdownOption> options;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            options = parseOptions(resource);
        } else {
            options = Collections.emptyList();
        }
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
     * Parse dropdown options from the dropdownOptions child node.
     */
    private List<DropdownOption> parseOptions(Resource componentResource) {
        List<DropdownOption> items = new ArrayList<>();
        Resource optionsResource = componentResource.getChild(OPTIONS_NODE);
        
        if (optionsResource != null) {
            for (Resource optionResource : optionsResource.getChildren()) {
                // Skip jcr: prefixed nodes
                if (optionResource.getName().startsWith("jcr:")) {
                    continue;
                }
                DropdownOptionImpl option = parseOption(optionResource);
                if (option != null) {
                    items.add(option);
                }
            }
        }
        
        return items;
    }

    /**
     * Parse a single dropdown option resource.
     */
    private DropdownOptionImpl parseOption(Resource optionResource) {
        ValueMap props = optionResource.getValueMap();
        
        String value = props.get("optionValue", String.class);
        String label = props.get("optionLabel", String.class);
        String selectedStr = props.get("optionSelected", String.class);
        boolean selected = "true".equals(selectedStr);
        
        // At minimum, we need both value and label
        if (StringUtils.isBlank(value) || StringUtils.isBlank(label)) {
            return null;
        }
        
        return new DropdownOptionImpl(value, label, selected);
    }

    @Override
    public List<DropdownOption> getOptions() {
        return options;
    }

    @Override
    public boolean hasOptions() {
        return options != null && !options.isEmpty();
    }

    /**
     * Implementation of DropdownOption interface.
     */
    public static class DropdownOptionImpl implements DropdownOption {
        private final String value;
        private final String label;
        private final boolean selected;

        public DropdownOptionImpl(String value, String label, boolean selected) {
            this.value = value;
            this.label = label;
            this.selected = selected;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }
    }
}

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
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.StepsTimelineModel;

/**
 * Sling Model implementation for the Steps Timeline component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = StepsTimelineModel.class,
    resourceType = StepsTimelineModelImpl.RESOURCE_TYPE
)
public class StepsTimelineModelImpl implements StepsTimelineModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/steps-timeline";

    private static final String STEPS_NODE = "steps";
    private static final String DEFAULT_ARIA_LABEL = "Getting started steps";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_ARIA_LABEL)
    private String ariaLabel;

    private List<Step> steps;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            steps = parseSteps(resource);
        } else {
            steps = Collections.emptyList();
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

    private List<Step> parseSteps(Resource componentResource) {
        List<Step> items = new ArrayList<>();
        Resource stepsResource = componentResource.getChild(STEPS_NODE);

        if (stepsResource != null) {
            for (Resource stepResource : stepsResource.getChildren()) {
                if (stepResource.getName().startsWith("jcr:")) {
                    continue;
                }
                StepImpl step = parseStep(stepResource, items.size() + 1);
                if (step != null) {
                    items.add(step);
                }
            }
        }

        return items;
    }

    private StepImpl parseStep(Resource stepResource, int position) {
        ValueMap props = stepResource.getValueMap();
        String stepTitle = props.get("stepTitle", String.class);
        String description = props.get("description", String.class);
        String meta = props.get("meta", String.class);
        String stepNumber = props.get("stepNumber", String.class);

        if (StringUtils.isBlank(stepTitle) && StringUtils.isBlank(description)
                && StringUtils.isBlank(meta)) {
            return null;
        }

        // A blank override falls back to the 1-based position of the step so authors
        // only type a number when they need something like "1a" or "0".
        String number = StringUtils.isNotBlank(stepNumber)
                ? stepNumber
                : String.valueOf(position);

        return new StepImpl(stepTitle, description, meta, number);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public String getVariant() {
        if ("steps-timeline--with-bg".equals(style)) {
            return "steps-timeline--with-bg";
        }
        if ("steps-timeline--muted".equals(style)) {
            return "steps-timeline--muted";
        }
        return "";
    }

    @Override
    public String getAriaLabel() {
        return StringUtils.isNotBlank(ariaLabel) ? ariaLabel : DEFAULT_ARIA_LABEL;
    }

    @Override
    public List<Step> getSteps() {
        return steps != null ? steps : Collections.<Step>emptyList();
    }

    @Override
    public boolean hasSteps() {
        return steps != null && !steps.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasSteps();
    }

    public static class StepImpl implements Step {
        private final String stepTitle;
        private final String description;
        private final String meta;
        private final String stepNumber;

        public StepImpl(String stepTitle, String description, String meta, String stepNumber) {
            this.stepTitle = stepTitle;
            this.description = description;
            this.meta = meta;
            this.stepNumber = stepNumber;
        }

        @Override
        public String getStepTitle() {
            return stepTitle;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getMeta() {
            return meta;
        }

        @Override
        public String getStepNumber() {
            return stepNumber;
        }
    }
}

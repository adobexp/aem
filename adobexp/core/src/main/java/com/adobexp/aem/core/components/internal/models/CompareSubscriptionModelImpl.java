/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 AdobeXP
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
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.adobexp.aem.core.components.models.CompareSubscriptionModel;

/**
 * Sling Model implementation for the Compare Subscription component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = CompareSubscriptionModel.class,
    resourceType = CompareSubscriptionModelImpl.RESOURCE_TYPE
)
public class CompareSubscriptionModelImpl implements CompareSubscriptionModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/compare-subscription";

    private static final String DEFAULT_TITLE = "Compare Plans";
    private static final String DEFAULT_CURRENCY = "$";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_TITLE)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String description;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_CURRENCY)
    private String currencySymbol;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    private List<Plan> plans;
    private List<String> featureTitles;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            plans = buildPlans(resource);
            featureTitles = buildFeatureTitles(plans);
            alignPlanFeatures(plans, featureTitles);
        } else {
            plans = Collections.emptyList();
            featureTitles = Collections.emptyList();
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

    private List<Plan> buildPlans(Resource resource) {
        ValueMap props = resource.getValueMap();
        List<Plan> items = new ArrayList<>();

        items.add(buildPlan("plan1", resource, props));
        items.add(buildPlan("plan2", resource, props));
        items.add(buildPlan("plan3", resource, props));
        items.add(buildPlan("plan4", resource, props));

        return items;
    }

    private Plan buildPlan(String prefix, Resource resource, ValueMap props) {
        String name = props.get(prefix + "Name", String.class);
        String monthlyPrice = props.get(prefix + "MonthlyPrice", String.class);
        String mostPopular = props.get(prefix + "MostPopular", "false");
        String bestValue = props.get(prefix + "BestValue", "false");
        String highlighted = props.get(prefix + "Highlighted", "false");
        List<FeatureValue> features = parseFeatures(resource, prefix + "Features");

        return new PlanImpl(
            prefix,
            name,
            monthlyPrice,
            "true".equals(mostPopular),
            "true".equals(bestValue),
            "true".equals(highlighted),
            features
        );
    }

    private List<FeatureValue> parseFeatures(Resource componentResource, String nodeName) {
        List<FeatureValue> features = new ArrayList<>();
        Resource featuresResource = componentResource.getChild(nodeName);

        if (featuresResource != null) {
            for (Resource featureResource : featuresResource.getChildren()) {
                if (featureResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ValueMap valueMap = featureResource.getValueMap();
                String title = valueMap.get("title", String.class);
                String included = valueMap.get("included", "false");
                if (StringUtils.isNotBlank(title)) {
                    features.add(new FeatureValueImpl(title, "true".equals(included)));
                }
            }
        }

        return features;
    }

    private List<String> buildFeatureTitles(List<Plan> planList) {
        Set<String> titles = new LinkedHashSet<>();
        for (Plan plan : planList) {
            if (plan instanceof PlanImpl) {
                for (FeatureValue value : ((PlanImpl) plan).getRawFeatures()) {
                    if (StringUtils.isNotBlank(value.getTitle())) {
                        titles.add(value.getTitle());
                    }
                }
            }
        }
        return new ArrayList<>(titles);
    }

    private void alignPlanFeatures(List<Plan> planList, List<String> titles) {
        for (Plan plan : planList) {
            if (plan instanceof PlanImpl) {
                ((PlanImpl) plan).alignFeatures(titles);
            }
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCurrencySymbol() {
        return StringUtils.isNotBlank(currencySymbol) ? currencySymbol : DEFAULT_CURRENCY;
    }

    @Override
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public List<Plan> getPlans() {
        return plans;
    }

    @Override
    public List<String> getFeatureTitles() {
        return featureTitles;
    }

    @Override
    public boolean hasPlans() {
        if (plans == null || plans.isEmpty()) {
            return false;
        }
        for (Plan plan : plans) {
            if (plan.hasContent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Plan implementation with computed helpers.
     */
    public static class PlanImpl implements Plan {
        private final String key;
        private final String name;
        private final String monthlyPrice;
        private final boolean mostPopular;
        private final boolean bestValue;
        private final boolean highlighted;
        private final List<FeatureValue> rawFeatures;
        private List<FeatureValue> alignedFeatures;

        public PlanImpl(
            String key,
            String name,
            String monthlyPrice,
            boolean mostPopular,
            boolean bestValue,
            boolean highlighted,
            List<FeatureValue> rawFeatures
        ) {
            this.key = key;
            this.name = name;
            this.monthlyPrice = monthlyPrice;
            this.mostPopular = mostPopular;
            this.bestValue = bestValue;
            this.highlighted = highlighted;
            this.rawFeatures = rawFeatures != null ? rawFeatures : Collections.emptyList();
            this.alignedFeatures = this.rawFeatures;
        }

        private void alignFeatures(List<String> titles) {
            if (titles == null || titles.isEmpty()) {
                alignedFeatures = rawFeatures;
                return;
            }
            Map<String, Boolean> includedByTitle = new HashMap<>();
            for (FeatureValue value : rawFeatures) {
                includedByTitle.put(value.getTitle(), value.isIncluded());
            }
            List<FeatureValue> aligned = new ArrayList<>();
            for (String title : titles) {
                boolean included = includedByTitle.getOrDefault(title, false);
                aligned.add(new FeatureValueImpl(title, included));
            }
            alignedFeatures = aligned;
        }

        private List<FeatureValue> getRawFeatures() {
            return rawFeatures;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getMonthlyPrice() {
            return monthlyPrice;
        }

        @Override
        public boolean isMostPopular() {
            return mostPopular;
        }

        @Override
        public boolean isBestValue() {
            return bestValue;
        }

        @Override
        public boolean isHighlighted() {
            return highlighted;
        }

        @Override
        public List<FeatureValue> getFeatureValues() {
            return alignedFeatures;
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotBlank(name)
                || StringUtils.isNotBlank(monthlyPrice)
                || mostPopular
                || bestValue
                || (rawFeatures != null && !rawFeatures.isEmpty());
        }
    }

    /**
     * Feature value implementation.
     */
    public static class FeatureValueImpl implements FeatureValue {
        private final String title;
        private final boolean included;

        public FeatureValueImpl(String title, boolean included) {
            this.title = title;
            this.included = included;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public boolean isIncluded() {
            return included;
        }
    }
}

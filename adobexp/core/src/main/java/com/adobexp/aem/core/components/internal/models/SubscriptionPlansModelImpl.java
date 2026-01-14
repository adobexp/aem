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

import com.adobexp.aem.core.components.models.SubscriptionPlansModel;

/**
 * Sling Model implementation for the Subscription Plans component.
 * Reads plan configurations and feature lists from component properties.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = SubscriptionPlansModel.class,
    resourceType = SubscriptionPlansModelImpl.RESOURCE_TYPE
)
public class SubscriptionPlansModelImpl implements SubscriptionPlansModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/subscription-plans";

    private static final String DEFAULT_BILLING = "yearly";
    private static final String DEFAULT_CURRENCY = "$";
    private static final String DEFAULT_YEARLY_DISCOUNT = "SAVE 33%";
    private static final String DEFAULT_MONTHLY_NOTE = "Billed monthly";
    private static final String DEFAULT_YEARLY_NOTE = "Billed yearly";
    private static final String DEFAULT_TITLE = "Pick your plan";

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
    @Default(values = DEFAULT_BILLING)
    private String defaultBilling;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_YEARLY_DISCOUNT)
    private String yearlyDiscountText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_CURRENCY)
    private String currencySymbol;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_MONTHLY_NOTE)
    private String monthlyBillingNote;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_YEARLY_NOTE)
    private String yearlyBillingNote;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    private List<Plan> plans;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            plans = buildPlans(resource);
        } else {
            plans = Collections.emptyList();
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
        String yearlyOriginalPrice = props.get(prefix + "YearlyOriginalPrice", String.class);
        String yearlyPrice = props.get(prefix + "YearlyPrice", String.class);
        String discountUntil = props.get(prefix + "DiscountUntil", String.class);
        String ctaText = props.get(prefix + "CtaText", String.class);
        String ctaLink = props.get(prefix + "CtaLink", String.class);
        String ctaNewTab = props.get(prefix + "CtaNewTab", "false");
        String topChoice = props.get(prefix + "TopChoice", "false");
        String specialOffer = props.get(prefix + "SpecialOffer", "false");
        String creditsValue = props.get(prefix + "CreditsValue", String.class);
        String creditsUnit = props.get(prefix + "CreditsUnit", String.class);
        String creditsLabel = props.get(prefix + "CreditsLabel", String.class);
        List<String> features = parseFeatures(resource, prefix + "Features");

        return new PlanImpl(
            name,
            monthlyPrice,
            yearlyPrice,
            yearlyOriginalPrice,
            ctaText,
            ctaLink,
            "true".equals(ctaNewTab),
            "true".equals(topChoice),
            "true".equals(specialOffer),
            discountUntil,
            buildCreditsText(creditsValue, creditsUnit, creditsLabel),
            features
        );
    }

    private List<String> parseFeatures(Resource componentResource, String nodeName) {
        List<String> features = new ArrayList<>();
        Resource featuresResource = componentResource.getChild(nodeName);

        if (featuresResource != null) {
            for (Resource featureResource : featuresResource.getChildren()) {
                if (featureResource.getName().startsWith("jcr:")) {
                    continue;
                }
                String feature = featureResource.getValueMap().get("feature", String.class);
                if (StringUtils.isNotBlank(feature)) {
                    features.add(feature);
                }
            }
        }

        return features;
    }

    private String buildCreditsText(String value, String unit, String label) {
        StringBuilder text = new StringBuilder();
        if (StringUtils.isNotBlank(value)) {
            text.append(value);
        }
        if (StringUtils.isNotBlank(unit)) {
            if (text.length() > 0) {
                text.append(" ");
            }
            text.append(unit);
        }
        if (StringUtils.isNotBlank(label)) {
            if (text.length() > 0) {
                text.append(" ");
            }
            text.append(label);
        }
        return text.length() > 0 ? text.toString() : null;
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
    public String getDefaultBilling() {
        if ("monthly".equals(defaultBilling) || "yearly".equals(defaultBilling)) {
            return defaultBilling;
        }
        return DEFAULT_BILLING;
    }

    @Override
    public String getYearlyDiscountText() {
        return yearlyDiscountText;
    }

    @Override
    public String getCurrencySymbol() {
        return StringUtils.isNotBlank(currencySymbol) ? currencySymbol : DEFAULT_CURRENCY;
    }

    @Override
    public String getMonthlyBillingNote() {
        return monthlyBillingNote;
    }

    @Override
    public String getYearlyBillingNote() {
        return yearlyBillingNote;
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
        private final String name;
        private final String monthlyPrice;
        private final String yearlyPrice;
        private final String yearlyOriginalPrice;
        private final String ctaText;
        private final String ctaLink;
        private final boolean ctaNewTab;
        private final boolean topChoice;
        private final boolean specialOffer;
        private final String discountUntil;
        private final String creditsText;
        private final List<String> features;

        public PlanImpl(
            String name,
            String monthlyPrice,
            String yearlyPrice,
            String yearlyOriginalPrice,
            String ctaText,
            String ctaLink,
            boolean ctaNewTab,
            boolean topChoice,
            boolean specialOffer,
            String discountUntil,
            String creditsText,
            List<String> features
        ) {
            this.name = name;
            this.monthlyPrice = monthlyPrice;
            this.yearlyPrice = yearlyPrice;
            this.yearlyOriginalPrice = yearlyOriginalPrice;
            this.ctaText = ctaText;
            this.ctaLink = ctaLink;
            this.ctaNewTab = ctaNewTab;
            this.topChoice = topChoice;
            this.specialOffer = specialOffer;
            this.discountUntil = discountUntil;
            this.creditsText = creditsText;
            this.features = features != null ? features : Collections.emptyList();
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
        public String getYearlyPrice() {
            return yearlyPrice;
        }

        @Override
        public String getYearlyOriginalPrice() {
            return yearlyOriginalPrice;
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
        public boolean isCtaNewTab() {
            return ctaNewTab;
        }

        @Override
        public boolean isTopChoice() {
            return topChoice;
        }

        @Override
        public boolean isSpecialOffer() {
            return specialOffer;
        }

        @Override
        public String getDiscountUntil() {
            return discountUntil;
        }

        @Override
        public String getCreditsText() {
            return creditsText;
        }

        @Override
        public List<String> getFeatures() {
            return features;
        }

        @Override
        public boolean hasFeatures() {
            return features != null && !features.isEmpty();
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotBlank(name)
                || StringUtils.isNotBlank(monthlyPrice)
                || StringUtils.isNotBlank(yearlyPrice)
                || StringUtils.isNotBlank(yearlyOriginalPrice)
                || StringUtils.isNotBlank(ctaText)
                || StringUtils.isNotBlank(ctaLink)
                || StringUtils.isNotBlank(creditsText)
                || hasFeatures()
                || topChoice
                || specialOffer;
        }
    }
}

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

import com.adobexp.aem.core.components.models.LeadCarouselModel;

/**
 * Sling Model implementation for the Lead Carousel component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = LeadCarouselModel.class,
    resourceType = LeadCarouselModelImpl.RESOURCE_TYPE
)
public class LeadCarouselModelImpl implements LeadCarouselModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/lead-carousel";

    private static final String SLIDES_NODE = "slides";
    private static final String DEFAULT_ARIA_LABEL = "Featured stories";
    private static final long DEFAULT_INTERVAL_MS = 7000L;
    private static final long MIN_INTERVAL_MS = 2000L;
    private static final long MAX_INTERVAL_MS = 60000L;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_ARIA_LABEL)
    private String ariaLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = true)
    private boolean autoplay;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(longValues = DEFAULT_INTERVAL_MS)
    private long interval;

    private List<Slide> slides;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            slides = parseSlides(resource);
        } else {
            slides = Collections.emptyList();
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

    private List<Slide> parseSlides(Resource componentResource) {
        List<Slide> items = new ArrayList<>();
        Resource slidesResource = componentResource.getChild(SLIDES_NODE);

        if (slidesResource != null) {
            for (Resource itemResource : slidesResource.getChildren()) {
                if (itemResource.getName().startsWith("jcr:")) {
                    continue;
                }
                SlideImpl slide = parseSlide(itemResource);
                if (slide != null) {
                    items.add(slide);
                }
            }
        }

        return items;
    }

    private SlideImpl parseSlide(Resource itemResource) {
        ValueMap props = itemResource.getValueMap();

        String imagePath = props.get("imagePath", String.class);
        String title = props.get("title", String.class);
        String text = props.get("text", String.class);

        if (StringUtils.isBlank(imagePath) && StringUtils.isBlank(title) && StringUtils.isBlank(text)) {
            return null;
        }

        return new SlideImpl(
            imagePath,
            props.get("imageAlt", String.class),
            title,
            text,
            props.get("ctaText", String.class),
            props.get("ctaLink", String.class),
            props.get("ctaExternal", false),
            props.get("promoLeftValue", String.class),
            props.get("promoLeftLabel", String.class),
            props.get("promoRightValue", String.class),
            props.get("promoRightLabel", String.class)
        );
    }

    @Override
    public String getAriaLabel() {
        return StringUtils.isNotBlank(ariaLabel) ? ariaLabel : DEFAULT_ARIA_LABEL;
    }

    @Override
    public boolean isAutoplay() {
        return autoplay;
    }

    @Override
    public String getInterval() {
        long value = interval;
        if (value < MIN_INTERVAL_MS || value > MAX_INTERVAL_MS) {
            value = DEFAULT_INTERVAL_MS;
        }
        return Long.toString(value);
    }

    @Override
    public List<Slide> getSlides() {
        return slides;
    }

    @Override
    public boolean hasSlides() {
        return slides != null && !slides.isEmpty();
    }

    public static class SlideImpl implements Slide {
        private final String imagePath;
        private final String imageAlt;
        private final String title;
        private final String text;
        private final String ctaText;
        private final String ctaLink;
        private final boolean ctaExternal;
        private final String promoLeftValue;
        private final String promoLeftLabel;
        private final String promoRightValue;
        private final String promoRightLabel;

        public SlideImpl(
                String imagePath,
                String imageAlt,
                String title,
                String text,
                String ctaText,
                String ctaLink,
                boolean ctaExternal,
                String promoLeftValue,
                String promoLeftLabel,
                String promoRightValue,
                String promoRightLabel) {
            this.imagePath = imagePath;
            this.imageAlt = imageAlt;
            this.title = title;
            this.text = text;
            this.ctaText = ctaText;
            this.ctaLink = ctaLink;
            this.ctaExternal = ctaExternal;
            this.promoLeftValue = promoLeftValue;
            this.promoLeftLabel = promoLeftLabel;
            this.promoRightValue = promoRightValue;
            this.promoRightLabel = promoRightLabel;
        }

        @Override
        public String getImagePath() {
            return imagePath;
        }

        @Override
        public String getImageAlt() {
            return imageAlt;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getText() {
            return text;
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
        public boolean isCtaExternal() {
            return ctaExternal;
        }

        @Override
        public boolean isHasCta() {
            return StringUtils.isNotBlank(ctaText);
        }

        @Override
        public String getPromoLeftValue() {
            return promoLeftValue;
        }

        @Override
        public String getPromoLeftLabel() {
            return promoLeftLabel;
        }

        @Override
        public String getPromoRightValue() {
            return promoRightValue;
        }

        @Override
        public String getPromoRightLabel() {
            return promoRightLabel;
        }

        @Override
        public boolean isHasPromo() {
            return StringUtils.isNotBlank(promoLeftValue) || StringUtils.isNotBlank(promoRightValue);
        }
    }
}

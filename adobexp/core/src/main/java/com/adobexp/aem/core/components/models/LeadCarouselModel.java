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

package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Lead Carousel component.
 */
public interface LeadCarouselModel {

    /**
     * Returns the accessible label for the carousel landmark,
     * defaults to {@code Featured stories}.
     */
    String getAriaLabel();

    /**
     * Returns {@code true} when the slideshow should start automatically.
     */
    boolean isAutoplay();

    /**
     * Returns the authored interval in milliseconds as a string for
     * {@code data-interval} (default {@code 7000}).
     */
    String getInterval();

    List<Slide> getSlides();

    boolean hasSlides();

    /**
     * Interface representing a single carousel slide.
     */
    interface Slide {

        String getImagePath();

        String getImageAlt();

        String getTitle();

        String getText();

        String getCtaText();

        String getCtaLink();

        boolean isCtaExternal();

        boolean isHasCta();

        String getPromoLeftValue();

        String getPromoLeftLabel();

        String getPromoRightValue();

        String getPromoRightLabel();

        boolean isHasPromo();
    }
}

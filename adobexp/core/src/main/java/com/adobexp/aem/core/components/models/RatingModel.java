/*~~~~* Copyright 2025 AdobeXP
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
 ~~~~~*/

package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Rating component.
 * Provides access to rating configuration including title, images, CTA, star rating and background color.
 */
public interface RatingModel {

    /**
     * Gets the title text.
     * @return title
     */
    String getTitle();

    /**
     * Gets the subtitle text.
     * @return subtitle
     */
    String getSubTitle();

    /**
     * Gets the list of avatar images.
     * @return list of avatar images
     */
    List<AvatarImage> getAvatarImages();

    /**
     * Gets the CTA text.
     * @return CTA text
     */
    String getCtaText();

    /**
     * Gets the CTA link.
     * @return CTA link
     */
    String getCtaLink();

    /**
     * Checks if the CTA link is external.
     * @return true if CTA link is external
     */
    boolean isCtaLinkExternal();

    /**
     * Gets the star rating value.
     * @return star rating
     */
    double getStarRating();

    /**
     * Gets the total number of stars.
     * @return total stars
     */
    int getStarTotal();

    /**
     * Checks if background color should be applied.
     * @return true if background color should be applied
     */
    boolean isApplyBgColor();

    /**
     * Represents an avatar image.
     */
    interface AvatarImage {
        /**
         * Gets the image path.
         * @return image path
         */
        String getImagePath();

        /**
         * Gets the alt text.
         * @return alt text
         */
        String getAltText();
    }
}
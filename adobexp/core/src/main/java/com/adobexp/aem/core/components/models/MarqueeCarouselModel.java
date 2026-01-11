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
package com.adobexp.aem.core.components.models;

import java.util.List;

/**
 * Sling Model interface for the Marquee Carousel component.
 * Provides access to carousel configuration including title, CTA, and carousel images.
 * Supports both manual image entry and DAM folder selection.
 */
public interface MarqueeCarouselModel {

    /**
     * Gets the image source type.
     * @return "manual" for multifield images or "damFolder" for DAM folder images
     */
    String getImageSource();

    /**
     * Gets the DAM folder path when image source is "damFolder".
     * @return the DAM folder path
     */
    String getDamFolderPath();

    /**
     * Gets the list of carousel images.
     * Returns images from either multifield (manual) or DAM folder based on configuration.
     * @return list of carousel images
     */
    List<CarouselImage> getCarouselImages();

    /**
     * Checks if the carousel has any images configured.
     * @return true if any images are configured
     */
    boolean hasImages();

    /**
     * Interface representing a single carousel image.
     */
    interface CarouselImage {
        
        /**
         * Gets the image path from DAM.
         * @return the image path
         */
        String getImagePath();

        /**
         * Gets the image alt text.
         * @return the alt text
         */
        String getImageAlt();
    }
}

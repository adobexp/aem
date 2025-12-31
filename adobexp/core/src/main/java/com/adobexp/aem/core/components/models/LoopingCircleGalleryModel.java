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
 * Sling Model interface for the Looping Circle Gallery component.
 * Provides access to gallery configuration including overlay message and gallery images.
 */
public interface LoopingCircleGalleryModel {

    /**
     * Gets the gallery overlay message text.
     * @return the gallery message displayed in the center overlay
     */
    String getGalleryMessage();

    /**
     * Gets the list of gallery images.
     * @return list of gallery images
     */
    List<GalleryImage> getGalleryImages();

    /**
     * Checks if the gallery has any images configured.
     * @return true if any images are configured
     */
    boolean hasImages();

    /**
     * Interface representing a single gallery image.
     */
    interface GalleryImage {
        
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

        /**
         * Gets the index of this image in the gallery (0-based).
         * @return the index
         */
        int getIndex();
    }
}


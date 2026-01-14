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
 * Sling Model interface for the Masonry Gallery component.
 * Provides access to component configuration including media items, CTA elements,
 * and style options. Supports both manual media item entry and DAM folder selection.
 */
public interface MasonryGalleryModel {

    /**
     * Gets the media source type.
     * @return "manual" or "damFolder"
     */
    String getMediaSourceType();

    /**
     * Gets the DAM folder path for automatic media loading.
     * @return DAM folder path
     */
    String getDamFolderPath();

    /**
     * Gets the default video start time for DAM folder videos.
     * @return default video start time in seconds
     */
    String getDefaultVideoStartTime();

    /**
     * Gets the list of gallery items (either from manual entry or DAM folder).
     * @return list of gallery items
     */
    List<GalleryItem> getGalleryItems();

    /**
     * Checks if there are media items to display.
     * @return true if gallery has items
     */
    boolean hasMediaItems();

    // ============================================
    // CTA (Call to Action) Fields
    // ============================================

    /**
     * Gets the CTA link URL.
     * @return CTA link URL
     */
    String getCtaLink();

    /**
     * Gets the CTA button text.
     * @return CTA text
     */
    String getCtaText();

    /**
     * Checks if CTA link should open in a new tab.
     * @return true if external link
     */
    boolean isCtaExternal();

    /**
     * Checks if CTA should be displayed.
     * @return true if CTA has both link and text
     */
    boolean hasCtaLink();

    // ============================================
    // Style Options
    // ============================================

    /**
     * Checks if background color should be applied.
     * @return true if background should be shown
     */
    boolean isWithBackground();

    /**
     * Checks if title overlay should be shown on hover.
     * @return true if title overlay is enabled
     */
    boolean isShowTitleOverlay();

    /**
     * Gets the aria label for accessibility.
     * @return aria label text
     */
    String getAriaLabel();

    /**
     * Interface for individual gallery items
     */
    interface GalleryItem {
        /**
         * Gets the media type.
         * @return "image" or "video"
         */
        String getMediaType();

        /**
         * Gets the media path.
         * @return path to the media asset
         */
        String getMediaPath();

        /**
         * Gets the media title (optional).
         * @return media title for overlay
         */
        String getMediaTitle();

        /**
         * Gets the alt text.
         * @return alt text for accessibility
         */
        String getMediaAlt();

        /**
         * Gets the video start time in seconds (for videos only).
         * @return video start time
         */
        String getVideoStartTime();

        /**
         * Checks if this item is a video.
         * @return true if video
         */
        boolean isVideo();
    }
}

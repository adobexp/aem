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

/**
 * Sling Model interface for the Blob Image Section component.
 * Provides access to component configuration including content, media, style options,
 * theme selection, CTA elements, and overlay configurations.
 */
public interface BlobImageSectionModel {

    /**
     * Gets the badge title text.
     * @return badge title
     */
    String getBadgeTitle();

    /**
     * Gets the main title/headline text.
     * @return main title
     */
    String getMainTitle();

    /**
     * Gets the description text.
     * @return description
     */
    String getDescription();

    /**
     * Gets the selected media type.
     * @return "image" or "video"
     */
    String getMediaType();

    /**
     * Gets the primary image path from DAM.
     * @return primary image path
     */
    String getPrimaryAssetPath();

    /**
     * Gets the alt text for the primary image.
     * @return primary image alt text
     */
    String getPrimaryAssetAlt();

    /**
     * Gets the primary video path from DAM.
     * @return primary video path
     */
    String getPrimaryVideoPath();

    /**
     * Gets the alt text for the primary video.
     * @return primary video alt text
     */
    String getPrimaryVideoAlt();

    /**
     * Checks if the selected media type is video.
     * @return true if video is selected
     */
    boolean isVideo();

    /**
     * Gets the effective media path (image or video based on selection).
     * @return the effective media path
     */
    String getEffectiveMediaPath();

    /**
     * Gets the effective alt text (for image or video based on selection).
     * @return the effective alt text
     */
    String getEffectiveMediaAlt();

    /**
     * Gets the video start time in seconds.
     * @return video start time or null if not configured
     */
    String getVideoStartTime();

    /**
     * Gets the icon badge image path from DAM.
     * @return icon badge image path
     */
    String getIconBadgeImage();

    /**
     * Gets the alt text for the icon badge.
     * @return icon badge alt text
     */
    String getIconBadgeAlt();

    /**
     * Gets the icon badge position CSS class.
     * @return position class (e.g., "blob-image-section__icon-badge--bottom-left")
     */
    String getIconBadgePosition();

    /**
     * Checks if icon badge should be displayed.
     * @return true if icon badge image is set
     */
    boolean hasIconBadge();

    /**
     * Gets the shape pattern CSS class.
     * @return shape pattern class (e.g., "blob-image-section--shape-v1")
     */
    String getShapePattern();

    /**
     * Gets the image alignment CSS class.
     * @return image alignment class (e.g., "blob-image-section--image-left")
     */
    String getImageAlignment();

    /**
     * Checks if background color should be applied.
     * @return true if background should be shown
     */
    boolean isWithBackground();

    /**
     * Checks if card background color should be applied.
     * @return true if card background should be shown
     */
    boolean isWithCardBackground();

    // ============================================
    // New Fields for Enhanced Component Support
    // ============================================

    // CTA (Call to Action) Fields

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

    /**
     * Gets the CTA position CSS class.
     * @return position class (e.g., "blob-image-section__overlay-cta--bottom-left")
     */
    String getCtaPosition();

    // Overlay Image Fields

    /**
     * Gets the overlay image path from DAM.
     * @return overlay image path
     */
    String getOverlayImagePath();

    /**
     * Gets the alt text for the overlay image.
     * @return overlay image alt text
     */
    String getOverlayImageAlt();

    /**
     * Gets the overlay image position CSS class.
     * @return position class (e.g., "blob-image-section__overlay-image--bottom-right")
     */
    String getOverlayImagePosition();

    /**
     * Checks if overlay image should be displayed.
     * @return true if overlay image path is set
     */
    boolean hasOverlayImage();

    // Overlay Card Fields

    /**
     * Gets the overlay card image path from DAM.
     * @return overlay card image path
     */
    String getOverlayCardImagePath();

    /**
     * Gets the overlay card title text.
     * @return overlay card title
     */
    String getOverlayCardTitle();

    /**
     * Gets the overlay card subtitle text.
     * @return overlay card subtitle
     */
    String getOverlayCardSubtitle();

    /**
     * Gets the overlay card position CSS class.
     * @return position class (e.g., "blob-image-section__overlay-card--bottom-right")
     */
    String getOverlayCardPosition();

    /**
     * Checks if overlay card should be displayed.
     * @return true if overlay card has required fields
     */
    boolean hasOverlayCard();
}

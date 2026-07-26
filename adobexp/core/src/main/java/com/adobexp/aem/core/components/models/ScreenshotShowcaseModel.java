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
 * Sling Model interface for the Screenshot Showcase component.
 */
public interface ScreenshotShowcaseModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the accessible label of the section, defaulting to {@code Product screenshots}.
     */
    String getAriaLabel();

    /**
     * Returns the section style variant CSS class, e.g. {@code screenshot-showcase--with-bg}
     * or {@code screenshot-showcase--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    /**
     * Returns the grid modifier class, e.g. {@code screenshot-showcase__grid--2}.
     */
    String getGridClass();

    /**
     * Returns the parallax drift strength in pixels, emitted as {@code data-parallax}.
     * {@code 0} disables the parallax effect.
     */
    String getParallaxStrength();

    /**
     * Returns {@code true} when frames should tilt toward the pointer.
     */
    boolean isEnableTilt();

    List<Frame> getFrames();

    boolean hasFrames();

    boolean hasContent();

    interface Frame {

        String getImageDark();

        /**
         * Returns the light theme screenshot path. Falls back to the dark image when the
         * author did not supply a light variant, so the frame stays visible in both themes.
         */
        String getImageLight();

        /**
         * Returns the image alt text. Never {@code null}.
         */
        String getAltText();

        String getUrlLabel();

        String getBadge();

        String getFrameTitle();

        String getDescription();

        /**
         * Returns the parallax depth multiplier, emitted as {@code data-depth}.
         */
        String getDepth();

        String getImageWidth();

        String getImageHeight();

        /**
         * Returns {@code true} when the frame has a badge, title or description to caption.
         */
        boolean hasCaption();
    }
}

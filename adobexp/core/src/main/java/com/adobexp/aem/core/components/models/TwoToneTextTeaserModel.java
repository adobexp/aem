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
 * Sling Model interface for the Two Tone Text Teaser component.
 * Provides access to primary text, secondary text, and CTA configuration.
 */
public interface TwoToneTextTeaserModel {

    /**
     * Gets the primary text content.
     * @return primary text displayed in primary color
     */
    String getPrimaryText();

    /**
     * Gets the secondary text content.
     * @return secondary text displayed in secondary color
     */
    String getSecondaryText();

    /**
     * Gets the CTA button text.
     * @return CTA button text
     */
    String getCtaText();

    /**
     * Gets the CTA link URL.
     * @return CTA link URL
     */
    String getCtaLink();

    /**
     * Checks if the CTA link is external.
     * @return true if the CTA link should open in a new tab
     */
    boolean isCtaLinkExternal();
}


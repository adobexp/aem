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
 * Sling Model interface for the Quote component.
 * Provides access to quote configuration including title, quote text, and author information.
 */
public interface QuoteModel {

    /**
     * Gets the component title text (e.g., "Quote").
     * @return component title
     */
    String getQuoteTitle();

    /**
     * Gets the primary quote text.
     * @return primary quote text
     */
    String getQuotePrimaryText();

    /**
     * Gets the muted/secondary quote text.
     * @return muted quote text
     */
    String getQuoteMutedText();

    /**
     * Gets the author avatar SVG image path from DAM.
     * @return avatar image path or null if not configured
     */
    String getQuoteAvatar();

    /**
     * Gets the author's name.
     * @return author name
     */
    String getAuthorName();

    /**
     * Gets the author's organisation/company name.
     * @return author organisation
     */
    String getAuthorOrganisation();
}


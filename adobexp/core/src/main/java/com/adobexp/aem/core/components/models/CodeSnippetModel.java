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
 * Sling Model interface for the Code Snippet component.
 */
public interface CodeSnippetModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the section style variant CSS class, e.g. {@code code-snippet--with-bg}
     * or {@code code-snippet--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    /**
     * Returns the screen-reader label of the section.
     */
    String getAriaLabel();

    boolean isShowLineNumbers();

    /**
     * Returns {@code "false"} when the line-number gutter is disabled, otherwise
     * {@code null} so that HTL omits the {@code data-line-numbers} attribute.
     */
    String getLineNumbersAttribute();

    List<Snippet> getSnippets();

    boolean hasSnippets();

    /**
     * Returns true when at least one snippet carries a filename, so HTL can render
     * the filename caption in the tab bar.
     */
    boolean hasFilenames();

    /**
     * Returns the filename of the initially active snippet, used as the initial text
     * of the tab bar caption. The client-side script keeps it in sync on tab change.
     */
    String getActiveFilename();

    boolean hasContent();

    interface Snippet {

        String getTabLabel();

        /**
         * Returns the highlighting language, e.g. {@code javascript}. Falls back to
         * {@code text} when the author left it unset.
         */
        String getLanguage();

        String getFilename();

        /**
         * Returns the source, with line endings normalised and surrounding blank
         * lines removed so the line-number gutter stays aligned.
         */
        String getCode();

        /**
         * Returns the stable, page-unique id of this snippet's pane, used both as the
         * pane {@code id} and as the tab's {@code data-target}.
         */
        String getPaneId();

        boolean isActive();
    }
}

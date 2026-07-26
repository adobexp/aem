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
 * Sling Model interface for the Flow Diagram component.
 */
public interface FlowDiagramModel {

    String getTitle();

    String getSubtitle();

    /**
     * Returns the explanatory caption rendered below the diagram.
     */
    String getCaption();

    /**
     * Returns the section style variant CSS class, e.g. {@code flow-diagram--with-bg} or
     * {@code flow-diagram--muted}. Empty string for the default (no modifier) style.
     */
    String getVariant();

    String getAriaLabel();

    /**
     * Returns the pipeline stages in DOM order; connectors are drawn between consecutive stages.
     */
    List<Stage> getStages();

    boolean hasStages();

    boolean hasContent();

    interface Stage {
        String getStageLabel();

        List<Node> getNodes();
    }

    interface Node {
        String getIcon();

        String getNodeTitle();

        String getSubtitle();

        /**
         * Returns the optional pill rendered below the node subtitle, e.g. {@code GeoIP enrich}.
         */
        String getTag();
    }
}

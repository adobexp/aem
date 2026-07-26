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

package com.adobexp.aem.core.components.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.FlowDiagramModel;

/**
 * Sling Model implementation for the Flow Diagram component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = FlowDiagramModel.class,
    resourceType = FlowDiagramModelImpl.RESOURCE_TYPE
)
public class FlowDiagramModelImpl implements FlowDiagramModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/flow-diagram";

    private static final String STAGES_NODE = "stages";
    private static final String STYLE_WITH_BG = "flow-diagram--with-bg";
    private static final String STYLE_MUTED = "flow-diagram--muted";

    /** Splits the nodes textarea on any line ending. */
    private static final String LINE_SPLIT_PATTERN = "\\r\\n|\\r|\\n";

    /** Node lines are pipe delimited: {@code Icon|Title|Subtitle|Tag}. */
    private static final String FIELD_DELIMITER = "\\|";

    private static final int MAX_NODE_FIELDS = 4;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String caption;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Architecture flow")
    private String ariaLabel;

    private List<Stage> stages;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            stages = parseStages(resource);
        } else {
            stages = Collections.emptyList();
        }
    }

    private Resource getResource() {
        if (currentResource != null) {
            return currentResource;
        }
        if (request != null) {
            return request.getResource();
        }
        return null;
    }

    private List<Stage> parseStages(Resource componentResource) {
        List<Stage> items = new ArrayList<>();
        Resource stagesResource = componentResource.getChild(STAGES_NODE);

        if (stagesResource != null) {
            for (Resource stageResource : stagesResource.getChildren()) {
                if (stageResource.getName().startsWith("jcr:")) {
                    continue;
                }
                StageImpl stage = parseStage(stageResource);
                if (stage != null) {
                    items.add(stage);
                }
            }
        }

        return items;
    }

    private StageImpl parseStage(Resource stageResource) {
        ValueMap props = stageResource.getValueMap();
        String stageLabel = props.get("stageLabel", String.class);
        List<Node> nodes = parseNodes(props.get("nodes", String.class));

        if (StringUtils.isBlank(stageLabel) && nodes.isEmpty()) {
            return null;
        }

        return new StageImpl(stageLabel, nodes);
    }

    /**
     * Parses the authored nodes textarea, one node per line in the form
     * {@code Icon|Title|Subtitle|Tag}. Blank lines are skipped and trailing fields may be omitted.
     */
    private List<Node> parseNodes(String value) {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }

        List<Node> nodes = new ArrayList<>();
        for (String rawLine : value.split(LINE_SPLIT_PATTERN)) {
            String line = StringUtils.trimToEmpty(rawLine);
            if (line.isEmpty()) {
                continue;
            }

            String icon = "";
            String nodeTitle = "";
            String nodeSubtitle = "";
            String nodeTag = "";

            if (line.indexOf('|') < 0) {
                // A single unqualified value is most useful as the node title.
                nodeTitle = line;
            } else {
                String[] fields = line.split(FIELD_DELIMITER, MAX_NODE_FIELDS);
                icon = StringUtils.trimToEmpty(fields[0]);
                if (fields.length > 1) {
                    nodeTitle = StringUtils.trimToEmpty(fields[1]);
                }
                if (fields.length > 2) {
                    nodeSubtitle = StringUtils.trimToEmpty(fields[2]);
                }
                if (fields.length > 3) {
                    nodeTag = StringUtils.trimToEmpty(fields[3]);
                }
            }

            if (nodeTitle.isEmpty() && nodeSubtitle.isEmpty()) {
                continue;
            }

            nodes.add(new NodeImpl(icon, nodeTitle, nodeSubtitle, nodeTag));
        }

        return nodes;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getVariant() {
        if (STYLE_WITH_BG.equals(style) || STYLE_MUTED.equals(style)) {
            return style;
        }
        return "";
    }

    @Override
    public String getAriaLabel() {
        return StringUtils.isNotBlank(ariaLabel) ? ariaLabel : "Architecture flow";
    }

    @Override
    public List<Stage> getStages() {
        return stages != null ? stages : Collections.emptyList();
    }

    @Override
    public boolean hasStages() {
        return stages != null && !stages.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasStages();
    }

    public static class StageImpl implements Stage {
        private final String stageLabel;
        private final List<Node> nodes;

        public StageImpl(String stageLabel, List<Node> nodes) {
            this.stageLabel = stageLabel;
            this.nodes = nodes != null ? nodes : Collections.<Node>emptyList();
        }

        @Override
        public String getStageLabel() {
            return stageLabel;
        }

        @Override
        public List<Node> getNodes() {
            return nodes;
        }
    }

    public static class NodeImpl implements Node {
        private final String icon;
        private final String nodeTitle;
        private final String subtitle;
        private final String tag;

        public NodeImpl(String icon, String nodeTitle, String subtitle, String tag) {
            this.icon = icon;
            this.nodeTitle = nodeTitle;
            this.subtitle = subtitle;
            this.tag = tag;
        }

        @Override
        public String getIcon() {
            return icon;
        }

        @Override
        public String getNodeTitle() {
            return nodeTitle;
        }

        @Override
        public String getSubtitle() {
            return subtitle;
        }

        @Override
        public String getTag() {
            return tag;
        }
    }
}

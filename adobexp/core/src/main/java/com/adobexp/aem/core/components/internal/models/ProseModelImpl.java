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
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobexp.aem.core.components.models.ProseModel;

/**
 * Sling Model implementation for the Prose component.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = ProseModel.class,
    resourceType = ProseModelImpl.RESOURCE_TYPE
)
public class ProseModelImpl implements ProseModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/prose";

    private static final String BLOCKS_NODE = "blocks";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    private List<ProseBlock> blocks;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            blocks = parseBlocks(resource);
        } else {
            blocks = Collections.emptyList();
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

    private List<ProseBlock> parseBlocks(Resource componentResource) {
        List<ProseBlock> items = new ArrayList<>();
        Resource blocksResource = componentResource.getChild(BLOCKS_NODE);

        if (blocksResource != null) {
            for (Resource blockResource : blocksResource.getChildren()) {
                if (blockResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ProseBlockImpl block = parseBlock(blockResource);
                if (block != null) {
                    items.add(block);
                }
            }
        }

        return items;
    }

    private ProseBlockImpl parseBlock(Resource blockResource) {
        ValueMap props = blockResource.getValueMap();
        String content = props.get("content", String.class);

        if (StringUtils.isBlank(content)) {
            return null;
        }

        return new ProseBlockImpl(content);
    }

    @Override
    public List<ProseBlock> getBlocks() {
        return blocks;
    }

    @Override
    public boolean hasBlocks() {
        return blocks != null && !blocks.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return hasBlocks();
    }

    public static class ProseBlockImpl implements ProseBlock {
        private final String content;

        public ProseBlockImpl(String content) {
            this.content = content;
        }

        @Override
        public String getContent() {
            return content;
        }
    }
}

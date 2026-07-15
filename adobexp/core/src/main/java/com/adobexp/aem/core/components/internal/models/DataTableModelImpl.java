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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

import com.adobexp.aem.core.components.models.DataTableModel;

/**
 * Sling Model implementation for the Data Table component.
 * Rows store pipe-delimited cell values for arbitrary column counts.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = DataTableModel.class,
    resourceType = DataTableModelImpl.RESOURCE_TYPE
)
public class DataTableModelImpl implements DataTableModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/data-table";

    private static final String HEADERS_NODE = "headers";
    private static final String ROWS_NODE = "rows";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String withBackground;

    private List<String> headers;
    private List<Row> rows;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            headers = parseHeaders(resource);
            rows = parseRows(resource);
        } else {
            headers = Collections.emptyList();
            rows = Collections.emptyList();
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

    private List<String> parseHeaders(Resource componentResource) {
        List<String> result = new ArrayList<>();
        Resource headersResource = componentResource.getChild(HEADERS_NODE);

        if (headersResource != null) {
            for (Resource headerResource : headersResource.getChildren()) {
                if (headerResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ValueMap props = headerResource.getValueMap();
                String headerText = props.get("headerText", String.class);
                if (StringUtils.isNotBlank(headerText)) {
                    result.add(headerText);
                }
            }
        }

        return result;
    }

    private List<Row> parseRows(Resource componentResource) {
        List<Row> result = new ArrayList<>();
        Resource rowsResource = componentResource.getChild(ROWS_NODE);

        if (rowsResource != null) {
            for (Resource rowResource : rowsResource.getChildren()) {
                if (rowResource.getName().startsWith("jcr:")) {
                    continue;
                }
                ValueMap props = rowResource.getValueMap();
                String cellsRaw = props.get("cells", String.class);
                if (StringUtils.isBlank(cellsRaw)) {
                    continue;
                }
                List<String> cells = Arrays.stream(cellsRaw.split("\\|"))
                    .map(String::trim)
                    .collect(Collectors.toList());
                result.add(new RowImpl(cells));
            }
        }

        return result;
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
    public boolean isWithBackground() {
        return "true".equals(withBackground);
    }

    @Override
    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public List<Row> getRows() {
        return rows;
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || StringUtils.isNotBlank(subtitle)
            || (headers != null && !headers.isEmpty())
            || (rows != null && !rows.isEmpty());
    }

    public static class RowImpl implements Row {
        private final List<String> cells;

        public RowImpl(List<String> cells) {
            this.cells = cells != null ? cells : Collections.emptyList();
        }

        @Override
        public List<String> getCells() {
            return cells;
        }
    }
}

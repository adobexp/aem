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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobexp.aem.core.components.models.FormBuilderModel;

/**
 * Sling Model implementation for the Form Builder component.
 * Reads multifield configurations for request headers and serializes them to JSON.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = FormBuilderModel.class,
    resourceType = FormBuilderModelImpl.RESOURCE_TYPE
)
public class FormBuilderModelImpl implements FormBuilderModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/form-builder";

    private static final String HEADERS_NODE = "requestHeaders";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    private String requestHeadersJson;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            requestHeadersJson = buildHeadersJson(resource);
        } else {
            requestHeadersJson = "";
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

    /**
     * Build a JSON string from the requestHeaders multifield child nodes.
     * Output format: {"Header-Name":"Header-Value","Another-Header":"Value"}
     */
    private String buildHeadersJson(Resource componentResource) {
        Resource headersResource = componentResource.getChild(HEADERS_NODE);
        if (headersResource == null) {
            return "";
        }

        Map<String, String> headers = new LinkedHashMap<>();

        for (Resource headerResource : headersResource.getChildren()) {
            if (headerResource.getName().startsWith("jcr:")) {
                continue;
            }
            ValueMap props = headerResource.getValueMap();
            String key = props.get("headerKey", String.class);
            String value = props.get("headerValue", String.class);

            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                headers.put(key, value);
            }
        }

        if (headers.isEmpty()) {
            return "";
        }

        // Build JSON manually to avoid external dependencies
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(escapeJson(entry.getKey())).append("\"");
            json.append(":");
            json.append("\"").append(escapeJson(entry.getValue())).append("\"");
            first = false;
        }
        json.append("}");

        return json.toString();
    }

    /**
     * Escape special characters for JSON string values.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    @Override
    public String getRequestHeadersJson() {
        return requestHeadersJson;
    }

    @Override
    public boolean hasRequestHeaders() {
        return StringUtils.isNotBlank(requestHeadersJson);
    }
}

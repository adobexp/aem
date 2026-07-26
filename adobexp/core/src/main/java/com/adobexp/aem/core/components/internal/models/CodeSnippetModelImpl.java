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

import com.adobexp.aem.core.components.models.CodeSnippetModel;

/**
 * Sling Model implementation for the Code Snippet component.
 * Each snippet becomes one tab plus one pane, correlated by a stable pane id.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = CodeSnippetModel.class,
    resourceType = CodeSnippetModelImpl.RESOURCE_TYPE
)
public class CodeSnippetModelImpl implements CodeSnippetModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/code-snippet";

    private static final String SNIPPETS_NODE = "snippets";
    private static final String DEFAULT_LANGUAGE = "text";
    private static final String VARIANT_WITH_BG = "code-snippet--with-bg";
    private static final String VARIANT_MUTED = "code-snippet--muted";

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String subtitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String style;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String showLineNumbers;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Code samples")
    private String ariaLabel;

    private List<Snippet> snippets;

    @PostConstruct
    protected void init() {
        Resource resource = getResource();
        if (resource != null) {
            snippets = parseSnippets(resource);
        } else {
            snippets = Collections.emptyList();
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

    private List<Snippet> parseSnippets(Resource componentResource) {
        List<Snippet> items = new ArrayList<>();
        Resource snippetsResource = componentResource.getChild(SNIPPETS_NODE);

        if (snippetsResource != null) {
            String idPrefix = buildIdPrefix(componentResource);
            for (Resource snippetResource : snippetsResource.getChildren()) {
                if (snippetResource.getName().startsWith("jcr:")) {
                    continue;
                }
                SnippetImpl item = parseSnippet(snippetResource, idPrefix, items.isEmpty());
                if (item != null) {
                    items.add(item);
                }
            }
        }

        return items;
    }

    private SnippetImpl parseSnippet(Resource snippetResource, String idPrefix, boolean active) {
        ValueMap props = snippetResource.getValueMap();
        String tabLabel = props.get("tabLabel", String.class);
        String language = props.get("language", String.class);
        String filename = props.get("filename", String.class);
        String code = normaliseCode(props.get("code", String.class));

        if (StringUtils.isBlank(tabLabel) && StringUtils.isBlank(filename) && StringUtils.isBlank(code)) {
            return null;
        }

        String label = tabLabel;
        if (StringUtils.isBlank(label)) {
            label = StringUtils.isNotBlank(filename) ? filename : DEFAULT_LANGUAGE;
        }

        String paneId = idPrefix + sanitiseIdPart(snippetResource.getName());
        String resolvedLanguage = StringUtils.isNotBlank(language) ? language : DEFAULT_LANGUAGE;

        return new SnippetImpl(label, resolvedLanguage, filename, code, paneId, active);
    }

    /**
     * Builds a prefix that is unique per component instance so that two Code Snippet
     * components on the same page never produce colliding pane ids. The resource path
     * is stable, so the ids survive dialog edits.
     */
    private String buildIdPrefix(Resource componentResource) {
        return "cs-" + Integer.toHexString(componentResource.getPath().hashCode()) + "-";
    }

    private static String sanitiseIdPart(String value) {
        return value.replaceAll("[^A-Za-z0-9_-]", "-");
    }

    /**
     * Normalises the authored source: CRLF becomes LF, and blank lines around the
     * snippet are dropped so the line-number gutter stays aligned with the code.
     */
    private static String normaliseCode(String raw) {
        if (raw == null) {
            return null;
        }
        String normalised = raw.replace("\r\n", "\n").replace("\r", "\n");
        return normalised.replaceAll("^\\n+", "").replaceAll("\\n+$", "");
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
    public String getVariant() {
        if (VARIANT_WITH_BG.equals(style) || VARIANT_MUTED.equals(style)) {
            return style;
        }
        return "";
    }

    @Override
    public String getAriaLabel() {
        return ariaLabel;
    }

    @Override
    public boolean isShowLineNumbers() {
        return !"false".equals(showLineNumbers);
    }

    @Override
    public String getLineNumbersAttribute() {
        return isShowLineNumbers() ? null : "false";
    }

    @Override
    public List<Snippet> getSnippets() {
        return snippets != null ? snippets : Collections.emptyList();
    }

    @Override
    public boolean hasSnippets() {
        return snippets != null && !snippets.isEmpty();
    }

    @Override
    public boolean hasFilenames() {
        for (Snippet snippet : getSnippets()) {
            if (StringUtils.isNotBlank(snippet.getFilename())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getActiveFilename() {
        for (Snippet snippet : getSnippets()) {
            if (snippet.isActive()) {
                return snippet.getFilename();
            }
        }
        return null;
    }

    @Override
    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(subtitle) || hasSnippets();
    }

    public static class SnippetImpl implements Snippet {
        private final String tabLabel;
        private final String language;
        private final String filename;
        private final String code;
        private final String paneId;
        private final boolean active;

        public SnippetImpl(String tabLabel, String language, String filename, String code,
                           String paneId, boolean active) {
            this.tabLabel = tabLabel;
            this.language = language;
            this.filename = filename;
            this.code = code;
            this.paneId = paneId;
            this.active = active;
        }

        @Override
        public String getTabLabel() {
            return tabLabel;
        }

        @Override
        public String getLanguage() {
            return language;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getPaneId() {
            return paneId;
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }
}

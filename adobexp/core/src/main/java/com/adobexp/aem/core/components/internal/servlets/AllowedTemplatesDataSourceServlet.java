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
package com.adobexp.aem.core.components.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Datasource servlet that provides a list of allowed templates for the page properties dialog.
 * 
 * It reads the sling:configRef property from the current page hierarchy to determine
 * the configuration path. If not found, it defaults to /conf/adobexp.
 * 
 * Templates are read from:
 * 1. {configPath}/settings/wcm/templates (editable templates)
 * 2. /apps/adobexp/templates (static templates)
 */
@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes=" + AllowedTemplatesDataSourceServlet.RESOURCE_TYPE_V1,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class AllowedTemplatesDataSourceServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AllowedTemplatesDataSourceServlet.class);

    public static final String RESOURCE_TYPE_V1 = "adobexp/components/commons/datasources/allowedtemplates/v1";
    
    private static final String PN_SLING_CONFIG_REF = "sling:configRef";
    private static final String DEFAULT_CONF_PATH = "/conf/adobexp";
    private static final String TEMPLATES_SUBPATH = "/settings/wcm/templates";
    private static final String STATIC_TEMPLATES_PATH = "/apps/adobexp/templates";
    private static final String PN_JCR_TITLE = "jcr:title";
    private static final String JCR_CONTENT = "jcr:content";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        List<Resource> templateResources = getTemplates(request);
        SimpleDataSource dataSource = new SimpleDataSource(templateResources.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }

    /**
     * Gets all available templates based on the page's configuration reference.
     */
    @SuppressWarnings("deprecation")
    private List<Resource> getTemplates(@NotNull SlingHttpServletRequest request) {
        List<Resource> templates = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        
        // Get the content path from the request (page being edited)
        String contentPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
        
        // Determine the configuration path
        String configPath = getConfigPath(resolver, contentPath);
        LOG.debug("Using configuration path: {} for content path: {}", configPath, contentPath);
        
        // Get current allowed templates to mark them as selected
        String[] currentAllowedTemplates = getCurrentAllowedTemplates(resolver, contentPath);
        
        // Add editable templates from conf path
        String editableTemplatesPath = configPath + TEMPLATES_SUBPATH;
        addTemplatesFromPath(resolver, editableTemplatesPath, templates, currentAllowedTemplates);
        
        // Add static templates from apps path
        addTemplatesFromPath(resolver, STATIC_TEMPLATES_PATH, templates, currentAllowedTemplates);
        
        // Add a wildcard option for allowing all templates from the conf path
        String wildcardPath = configPath + TEMPLATES_SUBPATH + "/.*";
        boolean wildcardSelected = isTemplateSelected(wildcardPath, currentAllowedTemplates);
        templates.add(0, new TemplateResource(
                "All templates from " + configPath,
                wildcardPath,
                wildcardSelected,
                resolver
        ));
        
        return templates;
    }

    /**
     * Determines the configuration path based on sling:configRef property.
     * Walks up the page hierarchy to find the property.
     * Falls back to DEFAULT_CONF_PATH if not found.
     */
    @NotNull
    private String getConfigPath(@NotNull ResourceResolver resolver, @Nullable String contentPath) {
        if (StringUtils.isBlank(contentPath)) {
            LOG.debug("Content path is blank, using default config path");
            return DEFAULT_CONF_PATH;
        }

        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.warn("Could not adapt to PageManager, using default config path");
            return DEFAULT_CONF_PATH;
        }

        // Get the page for the content path
        Page page = pageManager.getContainingPage(contentPath);
        if (page == null) {
            LOG.debug("No page found for path: {}, using default config path", contentPath);
            return DEFAULT_CONF_PATH;
        }

        // Walk up the hierarchy to find sling:configRef
        Page currentPage = page;
        while (currentPage != null) {
            ValueMap properties = currentPage.getProperties();
            String configRef = properties.get(PN_SLING_CONFIG_REF, String.class);
            if (StringUtils.isNotBlank(configRef)) {
                LOG.debug("Found sling:configRef '{}' on page: {}", configRef, currentPage.getPath());
                return configRef;
            }
            currentPage = currentPage.getParent();
        }

        LOG.debug("No sling:configRef found in hierarchy, using default config path");
        return DEFAULT_CONF_PATH;
    }

    /**
     * Gets the current allowed templates from the page's jcr:content.
     */
    @Nullable
    private String[] getCurrentAllowedTemplates(@NotNull ResourceResolver resolver, @Nullable String contentPath) {
        if (StringUtils.isBlank(contentPath)) {
            return null;
        }
        
        Resource contentResource = resolver.getResource(contentPath);
        if (contentResource == null) {
            return null;
        }
        
        ValueMap properties = contentResource.getValueMap();
        return properties.get("cq:allowedTemplates", String[].class);
    }

    /**
     * Adds templates from a given path to the list.
     */
    private void addTemplatesFromPath(
            @NotNull ResourceResolver resolver,
            @NotNull String templatesPath,
            @NotNull List<Resource> templates,
            @Nullable String[] currentAllowedTemplates) {
        
        Resource templatesResource = resolver.getResource(templatesPath);
        if (templatesResource == null) {
            LOG.debug("Templates path does not exist: {}", templatesPath);
            return;
        }

        Iterator<Resource> children = templatesResource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            
            // Skip non-template resources (like rep:policy, jcr:content)
            if (child.getName().startsWith("rep:") || child.getName().equals(JCR_CONTENT)) {
                continue;
            }
            
            // Check if it's a template (has jcr:content child or is a cq:Template)
            Resource jcrContent = child.getChild(JCR_CONTENT);
            if (jcrContent == null) {
                continue;
            }
            
            // Get template title
            ValueMap jcrContentProps = jcrContent.getValueMap();
            String title = jcrContentProps.get(PN_JCR_TITLE, child.getName());
            String templatePath = child.getPath();
            
            boolean selected = isTemplateSelected(templatePath, currentAllowedTemplates);
            
            templates.add(new TemplateResource(title, templatePath, selected, resolver));
            LOG.debug("Added template: {} ({})", title, templatePath);
        }
    }

    /**
     * Checks if a template path is in the current allowed templates.
     */
    private boolean isTemplateSelected(@NotNull String templatePath, @Nullable String[] currentAllowedTemplates) {
        if (currentAllowedTemplates == null) {
            return false;
        }
        for (String allowed : currentAllowedTemplates) {
            if (templatePath.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Synthetic resource representing a template option in the dropdown.
     */
    private static class TemplateResource extends TextValueDataResourceSource {

        private final String title;
        private final String path;
        private final boolean selected;

        TemplateResource(String title, String path, boolean selected, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.title = title;
            this.path = path;
            this.selected = selected;
        }

        @Override
        public String getText() {
            return title + " (" + path + ")";
        }

        @Override
        public String getValue() {
            return path;
        }

        @Override
        public boolean getSelected() {
            return selected;
        }
    }
}

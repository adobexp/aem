/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 AdobeXP
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
package com.adobexp.aem.core.components.internal.filters;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.engine.EngineConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.internal.services.DamLanguageRootHelper;

/**
 * Runs on Translation Job <em>Add</em> → Create Language Copy
 * ({@code :operation=ADD_TRANSLATION_PAGES}, {@code createLanguageCopy=true}).
 * Before AEM copies, ensures {@code /content/dam/{tenant}/{lang}} is a language root.
 * After AEM copies (it nests {@code global} because that folder name is not an ISO
 * locale), lifts {@code {lang}/global/cfm/...} to {@code {lang}/cfm/...} and rewrites
 * translation-object {@code sourcePath}.
 */
@Component(
    service = Filter.class,
    property = {
        EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST
    })
@ServiceDescription("AdobeXP DAM language root ensure on Translation Add / Create Language Copy")
@ServiceRanking(5000)
public class TranslationAddDamLanguageRootFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationAddDamLanguageRootFilter.class);

    static final String OPERATION_ADD_TRANSLATION_PAGES = "ADD_TRANSLATION_PAGES";
    static final String OPERATION_GET_RESOURCE_LANGUAGE = "GET_RESOURCE_LANGUAGE";
    static final String PN_COLON_OPERATION = ":operation";
    static final String PN_OPERATION = "operation";
    static final String PN_CREATE_LANGUAGE_COPY = "createLanguageCopy";
    static final String PN_TRANSLATION_PAGE = "translationpage";
    static final String PN_RESOURCE_PATH = "resourcePath";
    static final String PN_TARGET_LANGUAGE = "targetLanguage";
    static final String PN_DESTINATION_LANGUAGE = "destinationLanguage";
    static final String PN_TRANSLATION_JOB_PATH = ":translationJobPath";
    static final String JCR_CONTENT = "jcr:content";

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SlingHttpServletRequest slingRequest = null;
        if (request instanceof SlingHttpServletRequest) {
            slingRequest = (SlingHttpServletRequest) request;
            ensureDamLanguageRoots(slingRequest);
        }
        chain.doFilter(request, response);
        if (slingRequest != null) {
            flattenNestedDamCopies(slingRequest);
        }
    }

    void ensureDamLanguageRoots(@NotNull final SlingHttpServletRequest request) {
        String operation = firstNonBlank(
            request.getParameter(PN_COLON_OPERATION), request.getParameter(PN_OPERATION));
        boolean addPages = OPERATION_ADD_TRANSLATION_PAGES.equals(operation);
        boolean previewLanguage = OPERATION_GET_RESOURCE_LANGUAGE.equals(operation);
        if (!addPages && !previewLanguage) {
            return;
        }
        if (addPages && !Boolean.parseBoolean(request.getParameter(PN_CREATE_LANGUAGE_COPY))) {
            return;
        }
        String[] pages = request.getParameterValues(PN_TRANSLATION_PAGE);
        if (pages == null || pages.length == 0) {
            String resourcePath = request.getParameter(PN_RESOURCE_PATH);
            if (StringUtils.isNotBlank(resourcePath)) {
                pages = new String[] { resourcePath };
            }
        }
        if (pages == null || pages.length == 0) {
            return;
        }
        boolean hasDam = false;
        for (String page : pages) {
            if (page != null && page.startsWith("/content/dam/")) {
                hasDam = true;
                break;
            }
        }
        if (!hasDam) {
            return;
        }
        String targetLanguage = resolveTargetLanguage(request);
        try {
            Set<String> ensured = DamLanguageRootHelper.ensureForTranslationPages(
                request.getResourceResolver(), targetLanguage, pages);
            if (!ensured.isEmpty()) {
                LOG.info("Ensured DAM language roots {} for target {} ({})",
                    ensured, targetLanguage, operation);
            } else {
                LOG.warn("DAM language root was not ensured for operation {} targetLanguage={}",
                    operation, targetLanguage);
            }
        } catch (Exception e) {
            LOG.warn("Failed to ensure DAM language root before Create Language Copy (target={})",
                targetLanguage, e);
        }
    }

    void flattenNestedDamCopies(@NotNull final SlingHttpServletRequest request) {
        String operation = firstNonBlank(
            request.getParameter(PN_COLON_OPERATION), request.getParameter(PN_OPERATION));
        if (!OPERATION_ADD_TRANSLATION_PAGES.equals(operation)
                || !Boolean.parseBoolean(request.getParameter(PN_CREATE_LANGUAGE_COPY))) {
            return;
        }
        String[] pages = damTranslationPages(request);
        if (pages == null) {
            return;
        }
        String targetLanguage = resolveTargetLanguage(request);
        Resource job = translationJobResource(request);
        try {
            Set<String> flattened = DamLanguageRootHelper.flattenAfterLanguageCopy(
                request.getResourceResolver(), targetLanguage, pages, job);
            if (!flattened.isEmpty()) {
                LOG.info("Flattened nested DAM language copies {} for target {}",
                    flattened, targetLanguage);
            }
        } catch (Exception e) {
            LOG.warn("Failed to flatten nested DAM language copies after Create Language Copy (target={})",
                targetLanguage, e);
        }
    }

    @Nullable
    private static String[] damTranslationPages(@NotNull final SlingHttpServletRequest request) {
        String[] pages = request.getParameterValues(PN_TRANSLATION_PAGE);
        if (pages == null || pages.length == 0) {
            String resourcePath = request.getParameter(PN_RESOURCE_PATH);
            if (StringUtils.isNotBlank(resourcePath)) {
                pages = new String[] { resourcePath };
            }
        }
        if (pages == null || pages.length == 0) {
            return null;
        }
        for (String page : pages) {
            if (page != null && page.startsWith("/content/dam/")) {
                return pages;
            }
        }
        return null;
    }

    @Nullable
    private static Resource translationJobResource(@NotNull final SlingHttpServletRequest request) {
        Resource job = request.getResource();
        if (job != null && StringUtils.isNotBlank(languageFromResource(job))) {
            return job;
        }
        String jobPath = request.getParameter(PN_TRANSLATION_JOB_PATH);
        if (StringUtils.isNotBlank(jobPath)) {
            return request.getResourceResolver().getResource(jobPath);
        }
        return job;
    }

    @Nullable
    static String resolveTargetLanguage(@NotNull final SlingHttpServletRequest request) {
        String fromParam = firstNonBlank(
            request.getParameter(PN_TARGET_LANGUAGE),
            request.getParameter(PN_DESTINATION_LANGUAGE));
        if (fromParam != null) {
            return fromParam;
        }
        String fromJob = languageFromResource(request.getResource());
        if (fromJob != null) {
            return fromJob;
        }
        String jobPath = request.getParameter(PN_TRANSLATION_JOB_PATH);
        if (StringUtils.isNotBlank(jobPath)) {
            return languageFromResource(request.getResourceResolver().getResource(jobPath));
        }
        return null;
    }

    @Nullable
    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    private static String languageFromResource(@Nullable final Resource resource) {
        if (resource == null) {
            return null;
        }
        String onNode = languageFromValueMap(resource.getValueMap());
        if (StringUtils.isNotBlank(onNode)) {
            return onNode;
        }
        Resource content = resource.getChild(JCR_CONTENT);
        return content == null ? null : languageFromValueMap(content.getValueMap());
    }

    @Nullable
    private static String languageFromValueMap(@NotNull final ValueMap map) {
        return firstNonBlank(
            map.get(PN_TARGET_LANGUAGE, String.class),
            map.get(PN_DESTINATION_LANGUAGE, String.class));
    }

    @Override
    public void destroy() {
        // no-op
    }
}

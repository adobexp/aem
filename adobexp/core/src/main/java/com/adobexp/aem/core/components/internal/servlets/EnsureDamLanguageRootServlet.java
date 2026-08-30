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
package com.adobexp.aem.core.components.internal.servlets;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.internal.services.DamLanguageRootHelper;

/**
 * Authoring helper used by the Translation Job Add / Create Language Copy hook
 * to create {@code /content/dam/{tenant}/{lang}} as a sibling of {@code global}
 * before AEM copies Content Fragments.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/adobexp/ensure-dam-language-root",
        "sling.servlet.methods=POST"
    })
@ServiceDescription("AdobeXP ensure DAM language-root sibling before translation language copy")
public class EnsureDamLanguageRootServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(EnsureDamLanguageRootServlet.class);

    static final String PN_TARGET_LANGUAGE = "targetLanguage";
    static final String PN_PATH = "path";

    @Override
    protected void doPost(@NotNull final SlingHttpServletRequest request,
                          @NotNull final SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String targetLanguage = request.getParameter(PN_TARGET_LANGUAGE);
        String[] paths = request.getParameterValues(PN_PATH);
        try {
            Set<String> ensured = DamLanguageRootHelper.ensureForTranslationPages(
                request.getResourceResolver(), targetLanguage, paths);
            JSONObject json = new JSONObject();
            json.put("ensured", new JSONArray(ensured));
            json.put("targetLanguage", targetLanguage);
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            LOG.warn("Failed to ensure DAM language root for {}", targetLanguage, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"failed to ensure DAM language root\"}");
        }
    }
}

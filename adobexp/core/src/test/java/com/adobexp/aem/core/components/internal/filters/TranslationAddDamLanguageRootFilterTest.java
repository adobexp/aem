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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class TranslationAddDamLanguageRootFilterTest {

    @Test
    void prefersRequestParameter() {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        when(request.getParameter(TranslationAddDamLanguageRootFilter.PN_TARGET_LANGUAGE))
            .thenReturn("fr_ca");
        assertEquals("fr_ca", TranslationAddDamLanguageRootFilter.resolveTargetLanguage(request));
    }

    @Test
    void readsTargetLanguageFromJobResource() {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        Resource job = mock(Resource.class);
        when(request.getResource()).thenReturn(job);
        when(job.getValueMap()).thenReturn(valueMap("targetLanguage", "fr_ca"));
        assertEquals("fr_ca", TranslationAddDamLanguageRootFilter.resolveTargetLanguage(request));
    }

    @Test
    void readsDestinationLanguageFromJobGadget() {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        Resource job = mock(Resource.class);
        when(request.getResource()).thenReturn(job);
        when(job.getValueMap()).thenReturn(valueMap("destinationLanguage", "fr_ca"));
        assertEquals("fr_ca", TranslationAddDamLanguageRootFilter.resolveTargetLanguage(request));
    }

    @Test
    void readsTargetLanguageFromTranslationJobPathParam() {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        ResourceResolver resolver = mock(ResourceResolver.class);
        Resource job = mock(Resource.class);
        when(request.getResource()).thenReturn(null);
        when(request.getParameter(TranslationAddDamLanguageRootFilter.PN_TRANSLATION_JOB_PATH))
            .thenReturn("/content/projects/vw-translation-v2/jcr:content/dashboard/gadgets/translationjob0");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(
            "/content/projects/vw-translation-v2/jcr:content/dashboard/gadgets/translationjob0"))
            .thenReturn(job);
        when(job.getValueMap()).thenReturn(valueMap());
        Resource content = mock(Resource.class);
        when(job.getChild("jcr:content")).thenReturn(content);
        when(content.getValueMap()).thenReturn(valueMap("destinationLanguage", "de"));
        assertEquals("de", TranslationAddDamLanguageRootFilter.resolveTargetLanguage(request));
    }

    @Test
    void returnsNullWhenTargetLanguageMissing() {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        when(request.getResource()).thenReturn(null);
        when(request.getParameter(TranslationAddDamLanguageRootFilter.PN_TRANSLATION_JOB_PATH)).thenReturn(null);
        assertNull(TranslationAddDamLanguageRootFilter.resolveTargetLanguage(request));
    }

    private static ValueMap valueMap(String... kv) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i], kv[i + 1]);
        }
        return new ValueMapDecorator(map.isEmpty() ? Collections.emptyMap() : map);
    }
}

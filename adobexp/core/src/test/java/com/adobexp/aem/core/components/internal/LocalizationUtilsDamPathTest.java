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
package com.adobexp.aem.core.components.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;

import com.day.cq.wcm.api.Page;

class LocalizationUtilsDamPathTest {

    private static final String GLOBAL_FOLDER = "/content/dam/vw/global/cfm/models";
    private static final String GLOBAL_CF = "/content/dam/vw/global/cfm/models/id4";
    private static final String FR_CA_FOLDER = "/content/dam/vw/fr_ca/cfm/models";
    private static final String FR_CA_CF = "/content/dam/vw/fr_ca/cfm/models/id4";
    private static final String FR_FOLDER = "/content/dam/vw/fr/cfm/models";

    @Test
    void rewriteReplacesGlobalSegmentWithLanguageFolder() {
        assertEquals(FR_CA_CF, LocalizationUtils.rewriteGlobalDamPath(GLOBAL_CF, "fr_ca"));
        assertEquals("/content/dam/vw/fr_ca",
            LocalizationUtils.rewriteGlobalDamPath("/content/dam/vw/global", "fr_ca"));
    }

    @Test
    void prefersLanguageMastersNodeNameOverLocaleLanguage() {
        Page page = page("/content/vw/language-masters/fr_ca/models/new-vehicles", Locale.ENGLISH);
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource(FR_CA_FOLDER)).thenReturn(mock(Resource.class));

        assertEquals(FR_CA_FOLDER,
            LocalizationUtils.localizeDamLanguageCopyPath(GLOBAL_FOLDER, page, resolver));
    }

    @Test
    void usesLocaleLanguageTagWhenPathHasNoLanguageMasters() {
        Page page = page("/content/vw/ca/home", Locale.forLanguageTag("fr-CA"));
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource(FR_CA_FOLDER)).thenReturn(mock(Resource.class));

        assertEquals(FR_CA_FOLDER,
            LocalizationUtils.localizeDamLanguageCopyPath(GLOBAL_FOLDER, page, resolver));
    }

    @Test
    void fallsBackToLanguageOnlyFolderWhenRegionCopyMissing() {
        Page page = page("/content/vw/language-masters/fr_ca/home", Locale.FRENCH);
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource(FR_CA_FOLDER)).thenReturn(null);
        when(resolver.getResource(FR_FOLDER)).thenReturn(mock(Resource.class));

        assertEquals(FR_FOLDER,
            LocalizationUtils.localizeDamLanguageCopyPath(GLOBAL_FOLDER, page, resolver));
    }

    @Test
    void keepsGlobalPathWhenNoLanguageCopyExists() {
        Page page = page("/content/vw/language-masters/fr_ca/home", Locale.FRENCH);
        ResourceResolver resolver = mock(ResourceResolver.class);

        assertEquals(GLOBAL_FOLDER,
            LocalizationUtils.localizeDamLanguageCopyPath(GLOBAL_FOLDER, page, resolver));
    }

    @Test
    void keepsGlobalPathForEnglishLanguageMaster() {
        Page page = page("/content/vw/language-masters/en/home", Locale.ENGLISH);
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource("/content/dam/vw/en/cfm/models")).thenReturn(mock(Resource.class));

        assertEquals(GLOBAL_FOLDER,
            LocalizationUtils.localizeDamLanguageCopyPath(GLOBAL_FOLDER, page, resolver));
    }

    @Test
    void languageCopyFolderCandidatesPutPathBeforeLocale() {
        Page page = page("/content/vw/language-masters/fr_ca/home", Locale.FRENCH);
        List<String> langs = LocalizationUtils.languageCopyFolderCandidates(page);
        assertEquals("fr_ca", langs.get(0));
        assertEquals("fr", langs.get(langs.size() - 1));
    }

    @Test
    void languageMastersFolderNameReadsNodeName() {
        assertEquals("fr_ca",
            LocalizationUtils.languageMastersFolderName("/content/vw/language-masters/fr_ca/home"));
        assertNull(LocalizationUtils.languageMastersFolderName("/content/dam/vw/global/cfm"));
    }

    private static Page page(String path, Locale locale) {
        Page page = mock(Page.class);
        when(page.getPath()).thenReturn(path);
        when(page.getLanguage(false)).thenReturn(locale);
        return page;
    }
}

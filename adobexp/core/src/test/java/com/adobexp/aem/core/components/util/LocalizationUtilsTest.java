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
package com.adobexp.aem.core.components.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;

import com.day.cq.wcm.api.Page;

class LocalizationUtilsTest {

    private static final String EN_PAGE =
        "/content/vw/language-masters/en/models/new-vehicles.html";
    private static final String FR_CA_PAGE_PATH =
        "/content/vw/language-masters/fr_ca/models/new-vehicles";

    @Test
    void rewritesLanguageMastersSegmentToCurrentRoot() {
        assertEquals(
            "/content/vw/language-masters/fr_ca/models/new-vehicles.html",
            LocalizationUtils.rewriteLanguageMastersPath(EN_PAGE, FR_CA_PAGE_PATH));
    }

    @Test
    void rewritesLanguageRootWithHtmlExtension() {
        assertEquals(
            "/content/vw/language-masters/fr_ca.html",
            LocalizationUtils.rewriteLanguageMastersPath(
                "/content/vw/language-masters/en.html", FR_CA_PAGE_PATH));
    }

    @Test
    void leavesPathUnchangedWhenAlreadyInCurrentLanguage() {
        assertEquals(
            "/content/vw/language-masters/fr_ca/models/new-vehicles.html",
            LocalizationUtils.rewriteLanguageMastersPath(
                "/content/vw/language-masters/fr_ca/models/new-vehicles.html",
                FR_CA_PAGE_PATH));
    }

    @Test
    void doesNotRewriteAcrossTenants() {
        assertEquals(
            EN_PAGE,
            LocalizationUtils.rewriteLanguageMastersPath(
                EN_PAGE, "/content/other/language-masters/fr_ca/home"));
    }

    @Test
    void doesNotRewriteXfPathsOntoSitePages() {
        assertEquals(
            "/content/experience-fragments/vw/language-masters/en/site-header/master",
            LocalizationUtils.rewriteLanguageMastersPath(
                "/content/experience-fragments/vw/language-masters/en/site-header/master",
                FR_CA_PAGE_PATH));
    }

    @Test
    void leavesExternalAndFragmentUrlsUnchanged() {
        assertEquals("https://www.vw.com",
            LocalizationUtils.rewriteLanguageMastersPath("https://www.vw.com", FR_CA_PAGE_PATH));
        assertEquals("mailto:info@vw.com",
            LocalizationUtils.rewriteLanguageMastersPath("mailto:info@vw.com", FR_CA_PAGE_PATH));
        assertEquals("#section",
            LocalizationUtils.rewriteLanguageMastersPath("#section", FR_CA_PAGE_PATH));
        assertNull(LocalizationUtils.rewriteLanguageMastersPath(null, FR_CA_PAGE_PATH));
    }

    @Test
    void localizeFallsBackToEnglishWhenTargetPageMissing() {
        Page currentPage = mock(Page.class);
        when(currentPage.getPath()).thenReturn(FR_CA_PAGE_PATH);
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource("/content/vw/language-masters/fr_ca/models/new-vehicles"))
            .thenReturn(null);

        assertEquals(EN_PAGE,
            LocalizationUtils.localizeLanguageMastersPath(EN_PAGE, currentPage, resolver));
    }

    @Test
    void localizeRewritesWhenTargetPageExists() {
        Page currentPage = mock(Page.class);
        when(currentPage.getPath()).thenReturn(FR_CA_PAGE_PATH);
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resolver.getResource("/content/vw/language-masters/fr_ca/models/new-vehicles"))
            .thenReturn(mock(Resource.class));

        assertEquals(
            "/content/vw/language-masters/fr_ca/models/new-vehicles.html",
            LocalizationUtils.localizeLanguageMastersPath(EN_PAGE, currentPage, resolver));
    }
}

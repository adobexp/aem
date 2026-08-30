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
package com.adobexp.aem.core.components.internal.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.commons.jcr.JcrConstants;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class DamLanguageRootHelperTest {

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @BeforeEach
    void setUp() {
        context.create().resource("/content/dam/vw",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/global",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER,
            JcrConstants.JCR_TITLE, "English (Global)");
        context.create().resource("/content/dam/vw/global/jcr:content",
            JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
            DamLanguageRootHelper.PN_IS_LANGUAGE_ROOT, true,
            DamLanguageRootHelper.PN_LANGUAGE, "en",
            DamLanguageRootHelper.PN_CONF, "/conf/vw",
            DamLanguageRootHelper.PN_CLOUD_SERVICE_CONFIGS, new String[] {
                "/conf/global/settings/cloudconfigs/translation/memsource-translation/phrase-connector"
            });
        context.create().resource("/content/dam/vw/global/cfm/models/id4",
            JcrConstants.JCR_PRIMARYTYPE, "dam:Asset");
    }

    @Test
    void createsSiblingLanguageRootWithoutNestingGlobal() throws PersistenceException {
        Set<String> ensured = DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models/id4" });

        assertTrue(ensured.contains("/content/dam/vw/fr_ca"));
        Resource content = context.resourceResolver().getResource("/content/dam/vw/fr_ca/jcr:content");
        assertNotNull(content);
        ValueMap map = content.getValueMap();
        assertEquals(Boolean.TRUE, map.get(DamLanguageRootHelper.PN_IS_LANGUAGE_ROOT, Boolean.class));
        assertEquals("fr_CA", map.get(DamLanguageRootHelper.PN_LANGUAGE, String.class));
        assertEquals("/conf/vw", map.get(DamLanguageRootHelper.PN_CONF, String.class));
        assertNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/global"));
        assertNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/cfm"));
    }

    @Test
    void promotesExistingFolderThatIsNotALanguageRoot() throws PersistenceException {
        context.create().resource("/content/dam/vw/fr_ca",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/jcr:content",
            JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
            JcrConstants.JCR_TITLE, "French leftover");

        DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models" });

        Resource content = context.resourceResolver().getResource("/content/dam/vw/fr_ca/jcr:content");
        assertNotNull(content);
        assertEquals("fr_CA", content.getValueMap().get(DamLanguageRootHelper.PN_LANGUAGE, String.class));
        assertTrue(content.getValueMap().get(DamLanguageRootHelper.PN_IS_LANGUAGE_ROOT, false));
    }

    @Test
    void skipsEnglishTargetAndNonDamPages() throws PersistenceException {
        Set<String> ensured = DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "en",
            new String[] { "/content/dam/vw/global/cfm/models/id4" });
        assertTrue(ensured.isEmpty());

        ensured = DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/vw/language-masters/en/home" });
        assertTrue(ensured.isEmpty());
        assertNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca"));
    }

    @Test
    void folderAndJcrLanguageMapping() {
        assertEquals("fr_ca", DamLanguageRootHelper.toFolderName("fr-CA"));
        assertEquals("fr_CA", DamLanguageRootHelper.toJcrLanguage("fr_ca"));
        assertEquals("de", DamLanguageRootHelper.toJcrLanguage("de"));
        assertEquals("/content/dam/vw/global",
            DamLanguageRootHelper.globalDamRootOf("/content/dam/vw/global/cfm/models/id4"));
        assertNull(DamLanguageRootHelper.globalDamRootOf("/content/dam/vw/fr_ca/cfm/models/id4"));
        assertTrue(DamLanguageRootHelper.isEnglishDamFolder("en"));
        assertFalse(DamLanguageRootHelper.isEnglishDamFolder("fr_ca"));
    }

    @Test
    void flattensNestedGlobalCopyAndRewritesJobSourcePath() throws PersistenceException {
        DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models/id4" });

        context.create().resource("/content/dam/vw/fr_ca/global",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/jcr:content",
            JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
            "cq:isTransCreated", true);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm/models",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm/models/id4",
            JcrConstants.JCR_PRIMARYTYPE, "dam:Asset");

        context.create().resource("/content/projects/job");
        context.create().resource("/content/projects/job/child",
            DamLanguageRootHelper.PN_SOURCE_PATH, "/content/dam/vw/fr_ca/global/cfm/models/id4",
            "addedSourcePath", "/content/dam/vw/global/cfm/models/id4");

        Set<String> flattened = DamLanguageRootHelper.flattenAfterLanguageCopy(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models/id4" },
            context.resourceResolver().getResource("/content/projects/job"));

        assertTrue(flattened.contains("/content/dam/vw/fr_ca"));
        assertNotNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/cfm/models/id4"));
        assertNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/global"));
        assertEquals("/content/dam/vw/fr_ca/cfm/models/id4",
            context.resourceResolver().getResource("/content/projects/job/child")
                .getValueMap().get(DamLanguageRootHelper.PN_SOURCE_PATH, String.class));
        assertEquals("/content/dam/vw/global/cfm/models/id4",
            context.resourceResolver().getResource("/content/projects/job/child")
                .getValueMap().get("addedSourcePath", String.class));
    }

    @Test
    void mergeFlattensWhenCfmAlreadyExistsOnLanguageRoot() throws PersistenceException {
        DamLanguageRootHelper.ensureForTranslationPages(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models/id4" });
        context.create().resource("/content/dam/vw/fr_ca/cfm",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/cfm/models",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm/models",
            JcrConstants.JCR_PRIMARYTYPE, DamLanguageRootHelper.NT_SLING_ORDERED_FOLDER);
        context.create().resource("/content/dam/vw/fr_ca/global/cfm/models/id4",
            JcrConstants.JCR_PRIMARYTYPE, "dam:Asset");

        DamLanguageRootHelper.flattenAfterLanguageCopy(
            context.resourceResolver(),
            "fr_ca",
            new String[] { "/content/dam/vw/global/cfm/models/id4" },
            null);

        assertNotNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/cfm/models/id4"));
        assertNull(context.resourceResolver().getResource("/content/dam/vw/fr_ca/global"));
    }
}

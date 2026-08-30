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
package com.adobexp.aem.core.components.internal;

import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for localization.
 */
public final class LocalizationUtils {

    private static final String DAM_ROOT = "/content/dam/";
    private static final String GLOBAL_SEGMENT = "/global/";
    private static final String GLOBAL_SUFFIX = "/global";
    private static final String LANGUAGE_MASTERS_SEGMENT = "/language-masters/";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private LocalizationUtils() {
        // NOOP
    }

    /**
     * Resolves a DAM path under a {@code /global} language root to the sibling language-copy
     * folder for the current page language (for example {@code /content/dam/vw/global/...}
     * → {@code /content/dam/vw/de/...}). Falls back to {@code damPath} when the page is English,
     * the path is not a global DAM path, or the localized resource does not exist.
     *
     * @param damPath authored DAM path
     * @param currentPage current page (used for language)
     * @param resourceResolver resource resolver
     * @return localized DAM path when it exists, otherwise the authored path
     */
    @Nullable
    public static String localizeDamLanguageCopyPath(@Nullable final String damPath,
                                                     @Nullable final Page currentPage,
                                                     @Nullable final ResourceResolver resourceResolver) {
        if (damPath == null || damPath.isEmpty() || currentPage == null || resourceResolver == null) {
            return damPath;
        }
        if (!damPath.startsWith(DAM_ROOT)) {
            return damPath;
        }
        for (String lang : languageCopyFolderCandidates(currentPage)) {
            if (isEnglishDamFolder(lang)) {
                continue;
            }
            String localized = rewriteGlobalDamPath(damPath, lang);
            if (localized == null || localized.equals(damPath)) {
                continue;
            }
            if (resourceResolver.getResource(localized) != null) {
                return localized;
            }
        }
        return damPath;
    }

    /**
     * Folder names to try when mapping {@code /content/dam/{tenant}/global/...} to a sibling
     * language copy. Prefers the language-masters node name ({@code fr_ca}) over
     * {@link Locale#getLanguage()} ({@code fr}), which drops the region.
     */
    @NotNull
    static List<String> languageCopyFolderCandidates(@NotNull final Page currentPage) {
        Set<String> langs = new LinkedHashSet<>();
        String pathLang = languageMastersFolderName(currentPage.getPath());
        addFolderCandidate(langs, pathLang);
        Locale locale = currentPage.getLanguage(false);
        if (locale != null) {
            addFolderCandidate(langs, locale.toLanguageTag().replace('-', '_'));
            addFolderCandidate(langs, locale.getLanguage());
        }
        return new ArrayList<>(langs);
    }

    private static void addFolderCandidate(@NotNull final Set<String> langs, @Nullable final String raw) {
        if (raw == null || raw.isEmpty()) {
            return;
        }
        String normalized = raw.toLowerCase(Locale.ROOT);
        if (!normalized.isEmpty()) {
            langs.add(normalized);
        }
    }

    @Nullable
    static String languageMastersFolderName(@Nullable final String path) {
        if (path == null) {
            return null;
        }
        int idx = path.indexOf(LANGUAGE_MASTERS_SEGMENT);
        if (idx < 0) {
            return null;
        }
        int start = idx + LANGUAGE_MASTERS_SEGMENT.length();
        int end = path.indexOf('/', start);
        if (end < 0) {
            end = path.length();
        }
        if (end <= start) {
            return null;
        }
        return path.substring(start, end);
    }

    static boolean isEnglishDamFolder(@Nullable final String lang) {
        return lang == null || lang.isEmpty()
            || "en".equalsIgnoreCase(lang)
            || "global".equalsIgnoreCase(lang);
    }

    @Nullable
    static String rewriteGlobalDamPath(@NotNull final String damPath, @NotNull final String language) {
        int globalIdx = damPath.indexOf(GLOBAL_SEGMENT);
        if (globalIdx >= 0) {
            return damPath.substring(0, globalIdx) + "/" + language + "/"
                + damPath.substring(globalIdx + GLOBAL_SEGMENT.length());
        }
        if (damPath.endsWith(GLOBAL_SUFFIX)) {
            return damPath.substring(0, damPath.length() - GLOBAL_SUFFIX.length()) + "/" + language;
        }
        return damPath;
    }

    /**
     * Same as {@link #getLocalPage(Page, Page, ResourceResolver, LanguageManager, LiveRelationshipManager)}, but will
     * also return empty if the referenced page path does not reference a page.
     *
     * @param referencePagePath The path of the referenced page.
     * @param currentPage The current page.
     * @param resourceResolver A resource resolver.
     * @param languageManager The language manager service.
     * @param relationshipManager The live relationship manager service.
     * @return A page, that belongs to the same language or live copy as the current page, and can be used as the local
     * alternative to the referenced page, or empty if no such page exist, or the referenced page path does not point to
     * an existing page.
     */
    public static Optional<Page> getLocalPage(@Nullable final String referencePagePath,
                                         @NotNull final Page currentPage,
                                         @NotNull final ResourceResolver resourceResolver,
                                         @NotNull final LanguageManager languageManager,
                                         @NotNull final LiveRelationshipManager relationshipManager) {
        return Optional.ofNullable(currentPage.getPageManager().getPage(referencePagePath))
            .flatMap(referencePage -> getLocalPage(referencePage, currentPage, resourceResolver, languageManager, relationshipManager));
    }

    /**
     * Given the current requested page and a reference page, this method will determine a page belonging to the
     * current site and locale that can be used instead of the reference site.
     *
     * Specifically, if the reference page and the current page are both found under a language root, and that language
     * root is not the same, then the returned page is the page located under the current page's language root at the
     * same relative path as the reference page is located under it's own language root; or empty if that page does not
     * exist.
     *
     * If either the reference page or the current page are not located under a language root, or if they share the
     * same language root, and if the reference page has a live relationship where the target is the current page or
     * an ancestor of the current page, then the target of that live relationship is returned; or empty if that page
     * does not exist.
     *
     * All other conditions return empty.
     *
     * @param referencePage The referenced page.
     * @param currentPage The current page.
     * @param resourceResolver A resource resolver.
     * @param languageManager The language manager service.
     * @param relationshipManager The live relationship manager service.
     * @return A page, that belongs to the same language or live copy as the current page, and can be used as the local
     * alternative to the referenced page, or empty if no such page exists.
     */
    @SuppressWarnings("unchecked")
    public static Optional<Page> getLocalPage(@NotNull final Page referencePage,
                                              @NotNull final Page currentPage,
                                              @NotNull final ResourceResolver resourceResolver,
                                              @NotNull final LanguageManager languageManager,
                                              @NotNull final LiveRelationshipManager relationshipManager) {
        Page referencePageLanguageRoot = Optional.ofNullable(referencePage.getPath())
            .map(resourceResolver::getResource)
            .map(languageManager::getLanguageRoot)
            .orElse(null);

        Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
        if (referencePageLanguageRoot != null && currentPageLanguageRoot != null && !referencePageLanguageRoot.equals
            (currentPageLanguageRoot)) {
            // check if there's a language copy of the navigation root
            return Optional.ofNullable(
                referencePage.getPageManager().getPage(
                    ResourceUtil.normalize(
                        String.join("/",
                            currentPageLanguageRoot.getPath(),
                            getRelativePath(referencePageLanguageRoot, referencePage)))));
        } else {
            try {
                String currentPagePath = currentPage.getPath() + "/";
                return Optional.of(
                    Optional.ofNullable((Iterator<LiveRelationship>) relationshipManager.getLiveRelationships(referencePage.adaptTo(Resource.class), null, null))
                    .map(liveRelationshipIterator -> StreamSupport.stream(((Iterable<LiveRelationship>) () -> liveRelationshipIterator).spliterator(), false))
                    .orElseGet(Stream::empty)
                    .map(LiveRelationship::getTargetPath)
                    .filter(target -> currentPagePath.startsWith(target + "/"))
                    .map(referencePage.getPageManager()::getPage)
                    .findFirst()
                    .orElse(referencePage));
            } catch (WCMException e) {
                // ignore it
            }
        }
        return Optional.empty();
    }

    /**
     * Get the relative path between the two pages.
     *
     * @param root The root page.
     * @param child The child page.
     * @return The relative path between root and child page, null if child is not a child of root.
     */
    @Nullable
    private static String getRelativePath(@NotNull final Page root, @NotNull final Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

}

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

import java.util.Optional;

import javax.jcr.RangeIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

public class LocalizationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationUtils.class);

    private static final String LANGUAGE_MASTERS_SEGMENT = "/language-masters/";
    private static final String EXPERIENCE_FRAGMENTS_SEGMENT = "/experience-fragments/";
    private static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    private static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    /**
     * Resolves the site page that is being rendered when a header/footer is included from an
     * Experience Fragment. Walks the component context, then the original include URI.
     */
    @Nullable
    public static Page resolveSitePage(@Nullable SlingHttpServletRequest request,
                                       @Nullable Resource resource,
                                       @Nullable PageManager pageManager) {
        PageManager pm = pageManager;
        if (pm == null && request != null) {
            pm = request.getResourceResolver().adaptTo(PageManager.class);
        }
        if (pm == null && resource != null) {
            pm = resource.getResourceResolver().adaptTo(PageManager.class);
        }
        if (pm == null) {
            return null;
        }

        if (request != null) {
            Object ctxAttr = request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME);
            ComponentContext ctx = ctxAttr instanceof ComponentContext ? (ComponentContext) ctxAttr : null;
            while (ctx != null) {
                Page page = ctx.getPage();
                if (isSitePage(page)) {
                    return page;
                }
                ctx = ctx.getParent();
            }

            Page fromOuterUri = containingPage(pm, outerRequestPath(request));
            if (isSitePage(fromOuterUri)) {
                return fromOuterUri;
            }

            Page fromRequestResource = pm.getContainingPage(request.getResource());
            if (isSitePage(fromRequestResource)) {
                return fromRequestResource;
            }
        }

        if (resource != null) {
            return pm.getContainingPage(resource);
        }
        return null;
    }

    /**
     * Rewrites an internal {@code /content/{tenant}/language-masters/{lang}/...} path to the
     * language root of {@code currentPage} when that localized page exists. Leaves external
     * URLs and unmatched paths unchanged. Used so Phrase XF copies can keep English path
     * fields while the rendered header/footer stay in the current language.
     */
    @Nullable
    public static String localizeLanguageMastersPath(@Nullable String path,
                                                    @Nullable Page currentPage,
                                                    @Nullable ResourceResolver resolver) {
        if (isExternalOrEmpty(path) || currentPage == null) {
            return path;
        }
        String rewritten = rewriteLanguageMastersPath(path, currentPage.getPath());
        if (rewritten == null || rewritten.equals(path)) {
            return path;
        }
        if (resolver == null) {
            return rewritten;
        }
        return resolver.getResource(stripSelectorsAndExtension(rewritten)) != null ? rewritten : path;
    }

    /**
     * Path rewrite without a repository existence check. Package-visible for unit tests.
     */
    @Nullable
    static String rewriteLanguageMastersPath(@Nullable String path, @Nullable String currentPagePath) {
        if (isExternalOrEmpty(path) || StringUtils.isBlank(currentPagePath)) {
            return path;
        }
        String currentRoot = languageMastersRoot(currentPagePath);
        String authoredRoot = languageMastersRoot(stripSelectorsAndExtension(path));
        if (currentRoot == null || authoredRoot == null || authoredRoot.equals(currentRoot)) {
            return path;
        }
        String authoredParent = StringUtils.substringBeforeLast(authoredRoot, "/");
        String currentParent = StringUtils.substringBeforeLast(currentRoot, "/");
        if (!StringUtils.equals(authoredParent, currentParent)) {
            return path;
        }
        int rootAt = path.indexOf(authoredRoot);
        if (rootAt < 0) {
            return path;
        }
        return path.substring(0, rootAt) + currentRoot + path.substring(rootAt + authoredRoot.length());
    }

    static String languageMastersRoot(@Nullable String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        int idx = path.indexOf(LANGUAGE_MASTERS_SEGMENT);
        if (idx < 0) {
            return null;
        }
        int langStart = idx + LANGUAGE_MASTERS_SEGMENT.length();
        int langEnd = path.indexOf('/', langStart);
        if (langEnd < 0) {
            return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        }
        return path.substring(0, langEnd);
    }

    private static boolean isSitePage(@Nullable Page page) {
        return page != null && !StringUtils.contains(page.getPath(), EXPERIENCE_FRAGMENTS_SEGMENT);
    }

    private static boolean isExternalOrEmpty(@Nullable String path) {
        if (StringUtils.isBlank(path)) {
            return true;
        }
        String trimmed = path.trim();
        return trimmed.startsWith("http://")
            || trimmed.startsWith("https://")
            || trimmed.startsWith("//")
            || trimmed.startsWith("mailto:")
            || trimmed.startsWith("tel:")
            || trimmed.startsWith("#");
    }

    @Nullable
    private static String outerRequestPath(@NotNull SlingHttpServletRequest request) {
        String uri = (String) request.getAttribute(INCLUDE_SERVLET_PATH);
        if (StringUtils.isBlank(uri)) {
            uri = (String) request.getAttribute(INCLUDE_REQUEST_URI);
        }
        if (StringUtils.isBlank(uri)) {
            uri = request.getRequestURI();
        }
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        String ctx = request.getContextPath();
        if (StringUtils.isNotBlank(ctx) && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        int query = uri.indexOf('?');
        if (query >= 0) {
            uri = uri.substring(0, query);
        }
        return stripSelectorsAndExtension(uri);
    }

    @Nullable
    private static Page containingPage(@NotNull PageManager pageManager, @Nullable String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return pageManager.getContainingPage(path);
    }

    static String stripSelectorsAndExtension(@Nullable String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        int lastSlash = path.lastIndexOf('/');
        String parent = lastSlash >= 0 ? path.substring(0, lastSlash + 1) : "";
        String name = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        int dot = name.indexOf('.');
        if (dot < 0) {
            return path;
        }
        return parent + name.substring(0, dot);
    }

    /**
     * Returns the localization root of the given resource.
     * <pre>
     * Use case                                  | Resource Path                        | Root
     * ------------------------------------------|--------------------------------------|------------------
     * 1. No localization                        | /content/mysite/mypage               | null
     * 2. Language localization                  | /content/mysite/en/mypage            | /content/mysite/en
     * 3. Country-language localization          | /content/mysite/us/en/mypage         | /content/mysite/us/en
     * 4. Country-language localization (variant)| /content/us/mysite/en/mypage         | /content/us/mysite/en
     * 5. Blueprint                              | /content/mysite/blueprint/mypage     | /content/mysite/blueprint
     * 6. Live Copy                              | /content/mysite/livecopy/mypage      | /content/mysite/livecopy
     * </pre>
     *
     * @param resource the resource for which we want to find the localization root
     * @param resolver the resource resolver
     * @param languageManager the language manager service
     * @param relationshipManager the live relationship manager service
     * @return the localization root of the resource at the given path if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLocalizationRoot(@NotNull Resource resource, @NotNull ResourceResolver resolver,
        @NotNull LanguageManager languageManager, @NotNull LiveRelationshipManager relationshipManager) {
        String root = getLanguageRoot(resource, languageManager);
        if (StringUtils.isEmpty(root)) {
            root = getBlueprintPath(resource, relationshipManager);
        }
        if (StringUtils.isEmpty(root)) {
            root = getLiveCopyPath(resource, relationshipManager);
        }
        return root;
    }

    /**
     * Returns the language root of the resource.
     *
     * @param resource the resource
     * @param languageManager the language manager service
     * @return the language root of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLanguageRoot(@NotNull Resource resource, @NotNull LanguageManager languageManager) {
        return Optional.ofNullable(languageManager.getLanguageRoot(resource, true))
            .map(Page::getPath)
            .orElse(null);
    }

    /**
     * Returns the path of the blueprint of the resource.
     *
     * @param resource the resource
     * @param relationshipManager the live relationship manager service
     * @return the path of the blueprint of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getBlueprintPath(@NotNull Resource resource, @NotNull LiveRelationshipManager relationshipManager) {
        try {
            if (relationshipManager.isSource(resource)) {
                // the resource is a blueprint
                RangeIterator liveCopiesIterator = relationshipManager.getLiveRelationships(resource, null, null);
                if (liveCopiesIterator != null && liveCopiesIterator.hasNext()) {
                    LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                    LiveCopy liveCopy = relationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getBlueprintPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the blueprint: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the path of the live copy of the resource.
     *
     * @param resource the resource
     * @param relationshipManager the live relationship manager service
     * @return the path of the live copy of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLiveCopyPath(@NotNull Resource resource, @NotNull LiveRelationshipManager relationshipManager) {
        try {
            if (relationshipManager.hasLiveRelationship(resource)) {
                // the resource is a live copy
                LiveRelationship liveRelationship = relationshipManager.getLiveRelationship(resource, false);
                if (liveRelationship != null) {
                    LiveCopy liveCopy = liveRelationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the live copy: {}", e.getMessage());
        }
        return null;
    }
}

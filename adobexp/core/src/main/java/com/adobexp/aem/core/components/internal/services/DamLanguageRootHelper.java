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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

/**
 * Prepares DAM language copies for Translation Job Create Language Copy.
 * AEM does not treat a folder named {@code global} as an ISO locale, so it copies
 * {@code /content/dam/{tenant}/global/...} to {@code /content/dam/{tenant}/{lang}/global/...}
 * even when {@code {lang}} is already a language root. This helper:
 * <ol>
 *   <li>creates {@code /content/dam/{tenant}/{lang}} as a real language root first</li>
 *   <li>after AEM copies, lifts nested {@code {lang}/global/*} up to {@code {lang}/*}</li>
 *   <li>rewrites translation-object {@code sourcePath} to the flattened location</li>
 * </ol>
 */
public final class DamLanguageRootHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DamLanguageRootHelper.class);

    static final String PN_IS_LANGUAGE_ROOT = "cq:isLanguageRoot";
    static final String PN_LANGUAGE = "jcr:language";
    static final String PN_CONF = "cq:conf";
    static final String PN_CLOUD_SERVICE_CONFIGS = "cq:cloudserviceconfigs";
    static final String PN_SOURCE_PATH = "sourcePath";
    static final String MIX_LANGUAGE = "mix:language";
    static final String NT_SLING_ORDERED_FOLDER = "sling:OrderedFolder";
    static final String NT_SLING_FOLDER = "sling:Folder";
    static final String JCR_CONTENT = "jcr:content";

    private static final Pattern GLOBAL_DAM_ROOT =
        Pattern.compile("^(/content/dam/[^/]+)/global(?:/|$)");

    private DamLanguageRootHelper() {
        // NOOP
    }

    /**
     * Creates or promotes {@code /content/dam/{tenant}/{lang}} language roots for any
     * {@code /content/dam/{tenant}/global/...} paths in {@code translationPages}.
     *
     * @return paths of language roots that were created or updated
     */
    @NotNull
    public static Set<String> ensureForTranslationPages(@Nullable final ResourceResolver resolver,
                                                        @Nullable final String targetLanguage,
                                                        @Nullable final String[] translationPages)
            throws PersistenceException {
        Set<String> ensured = new LinkedHashSet<>();
        if (resolver == null || translationPages == null || translationPages.length == 0) {
            return ensured;
        }
        String folderName = toFolderName(targetLanguage);
        if (StringUtils.isBlank(folderName) || isEnglishDamFolder(folderName)) {
            return ensured;
        }
        Set<String> globalRoots = new LinkedHashSet<>();
        for (String page : translationPages) {
            String globalRoot = globalDamRootOf(page);
            if (globalRoot != null) {
                globalRoots.add(globalRoot);
            }
        }
        for (String globalRoot : globalRoots) {
            String created = ensureSiblingLanguageRoot(resolver, globalRoot, folderName);
            if (created != null) {
                ensured.add(created);
            }
        }
        if (!ensured.isEmpty() && resolver.hasChanges()) {
            resolver.commit();
        }
        return ensured;
    }

    /**
     * After AEM Create Language Copy, move nested {@code {lang}/global/...} content
     * to {@code {lang}/...} and point the translation job at the flattened paths.
     *
     * @return language-root paths that had a nested {@code global} folder flattened
     */
    @NotNull
    public static Set<String> flattenAfterLanguageCopy(@Nullable final ResourceResolver resolver,
                                                       @Nullable final String targetLanguage,
                                                       @Nullable final String[] translationPages,
                                                       @Nullable final Resource translationJob)
            throws PersistenceException {
        Set<String> flattened = new LinkedHashSet<>();
        if (resolver == null || translationPages == null || translationPages.length == 0) {
            return flattened;
        }
        String folderName = toFolderName(targetLanguage);
        if (StringUtils.isBlank(folderName) || isEnglishDamFolder(folderName)) {
            return flattened;
        }
        try {
            resolver.refresh();
        } catch (RuntimeException e) {
            LOG.debug("Could not refresh resolver before flattening DAM language copies", e);
        }
        Set<String> languageRoots = new LinkedHashSet<>();
        for (String page : translationPages) {
            String globalRoot = globalDamRootOf(page);
            if (globalRoot != null) {
                languageRoots.add(siblingLanguageRootPath(globalRoot, folderName));
            }
        }
        for (String languageRoot : languageRoots) {
            if (flattenNestedGlobal(resolver, languageRoot)) {
                flattened.add(languageRoot);
            }
        }
        if (translationJob != null) {
            int rewritten = rewriteJobSourcePaths(translationJob, folderName);
            if (rewritten > 0) {
                LOG.info("Rewrote {} translation sourcePath values off nested /{}/global/",
                    rewritten, folderName);
            }
        }
        if (resolver.hasChanges()) {
            resolver.commit();
        }
        return flattened;
    }

    @Nullable
    static String ensureSiblingLanguageRoot(@NotNull final ResourceResolver resolver,
                                            @NotNull final String globalDamRoot,
                                            @NotNull final String folderName)
            throws PersistenceException {
        Resource global = resolver.getResource(globalDamRoot);
        if (global == null) {
            LOG.warn("DAM global language root {} is missing; cannot ensure sibling {}",
                globalDamRoot, folderName);
            return null;
        }
        Resource parent = global.getParent();
        if (parent == null) {
            return null;
        }
        String targetPath = parent.getPath() + "/" + folderName;
        Resource globalContent = global.getChild(JCR_CONTENT);
        String jcrLanguage = toJcrLanguage(folderName);
        String title = languageTitle(folderName);

        Resource folder = resolver.getResource(targetPath);
        if (folder == null) {
            Map<String, Object> folderProps = new HashMap<>();
            folderProps.put(JcrConstants.JCR_PRIMARYTYPE, NT_SLING_ORDERED_FOLDER);
            folderProps.put("sling:resourceType", NT_SLING_ORDERED_FOLDER);
            folderProps.put(JcrConstants.JCR_TITLE, title);
            folder = ResourceUtil.getOrCreateResource(
                resolver, targetPath, folderProps, NT_SLING_ORDERED_FOLDER, false);
            LOG.info("Created DAM language root folder {}", targetPath);
        }

        Resource content = folder.getChild(JCR_CONTENT);
        if (content == null) {
            Map<String, Object> contentProps = languageRootContentProperties(globalContent, jcrLanguage, title);
            content = ResourceUtil.getOrCreateResource(
                resolver, targetPath + "/" + JCR_CONTENT, contentProps, JcrConstants.NT_UNSTRUCTURED, false);
        } else if (!isLanguageRoot(content, jcrLanguage)) {
            ModifiableValueMap map = content.adaptTo(ModifiableValueMap.class);
            if (map != null) {
                applyLanguageRootProperties(map, globalContent, jcrLanguage, title);
                LOG.info("Promoted existing DAM folder {} to a language root ({})", targetPath, jcrLanguage);
            }
        }

        addLanguageMixin(content);
        return targetPath;
    }

    @NotNull
    static Map<String, Object> languageRootContentProperties(@Nullable final Resource globalContent,
                                                             @NotNull final String jcrLanguage,
                                                             @NotNull final String title) {
        Map<String, Object> contentProps = new HashMap<>();
        contentProps.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        applyLanguageRootProperties(contentProps, globalContent, jcrLanguage, title);
        return contentProps;
    }

    private static void applyLanguageRootProperties(@NotNull final Map<String, Object> target,
                                                    @Nullable final Resource globalContent,
                                                    @NotNull final String jcrLanguage,
                                                    @NotNull final String title) {
        target.put(PN_IS_LANGUAGE_ROOT, true);
        target.put(PN_LANGUAGE, jcrLanguage);
        target.put(JcrConstants.JCR_TITLE, title);
        if (globalContent == null) {
            return;
        }
        ValueMap source = globalContent.getValueMap();
        Object conf = source.get(PN_CONF);
        if (conf != null) {
            target.put(PN_CONF, conf);
        }
        Object cloudConfigs = source.get(PN_CLOUD_SERVICE_CONFIGS);
        if (cloudConfigs != null) {
            target.put(PN_CLOUD_SERVICE_CONFIGS, cloudConfigs);
        }
    }

    static boolean isLanguageRoot(@Nullable final Resource content, @NotNull final String jcrLanguage) {
        if (content == null) {
            return false;
        }
        ValueMap map = content.getValueMap();
        if (!Boolean.TRUE.equals(map.get(PN_IS_LANGUAGE_ROOT, Boolean.class))) {
            return false;
        }
        String existing = map.get(PN_LANGUAGE, String.class);
        return existing != null && existing.replace('-', '_').equalsIgnoreCase(jcrLanguage);
    }

    private static void addLanguageMixin(@Nullable final Resource content) {
        if (content == null) {
            return;
        }
        Node node = content.adaptTo(Node.class);
        if (node == null) {
            return;
        }
        try {
            if (!node.isNodeType(MIX_LANGUAGE)) {
                node.addMixin(MIX_LANGUAGE);
            }
        } catch (RepositoryException e) {
            LOG.debug("Could not add {} mixin on {}: {}", MIX_LANGUAGE, content.getPath(), e.getMessage());
        }
    }

    @Nullable
    static String globalDamRootOf(@Nullable final String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        Matcher matcher = GLOBAL_DAM_ROOT.matcher(path);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1) + "/global";
    }

    @NotNull
    static String siblingLanguageRootPath(@NotNull final String globalDamRoot,
                                          @NotNull final String folderName) {
        int slash = globalDamRoot.lastIndexOf('/');
        return globalDamRoot.substring(0, slash) + "/" + folderName;
    }

    static boolean flattenNestedGlobal(@NotNull final ResourceResolver resolver,
                                       @NotNull final String languageRootPath)
            throws PersistenceException {
        Resource nested = resolver.getResource(languageRootPath + "/global");
        if (nested == null) {
            return false;
        }
        List<Resource> children = snapshotChildren(nested);
        for (Resource child : children) {
            if (JCR_CONTENT.equals(child.getName())) {
                continue;
            }
            mergeOrMove(resolver, child, languageRootPath + "/" + child.getName());
        }
        Resource leftover = resolver.getResource(languageRootPath + "/global");
        if (leftover != null) {
            resolver.delete(leftover);
        }
        LOG.info("Flattened nested DAM copy {}/global into {}", languageRootPath, languageRootPath);
        return true;
    }

    private static void mergeOrMove(@NotNull final ResourceResolver resolver,
                                    @NotNull final Resource src,
                                    @NotNull final String destPath)
            throws PersistenceException {
        Resource dest = resolver.getResource(destPath);
        if (dest == null) {
            moveResource(resolver, src.getPath(), destPath);
            return;
        }
        if (!isFolder(src)) {
            return;
        }
        for (Resource child : snapshotChildren(src)) {
            if (JCR_CONTENT.equals(child.getName())) {
                continue;
            }
            mergeOrMove(resolver, child, destPath + "/" + child.getName());
        }
    }

    private static void moveResource(@NotNull final ResourceResolver resolver,
                                     @NotNull final String from,
                                     @NotNull final String to)
            throws PersistenceException {
        Resource src = resolver.getResource(from);
        if (src == null) {
            return;
        }
        Node node = src.adaptTo(Node.class);
        if (node != null) {
            try {
                node.getSession().move(from, to);
                return;
            } catch (RepositoryException e) {
                throw new PersistenceException("Failed to move " + from + " to " + to, e);
            }
        }
        resolver.move(from, to);
    }

    private static boolean isFolder(@NotNull final Resource resource) {
        String type = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
        return NT_SLING_ORDERED_FOLDER.equals(type)
            || NT_SLING_FOLDER.equals(type)
            || "nt:folder".equals(type);
    }

    @NotNull
    private static List<Resource> snapshotChildren(@NotNull final Resource parent) {
        List<Resource> children = new ArrayList<>();
        for (Resource child : parent.getChildren()) {
            children.add(child);
        }
        return children;
    }

    static int rewriteJobSourcePaths(@Nullable final Resource job,
                                     @NotNull final String folderName) {
        if (job == null) {
            return 0;
        }
        return rewriteSourcePaths(job, "/" + folderName + "/global/", "/" + folderName + "/");
    }

    private static int rewriteSourcePaths(@NotNull final Resource resource,
                                          @NotNull final String nested,
                                          @NotNull final String flat) {
        int count = 0;
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            String sourcePath = map.get(PN_SOURCE_PATH, String.class);
            if (sourcePath != null && sourcePath.contains(nested)) {
                map.put(PN_SOURCE_PATH, sourcePath.replace(nested, flat));
                count++;
            }
        }
        for (Resource child : resource.getChildren()) {
            count += rewriteSourcePaths(child, nested, flat);
        }
        return count;
    }

    @Nullable
    static String toFolderName(@Nullable final String targetLanguage) {
        if (StringUtils.isBlank(targetLanguage)) {
            return null;
        }
        String normalized = targetLanguage.trim().replace('-', '_').toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    @NotNull
    static String toJcrLanguage(@NotNull final String folderName) {
        String[] parts = folderName.split("_", 2);
        if (parts.length == 1 || StringUtils.isBlank(parts[1])) {
            return parts[0].toLowerCase(Locale.ROOT);
        }
        return parts[0].toLowerCase(Locale.ROOT) + "_" + parts[1].toUpperCase(Locale.ROOT);
    }

    static boolean isEnglishDamFolder(@Nullable final String folderName) {
        return folderName == null
            || "en".equalsIgnoreCase(folderName)
            || "global".equalsIgnoreCase(folderName);
    }

    @NotNull
    static String languageTitle(@NotNull final String folderName) {
        String tag = folderName.replace('_', '-');
        Locale locale = Locale.forLanguageTag(tag);
        if (locale == null || StringUtils.isBlank(locale.getLanguage())) {
            return folderName;
        }
        String display = locale.getDisplayName(Locale.ENGLISH);
        return StringUtils.isBlank(display) ? folderName : display;
    }
}

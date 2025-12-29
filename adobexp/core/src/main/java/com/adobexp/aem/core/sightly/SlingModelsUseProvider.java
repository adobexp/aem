/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025
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
package com.adobexp.aem.core.sightly;

import javax.script.Bindings;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.scripting.sightly.render.RenderContext;
import org.apache.sling.scripting.sightly.use.ProviderOutcome;
import org.apache.sling.scripting.sightly.use.UseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Custom HTL UseProvider that forces Sling Models adaptation for {@code com.adobexp.*} identifiers.
 *
 * Why: this local AEM instance doesn't have a dedicated "Sling Models UseProvider" bundle installed, so HTL falls
 * back to {@code JavaUseProvider} (service.ranking=90) and tries to instantiate interfaces like
 * {@code com.adobexp.aem.core.components.models.Page}, which fails.
 *
 * This provider runs before JavaUseProvider and creates Sling Models via {@link ModelFactory#createModel(Object, Class)}.
 */
@Component(
        service = UseProvider.class,
        property = {
                // Higher than org.apache.sling.scripting.sightly.impl.engine.extension.use.JavaUseProvider (90)
                "service.ranking:Integer=200"
        }
)
public class SlingModelsUseProvider implements UseProvider {

    private static final String SUPPORTED_PREFIX = "com.adobexp.";
    private static final String PAGE_MODEL_FQCN = "com.adobexp.aem.core.components.models.Page";
    private static final String PAGE_IMPL_FQCN = "com.adobexp.aem.core.components.internal.models.PageImpl";

    private static final Logger LOG = LoggerFactory.getLogger(SlingModelsUseProvider.class);

    @Reference
    private ModelFactory modelFactory;

    @Override
    public ProviderOutcome provide(String identifier, RenderContext renderContext, Bindings arguments) {
        if (identifier == null || !identifier.startsWith(SUPPORTED_PREFIX)) {
            return ProviderOutcome.failure();
        }

        // UseRuntimeExtension passes only the provided arguments map (often empty) as "arguments".
        // The Sling request is available via the renderContext bindings.
        final Bindings ctxBindings = renderContext != null ? renderContext.getBindings() : null;
        Object requestObj = ctxBindings != null
                ? (ctxBindings.containsKey("request") ? ctxBindings.get("request") : ctxBindings.get("slingRequest"))
                : null;
        if (!(requestObj instanceof SlingHttpServletRequest) && ctxBindings != null) {
            // Be robust across AEM/Sling versions: find the request by scanning bindings values.
            for (Object v : ctxBindings.values()) {
                if (v instanceof SlingHttpServletRequest) {
                    requestObj = v;
                    break;
                }
            }
        }
        if (!(requestObj instanceof SlingHttpServletRequest)) {
            if (PAGE_MODEL_FQCN.equals(identifier)) {
                LOG.warn("HTL UseProvider could not find SlingHttpServletRequest in RenderContext bindings for {}", identifier);
            }
            return ProviderOutcome.failure();
        }
        SlingHttpServletRequest request = (SlingHttpServletRequest) requestObj;
        // HTL/WCM rendering can wrap the request (e.g. OnDemandReaderRequest, WCM ForwardRequestWrapper).
        // Sling Models resolution in this AEM build behaves better with the underlying request instance.
        if (request instanceof SlingHttpServletRequestWrapper) {
            request = ((SlingHttpServletRequestWrapper) request).getSlingRequest();
        }
        while (request instanceof ServletRequestWrapper) {
            ServletRequest inner = ((ServletRequestWrapper) request).getRequest();
            if (inner instanceof SlingHttpServletRequest) {
                request = (SlingHttpServletRequest) inner;
            } else {
                break;
            }
        }
        final Resource reqResource = request.getResource();

        final Class<?> targetClass;
        try {
            targetClass = this.getClass().getClassLoader().loadClass(identifier);
        } catch (ClassNotFoundException e) {
            if (PAGE_MODEL_FQCN.equals(identifier)) {
                LOG.warn("HTL UseProvider could not load class {}", identifier, e);
            }
            return ProviderOutcome.failure(e);
        }

        try {
            // Special-case Page interface: in some rendering contexts Sling Models cannot resolve
            // "interface -> implementation" mapping for request wrappers (e.g. OnDemandReaderRequest).
            // We can still return the correct model instance by creating the concrete implementation.
            if (PAGE_MODEL_FQCN.equals(identifier)) {
                try {
                    Class<?> pageImplClass = this.getClass().getClassLoader().loadClass(PAGE_IMPL_FQCN);
                    Object pageModel = request.adaptTo(pageImplClass);
                    if (pageModel == null && modelFactory != null) {
                        @SuppressWarnings({ "rawtypes", "unchecked" })
                        Object created = modelFactory.createModel(request, (Class) pageImplClass);
                        pageModel = created;
                    }
                    if (pageModel != null) {
                        return ProviderOutcome.success(pageModel);
                    }
                } catch (Throwable e) {
                    // fall through to the generic path below (will log with context)
                    LOG.warn("Page impl fallback failed for {} (resource={}, resourceType={})",
                            identifier,
                            reqResource != null ? reqResource.getPath() : null,
                            reqResource != null ? reqResource.getResourceType() : null,
                            e);
                }
            }

            // Important: ModelFactory.canCreateFromAdaptable(...) may return false for adapter interfaces
            // (e.g. com.adobexp...models.Page). Using adaptTo(...) triggers Sling Models adapter resolution.
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Object model = request.adaptTo((Class) targetClass);
            if (model == null && modelFactory != null) {
                // Try explicit Sling Models creation as a fallback (more deterministic in some contexts).
                @SuppressWarnings({ "rawtypes", "unchecked" })
                Object created = modelFactory.createModel(request, (Class) targetClass);
                model = created;
            }

            if (PAGE_MODEL_FQCN.equals(identifier) && model == null) {
                LOG.warn("HTL UseProvider could not adapt request to {} (resource={}, resourceType={})",
                        identifier,
                        reqResource != null ? reqResource.getPath() : null,
                        reqResource != null ? reqResource.getResourceType() : null);
            }
            return ProviderOutcome.notNullOrFailure(model);
        } catch (Throwable e) {
            if (PAGE_MODEL_FQCN.equals(identifier)) {
                LOG.warn("HTL UseProvider failed creating model {} (resource={}, resourceType={})",
                        identifier,
                        reqResource != null ? reqResource.getPath() : null,
                        reqResource != null ? reqResource.getResourceType() : null,
                        e);
            }
            return ProviderOutcome.failure(e);
        }
    }
}



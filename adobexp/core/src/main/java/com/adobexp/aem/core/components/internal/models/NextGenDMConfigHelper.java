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
package com.adobexp.aem.core.components.internal.models;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Helper class to dynamically access NextGenDynamicMediaConfig service using reflection.
 * This avoids direct class dependencies on internal Adobe APIs that may not be available
 * in all AEM SDK versions.
 */
public final class NextGenDMConfigHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NextGenDMConfigHelper.class);
    private static final String NGDM_CONFIG_CLASS = "com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig";

    private final Object configService;

    private NextGenDMConfigHelper(Object configService) {
        this.configService = configService;
    }

    /**
     * Attempts to get the NextGenDynamicMediaConfig service from OSGi.
     * @return a helper wrapping the config, or null if not available
     */
    public static NextGenDMConfigHelper getConfig() {
        try {
            BundleContext bundleContext = FrameworkUtil.getBundle(NextGenDMConfigHelper.class).getBundleContext();
            if (bundleContext == null) {
                return null;
            }
            ServiceReference<?> serviceRef = bundleContext.getServiceReference(NGDM_CONFIG_CLASS);
            if (serviceRef == null) {
                LOGGER.debug("NextGenDynamicMediaConfig service not available");
                return null;
            }
            Object service = bundleContext.getService(serviceRef);
            if (service != null) {
                return new NextGenDMConfigHelper(service);
            }
        } catch (Exception e) {
            LOGGER.debug("Unable to get NextGenDynamicMediaConfig service: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Checks if NGDM is enabled and properly configured.
     */
    public boolean isEnabled() {
        try {
            Boolean enabled = invokeMethod("enabled", Boolean.class);
            String repositoryId = getRepositoryId();
            return Boolean.TRUE.equals(enabled) && StringUtils.isNotBlank(repositoryId);
        } catch (Exception e) {
            LOGGER.debug("Error checking NGDM enabled status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the repository ID.
     */
    public String getRepositoryId() {
        return invokeMethod("getRepositoryId", String.class);
    }

    /**
     * Gets the image delivery base path.
     */
    public String getImageDeliveryBasePath() {
        return invokeMethod("getImageDeliveryBasePath", String.class);
    }

    /**
     * Gets the asset metadata path.
     */
    public String getAssetMetadataPath() {
        return invokeMethod("getAssetMetadataPath", String.class);
    }

    private <T> T invokeMethod(String methodName, Class<T> returnType) {
        if (configService == null) {
            return null;
        }
        try {
            Method method = configService.getClass().getMethod(methodName);
            Object result = method.invoke(configService);
            return returnType.cast(result);
        } catch (Exception e) {
            LOGGER.debug("Error invoking {} on NGDM config: {}", methodName, e.getMessage());
            return null;
        }
    }
}

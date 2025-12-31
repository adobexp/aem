/*
 *  Copyright 2024 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobexp.aem.core.components.internal.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobexp.aem.core.components.models.VideoModel;

/**
 * Sling Model implementation for the Video component.
 * Reads video configuration from the component properties.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = VideoModel.class,
    resourceType = VideoModelImpl.RESOURCE_TYPE
)
public class VideoModelImpl implements VideoModel {

    protected static final String RESOURCE_TYPE = "adobexp/components/content/video";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String videoPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String videoTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String videoDescription;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String videoHref;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String openInNewTab;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String showPlayToggle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "true")
    private String showMuteToggle;

    @Override
    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public String getVideoTitle() {
        return videoTitle;
    }

    @Override
    public String getVideoDescription() {
        return videoDescription;
    }

    @Override
    public String getVideoHref() {
        return videoHref;
    }

    @Override
    public boolean isOpenInNewTab() {
        return "true".equals(openInNewTab);
    }

    @Override
    public boolean isShowPlayToggle() {
        return !"false".equals(showPlayToggle);
    }

    @Override
    public boolean isShowMuteToggle() {
        return !"false".equals(showMuteToggle);
    }
}


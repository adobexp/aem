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
package com.adobexp.aem.core.components.models;

/**
 * Sling Model interface for the Video component.
 * Provides access to video configuration including video source, teaser overlay settings,
 * and control button visibility options.
 */
public interface VideoModel {

    /**
     * Gets the video path from DAM.
     * @return video path
     */
    String getVideoPath();

    /**
     * Gets the video title for the teaser overlay.
     * @return video title
     */
    String getVideoTitle();

    /**
     * Gets the video description for the teaser overlay.
     * @return video description
     */
    String getVideoDescription();

    /**
     * Gets the teaser link URL (content path or absolute URL).
     * @return teaser link URL
     */
    String getVideoHref();

    /**
     * Checks if the teaser link should open in a new tab.
     * @return true if should open in new tab
     */
    boolean isOpenInNewTab();

    /**
     * Checks if the play/pause toggle button should be shown.
     * @return true if play toggle should be shown
     */
    boolean isShowPlayToggle();

    /**
     * Checks if the mute/unmute toggle button should be shown.
     * @return true if mute toggle should be shown
     */
    boolean isShowMuteToggle();
}


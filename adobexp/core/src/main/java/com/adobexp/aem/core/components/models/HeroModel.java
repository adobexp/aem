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

package com.adobexp.aem.core.components.models;

/**
 * Sling Model interface for the Hero component.
 */
public interface HeroModel {

    String getTitle();

    String getSubtitle();

    String getLogoDark();

    String getLogoDarkAlt();

    String getLogoLight();

    String getLogoLightAlt();

    boolean hasLogo();

    String getPrimaryCtaText();

    String getPrimaryCtaLink();

    boolean isPrimaryCtaExternal();

    boolean hasPrimaryCta();

    String getSecondaryCtaText();

    String getSecondaryCtaLink();

    boolean isSecondaryCtaExternal();

    boolean hasSecondaryCta();

    /**
     * Whether to apply the CA-configured hero background color to the section.
     */
    boolean isWithBackground();

    boolean hasContent();
}

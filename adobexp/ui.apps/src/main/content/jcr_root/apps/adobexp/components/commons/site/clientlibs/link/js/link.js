(function() {
    "use strict";

    var linkAccessibilityClass = "cmp-link__screen-reader-only";
    var selectors = {
        linkAccessibility: "." + linkAccessibilityClass,
        linkAccessibilityEnabled: "[data-cmp-link-accessibility-enabled]",
        linkAccessibilityText: "[data-cmp-link-accessibility-text]"
    };

    function getLinkAccessibilityText() {
        var linkAccessibilityEnabled = document.querySelectorAll(selectors.linkAccessibilityEnabled);
        if (!linkAccessibilityEnabled[0]) {
            return;
        }
        var linkAccessibilityTextElements = document.querySelectorAll(selectors.linkAccessibilityText);
        if (!linkAccessibilityTextElements[0]) {
            return;
        }
        return linkAccessibilityTextElements[0].dataset.cmpLinkAccessibilityText;
    }

    function onDocumentReady() {
        var linkAccessibilityText = getLinkAccessibilityText();
        if (linkAccessibilityText) {
            document.querySelectorAll("a[target='_blank']").forEach(function(link) {
                if (!link.querySelector(selectors.linkAccessibility)) {
                    var linkAccessibilityElement = document.createElement("span");
                    linkAccessibilityElement.classList.add(linkAccessibilityClass);
                    linkAccessibilityElement.innerText = linkAccessibilityText;
                    link.insertAdjacentElement("beforeend", linkAccessibilityElement);
                }
            });
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());

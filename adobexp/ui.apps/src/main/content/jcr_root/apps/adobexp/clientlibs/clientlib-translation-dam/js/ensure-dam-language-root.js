(function(document, Granite, $) {
    "use strict";

    var ENSURE_URL = "/bin/adobexp/ensure-dam-language-root";

    function targetLanguageFromPage() {
        return $("#cq-project-translation-job-target-language").val()
            || $("#cq-project-translation-job-destination-language").val()
            || "";
    }

    function damPathsFromAjaxData(data) {
        var encoded = typeof data === "string" ? data : $.param(data || {});
        var paths = [];
        encoded.replace(/translationpage=([^&]*)/g, function(_, value) {
            try {
                paths.push(decodeURIComponent(value.replace(/\+/g, " ")));
            } catch (ignore) {
                paths.push(value);
            }
        });
        encoded.replace(/resourcePath=([^&]*)/g, function(_, value) {
            try {
                paths.push(decodeURIComponent(value.replace(/\+/g, " ")));
            } catch (ignore) {
                paths.push(value);
            }
        });
        return paths.filter(function(path) {
            return path && path.indexOf("/content/dam/") === 0;
        });
    }

    function shouldEnsure(data) {
        var encoded = typeof data === "string" ? data : $.param(data || {});
        return encoded.indexOf("ADD_TRANSLATION_PAGES") >= 0
            || encoded.indexOf("GET_RESOURCE_LANGUAGE") >= 0
            || encoded.indexOf("createLanguageCopy=true") >= 0;
    }

    function ensureDamLanguageRoot(paths, targetLanguage) {
        if (!paths.length || !targetLanguage) {
            return;
        }
        $.ajax({
            url: Granite.HTTP.externalize(ENSURE_URL),
            type: "post",
            async: false,
            data: {
                targetLanguage: targetLanguage,
                path: paths
            }
        });
    }

    var originalAjax = $.ajax;
    $.ajax = function(url, options) {
        var settings = options;
        if (typeof url === "object") {
            settings = url;
        } else {
            settings = $.extend({}, options || {}, { url: url });
        }
        if (settings && shouldEnsure(settings.data)) {
            ensureDamLanguageRoot(damPathsFromAjaxData(settings.data), targetLanguageFromPage());
        }
        return originalAjax.apply(this, arguments);
    };
})(document, Granite, Granite.$);

"use strict";

use(function() {
	var urlRewriterService = sling.getService(Packages.com.adobexp.aem.core.components.config.UrlRewriterConfig);
	var value = this.propValue;
	var propType = "String";
	var isWcmModeDisabled = !(wcmmode == "EDIT" || wcmmode == "PREVIEW");
	var requestUri = request.getRequestURI();
	if (this.propValue) {

		if (this.propValue.constructor === String) {
			if (value.startsWith("/content/")) {
				var hashIndex = value.indexOf('#');
				var queryIndex = value.indexOf('?');
				var suffixStart = -1;
				if (hashIndex >= 0 && queryIndex >= 0) {
					suffixStart = Math.min(hashIndex, queryIndex);
				} else if (hashIndex >= 0) {
					suffixStart = hashIndex;
				} else if (queryIndex >= 0) {
					suffixStart = queryIndex;
				}
				var pathPart = suffixStart >= 0 ? value.substring(0, suffixStart) : value;
				var suffix = suffixStart >= 0 ? value.substring(suffixStart) : '';
				var pagePath = pathPart;
				if (pagePath.length > 5 && pagePath.substring(pagePath.length - 5) === '.html') {
					pagePath = pagePath.substring(0, pagePath.length - 5);
				}
				if (null != pageManager.getPage(pagePath)) {
					value = resolver.map(pagePath) + '';
					if (this.propName.indexOf("JsonUrl") == -1
							&& !(value.length > 5 && value.substring(value.length - 5) === '.html')) {
						value = value + '.html';
					}
					value = value + suffix;
				}
			}
			value = value.replace(/[\n\r]+/g, '');
		} else if (this.propValue.constructor === Boolean || this.propValue.constructor === Number) {
			propType = "Boolean";
		} else if (this.propValue.constructor === Array) {
			propType = "Array";
		}
		return {
			validKey: !this.propName.startsWith("jcr:"),
			value: urlRewriterService.getPublishUrl(value, isWcmModeDisabled, requestUri),
			valueType: propType
		};
	} else {
		return {
			validKey: !this.propName.startsWith("jcr:"),
			value: value,
			valueType: propType
		};
	}
});

"use strict";

use(function() {
	var urlRewriterService = sling.getService(Packages.com.adobexp.aem.core.components.config.UrlRewriterConfig);
	var value = this.propValue;
	var propType = "String";
	if (this.propValue) {

		if (this.propValue.constructor === String) {
			if (value.startsWith("/content/") && null != pageManager.getPage(value)) {
				value = resolver.map(value) + '';
				if (this.propName.indexOf("JsonUrl") == -1) {
					value = value + '.html';
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
			value: urlRewriterService.getPublishUrl(value, !(wcmmode == "EDIT" || wcmmode == "PREVIEW")),
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
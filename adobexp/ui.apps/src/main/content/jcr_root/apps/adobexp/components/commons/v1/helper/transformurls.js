"use strict";

use(function() {
	var urlRewriterService = sling.getService(Packages.com.adobexp.aem.core.components.config.UrlRewriterConfig);
	if (this.internalUrl) {
		return urlRewriterService.getPublishUrl(this.internalUrl, !(wcmmode == "EDIT" || wcmmode == "PREVIEW"));
	} else {
		return this.internalUrl;
	}
});
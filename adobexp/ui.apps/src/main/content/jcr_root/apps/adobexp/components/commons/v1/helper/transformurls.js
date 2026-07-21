"use strict";

use(function() {
	var urlRewriterService = sling.getService(Packages.com.adobexp.aem.core.components.config.UrlRewriterConfig);
	if (this.internalUrl) {
		var isWcmModeDisabled = !(wcmmode == "EDIT" || wcmmode == "PREVIEW");
		var requestUri = request.getRequestURI();
		return urlRewriterService.getPublishUrl(this.internalUrl, isWcmModeDisabled, requestUri);
	} else {
		return this.internalUrl;
	}
});

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
package com.adobexp.aem.core.components.internal.filters;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.config.UrlRewriterConfig;

/**
 * Runs before Sling resource resolution (HTTP whiteboard) and converts external
 * short URLs (/en, /static, /images, …) to AEM internal paths using
 * {@link UrlRewriterConfig}.
 */
@Component(
		service = Filter.class,
		property = {
				"osgi.http.whiteboard.filter.regex=^/(en|static|images)(/|\\.|$).*",
				"osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=*)"
		})
@ServiceDescription("AdobeXP URL Rewriter inbound filter (external → internal)")
@ServiceRanking(10000)
public class UrlRewriterInboundFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(UrlRewriterInboundFilter.class);

	@Reference
	private UrlRewriterConfig urlRewriterConfig;

	@Override
	public void init(FilterConfig filterConfig) {
		// no-op
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || urlRewriterConfig == null) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String uri = httpRequest.getRequestURI();
		Map<String, String> mapping = urlRewriterConfig.getUrlRewriteMapping();
		if (mapping == null || mapping.isEmpty() || StringUtils.isEmpty(uri)) {
			chain.doFilter(request, response);
			return;
		}

		String internal = urlRewriterConfig.getInternalUrl(uri);
		if (StringUtils.isEmpty(internal) || internal.equals(uri)) {
			chain.doFilter(request, response);
			return;
		}

		String query = httpRequest.getQueryString();
		String target = query == null ? internal : internal + "?" + query;
		LOG.debug("Inbound rewrite {} -> {}", uri, target);
		httpRequest.getRequestDispatcher(target).forward(httpRequest, response);
	}

	@Override
	public void destroy() {
		// no-op
	}
}

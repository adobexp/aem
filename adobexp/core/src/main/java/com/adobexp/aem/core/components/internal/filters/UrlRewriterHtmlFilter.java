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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobexp.aem.core.components.config.UrlRewriterConfig;
import com.day.cq.wcm.api.WCMMode;

/**
 * When a page is rendered under {@code /content} (publish / wcmmode=disabled),
 * rewrites internal URL prefixes in the HTML to the external short URLs from
 * {@link UrlRewriterConfig} — including clientlib paths that bypass Sightly helpers.
 */
@Component(
		service = Filter.class,
		property = {
				EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST
		})
@ServiceDescription("AdobeXP URL Rewriter HTML filter (internal → external in markup)")
@ServiceRanking(-500)
public class UrlRewriterHtmlFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(UrlRewriterHtmlFilter.class);

	/**
	 * Blocks where whitespace is significant and must not be compacted.
	 */
	private static final Pattern PROTECTED_BLOCKS = Pattern.compile(
			"(?is)(<script\\b[^>]*>.*?</script>)|(<style\\b[^>]*>.*?</style>)|(<pre\\b[^>]*>.*?</pre>)|(<textarea\\b[^>]*>.*?</textarea>)");

	@Reference
	private UrlRewriterConfig urlRewriterConfig;

	@Override
	public void init(FilterConfig filterConfig) {
		// no-op
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof SlingHttpServletRequest) || !(response instanceof HttpServletResponse)
				|| urlRewriterConfig == null) {
			chain.doFilter(request, response);
			return;
		}

		SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		WCMMode mode = WCMMode.fromRequest(slingRequest);
		boolean wcmDisabled = !(mode == WCMMode.EDIT || mode == WCMMode.PREVIEW || mode == WCMMode.DESIGN);
		String uri = slingRequest.getRequestURI();
		if (!urlRewriterConfig.shouldRewriteForRequest(uri, wcmDisabled)
				|| !uri.startsWith("/content/")) {
			chain.doFilter(request, response);
			return;
		}

		Map<String, String> mapping = urlRewriterConfig.getUrlRewriteMapping();

		BufferingResponseWrapper wrapped = new BufferingResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, wrapped);

		String contentType = wrapped.getContentType();
		byte[] body = wrapped.getBody();
		if (body.length == 0 || contentType == null || !contentType.toLowerCase().contains("text/html")) {
			if (body.length > 0) {
				response.getOutputStream().write(body);
			}
			return;
		}

		Charset charset = StandardCharsets.UTF_8;
		if (contentType.toLowerCase().contains("charset=")) {
			try {
				String cs = contentType.substring(contentType.toLowerCase().indexOf("charset=") + 8).trim();
				int semi = cs.indexOf(';');
				if (semi >= 0) {
					cs = cs.substring(0, semi).trim();
				}
				charset = Charset.forName(cs);
			} catch (Exception e) {
				charset = StandardCharsets.UTF_8;
			}
		}

		String html = new String(body, charset);
		if (mapping != null && !mapping.isEmpty()) {
			List<Map.Entry<String, String>> entries = new ArrayList<>(mapping.entrySet());
			entries.sort(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getKey().length()).reversed());
			for (Map.Entry<String, String> entry : entries) {
				if (StringUtils.isNotEmpty(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue())) {
					html = html.replace(entry.getKey(), entry.getValue());
				}
			}
		}

		html = compactHtmlWhitespace(html);

		byte[] rewritten = html.getBytes(charset);
		response.setContentLength(rewritten.length);
		response.getOutputStream().write(rewritten);
		LOG.debug("Compacted HTML (and rewrote URL prefixes if configured) for {}", uri);
	}

	/**
	 * Removes HTL-generated indentation and blank lines outside
	 * {@code script}/{@code style}/{@code pre}/{@code textarea}.
	 * Does not fully minify (keeps one newline between tags) to avoid
	 * breaking significant inter-element whitespace in edge cases.
	 */
	static String compactHtmlWhitespace(String html) {
		if (StringUtils.isEmpty(html)) {
			return html;
		}
		Matcher matcher = PROTECTED_BLOCKS.matcher(html);
		StringBuffer sb = new StringBuffer(html.length());
		int last = 0;
		while (matcher.find()) {
			sb.append(compactMarkupSegment(html.substring(last, matcher.start())));
			sb.append(matcher.group());
			last = matcher.end();
		}
		sb.append(compactMarkupSegment(html.substring(last)));
		return sb.toString();
	}

	private static String compactMarkupSegment(String segment) {
		if (segment.isEmpty()) {
			return segment;
		}
		String[] lines = segment.split("\\R", -1);
		StringBuilder out = new StringBuilder(segment.length());
		boolean previousWasBlank = true;
		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				if (!previousWasBlank) {
					// skip blank lines entirely
					previousWasBlank = true;
				}
				continue;
			}
			if (out.length() > 0) {
				out.append('\n');
			}
			out.append(trimmed);
			previousWasBlank = false;
		}
		return out.toString();
	}

	@Override
	public void destroy() {
		// no-op
	}

	private static final class BufferingResponseWrapper extends HttpServletResponseWrapper {
		private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(16 * 1024);
		private ServletOutputStream outputStream;
		private PrintWriter writer;

		BufferingResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		byte[] getBody() {
			try {
				if (writer != null) {
					writer.flush();
				}
				if (outputStream != null) {
					outputStream.flush();
				}
			} catch (IOException e) {
				// ignore
			}
			return buffer.toByteArray();
		}

		@Override
		public ServletOutputStream getOutputStream() {
			if (writer != null) {
				throw new IllegalStateException("getWriter() already called");
			}
			if (outputStream == null) {
				outputStream = new ServletOutputStream() {
					@Override
					public boolean isReady() {
						return true;
					}

					@Override
					public void setWriteListener(WriteListener writeListener) {
						// no-op
					}

					@Override
					public void write(int b) {
						buffer.write(b);
					}
				};
			}
			return outputStream;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (outputStream != null) {
				throw new IllegalStateException("getOutputStream() already called");
			}
			if (writer == null) {
				String enc = getCharacterEncoding();
				Charset charset = enc != null ? Charset.forName(enc) : StandardCharsets.UTF_8;
				writer = new PrintWriter(new OutputStreamWriter(buffer, charset));
			}
			return writer;
		}

		@Override
		public void flushBuffer() throws IOException {
			if (writer != null) {
				writer.flush();
			}
			if (outputStream != null) {
				outputStream.flush();
			}
		}
	}
}

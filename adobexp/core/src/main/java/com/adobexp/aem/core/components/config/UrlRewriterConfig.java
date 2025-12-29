package com.adobexp.aem.core.components.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true, service = { UrlRewriterConfig.class })
@ServiceDescription(com.adobexp.aem.core.components.config.UrlRewriterConfig.SERVICE_DESC)
@Designate(ocd = UrlRewriterConfig.Config.class)
public class UrlRewriterConfig {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static final String SERVICE_NAME = "AdobeXP Components | URL Rewriter Configs";
	public static final String SERVICE_DESC = "AdobeXP Components | URL Rewriter Configurations.";
	
	
	private String[] urlsToBeReWritten;
	private String[] urlsToBeSkipped;
	
	private Map<String,String> urlRewriteMapping;
	private List<String> urlSkipMapping;

	@ObjectClassDefinition(name = SERVICE_NAME, description = SERVICE_DESC)
	public static @interface Config {
		@AttributeDefinition(name = "List of URLs to be rewritten. Format : {\"internalUrl\":\"/content/dam/\",\"externalUrl\":\"/static/assets\"}")
		String[] urls_to_be_re_written();
		
		@AttributeDefinition(name = "List of URLs to be skipped. like /content/dam etc.")
		String[] urls_to_be_skipped();
		
	}
	
	@Activate
	private void activate(BundleContext context, Config config) {
		log.info("activate(context, config) | PARAMS | context = {} , config = {}", context, config);
		urlRewriteMapping = new HashMap<String, String>();
		urlSkipMapping = new ArrayList<String>();

		this.urlsToBeReWritten = config.urls_to_be_re_written();
		this.urlsToBeSkipped = config.urls_to_be_skipped();
		log.info("activate(context, config) | REFERENCE | " + "\n\t urlsToBeReWritten = {} " + "\n\t urlsToBeSkipped = {} ", Arrays.deepToString(urlsToBeReWritten), Arrays.deepToString(urlsToBeSkipped));
		for (String entry : urlsToBeReWritten) {
			if (StringUtils.isNotEmpty(entry)) {
				try {
					@SuppressWarnings("deprecation")
					JSONObject jsonObject = new JSONObject(entry);
					urlRewriteMapping.put(jsonObject.getString("internalUrl"), jsonObject.getString("externalUrl"));
				} catch (@SuppressWarnings("deprecation") JSONException e) {
					log.error("activate(context, config) | urlsToBeReWritten | JSONException detected | entry = {}, Exception = {}", entry, e);
					e.printStackTrace();
				}
			} else {
				log.info("activate(context, config) | urlsToBeReWritten | entry = {}", entry);
			}
		}
		urlSkipMapping = Arrays.asList(urlsToBeSkipped);
		log.info("activate(context, config) | FINAL VALUES | " + "\n\t urlRewriteMapping = {} " + "\n\t urlSkipMapping = {} ", urlRewriteMapping, urlSkipMapping);
	}
	
	public String getPublishUrl(String internalPath, boolean isWcmModeDisabled) {
		String returningURL = null;
		log.debug("getPublishUrl(internalPath,isWcmModeDisabled) | internalPath = {} | isWcmModeDisabled = {}", internalPath, isWcmModeDisabled);
		if (isWcmModeDisabled) {
			if (isUrlToBeSkipped(internalPath)) {
				returningURL = internalPath;
			} else {
				Set<String> keySet = urlRewriteMapping.keySet();
				for (String entry: keySet) {
					if (internalPath.startsWith(entry)) {
						String strippedMatchedPrefix = internalPath.substring(entry.length());
						returningURL = urlRewriteMapping.get(entry) + strippedMatchedPrefix;
						break;
					}
				}
				if (returningURL == null) {
					returningURL = internalPath;
				}
			}
		} else {
			returningURL = internalPath;
		}
		log.debug("getPublishUrl(internalPath,isWcmModeDisabled) | Transformed = {} -> {}", internalPath, returningURL);
		return returningURL;
	}
	
	private boolean isUrlToBeSkipped(String internalPath) {
		if (!StringUtils.isEmpty(internalPath)) {
			for (String entry: urlSkipMapping) {
				if (internalPath.startsWith(entry)) {
					return true;
				}
			}
		}
		return false;
	}

	@Deactivate
	private void deactivate() {
		log.info("deactivate() | FINISHED");
	}

}


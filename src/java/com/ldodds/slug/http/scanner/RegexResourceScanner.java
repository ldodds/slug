package com.ldodds.slug.http.scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.vocabulary.CONFIG;

public class RegexResourceScanner extends ResourceScanner {

	protected Pattern getAcceptance() {
		return acceptance;
	}

	protected String getReplacementRegex() {
		return replacementRegex;
	}

	protected String getReplacementValue() {
		return replacementValue;
	}

	private Pattern acceptance = null;
	private String replacementRegex = null;
	private String replacementValue = null;
	
	@Override
	protected URL createURL(String url) {
		if (replacementRegex == null || replacementValue == null) {
			return super.createURL(url);
		}
		String newURL = url.replaceFirst(replacementRegex, replacementValue);
		try {
			return new URL(newURL);
		} catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "Unable to create url from " + url, e);
			
		}
		return null;
	}

	@Override
	protected boolean isAcceptable(String uri) {
		getLogger().log(Level.FINEST, "Acceptance test for url: " + uri);
		if ( acceptance == null ) {
			return super.isAcceptable(uri);	
		}
		return acceptance.matcher(uri).find();
	}

	@Override
	protected boolean doConfig(Resource self) {
		
		if ( self.hasProperty(CONFIG.filter) ) {
    		String regex = self.getProperty(CONFIG.filter).getString();
    		getLogger().config("Scan regex: " + regex);
    		acceptance = Pattern.compile(regex);
		}

		if ( self.hasProperty(CONFIG.replacementRegex) ) {
			replacementRegex = self.getProperty(CONFIG.replacementRegex).getString();
			getLogger().config("Replacement regex: " + replacementRegex);
		}
		
		if ( self.hasProperty(CONFIG.replacementValue) ) {
			replacementValue = self.getProperty(CONFIG.replacementValue).getString();
			getLogger().config("Replacement value: " + replacementValue);
		}
		return super.doConfig(self);
	}

	
}

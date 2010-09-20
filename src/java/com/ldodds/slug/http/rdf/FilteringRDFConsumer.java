package com.ldodds.slug.http.rdf;

import java.util.logging.Level;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.vocabulary.CONFIG;

public abstract class FilteringRDFConsumer extends AbstractRDFConsumer {

	private Pattern acceptance = null;
	
	@Override
	protected boolean canProcess(URLTask task, Response response) {
		if (isAcceptable( task.getURL().toString() ) ) {
			return true;
		}
		return false;
	}

	protected boolean isAcceptable(String uri) {
		if (acceptance == null) {			
			return true;
		}
		getLogger().log(Level.FINEST, "Acceptance test for url: " + uri);
		return acceptance.matcher(uri).find();
	}
	
	@Override
	protected boolean doConfig(Resource self) {
		if ( self.hasProperty(CONFIG.filter) ) {
    		String regex = self.getProperty(CONFIG.filter).getString();
    		getLogger().config("Filter regex: " + regex);
    		acceptance = Pattern.compile(regex);
		}

		return super.doConfig(self);
	}

	
}

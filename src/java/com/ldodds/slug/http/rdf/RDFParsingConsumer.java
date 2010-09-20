package com.ldodds.slug.http.rdf;

import com.hp.hpl.jena.rdf.model.*;

import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;

/**
 * Implementation of the Consumer interface that provides support for parsing 
 * RDF/XML fetched during the crawl.
 * 
 * @author ldodds
 * @see com.ldodds.slug.http.scanner.Scanner
 * TODO: move out behaviour that can rely on context
 */
public class RDFParsingConsumer extends AbstractRDFConsumer {


	@Override
	protected boolean canProcess(URLTask task, Response response) {
		return "application/rdf+xml".equals( response.getContentTypeWithoutCharset() );
	}
	
	@Override
	protected RDFReader getReader(Response response, Model model) {
		return model.getReader();
	}

}

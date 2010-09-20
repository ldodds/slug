package com.ldodds.slug.http.scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Finds all HTTP Resource URIs in a Jena model
 * 
 * @author ldodds
 *
 */
public class ResourceScanner extends ScannerImpl {

	@Override
	protected Set<URL> scan(Model toScan, URL origin) {
		
		Set<URL> found = new HashSet<URL>(5);
		
		addAcceptableResourcesToSet(found, toScan.listObjects() );
		addAcceptableResourcesToSet(found, toScan.listSubjects() );
		return found;
	}

	/**
	 * @param urls
	 * @param iterator
	 */
	private void addAcceptableResourcesToSet(Set<URL> urls,
			Iterator iterator) {
		while ( iterator.hasNext() ) {

			RDFNode node = (RDFNode)iterator.next();
			if ( node.isResource() && !node.isAnon() ) {
				String uri = ((Resource)node).getURI();
				if ( isAcceptable(uri) ) {
					URL url = createURL(uri);
					if (url != null) {
						urls.add( url );
					}
				}
				
			}
		}
	}

	protected URL createURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "Unable to create url from " + url, e);
		}
		return null;
	}

	protected boolean isAcceptable(String uri) {
		return uri.startsWith("http");
	}

}

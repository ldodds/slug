package com.ldodds.slug.http.scanner;

import java.net.URL;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Implementations of this interface provide behaviour to scan a Model for 
 * find, filter, and/or generate URLs to be used to drive the crawling 
 * process.
 * 
 * @author ldodds
 *
 */
public interface Scanner {

	Set<URL> findURLs(Model toScan, URL origin);
}

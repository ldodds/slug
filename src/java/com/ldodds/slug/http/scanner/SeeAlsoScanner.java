package com.ldodds.slug.http.scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SeeAlsoScanner extends ScannerImpl {

	private static Logger LOGGER = Logger.getLogger(SeeAlsoScanner.class.getName());
	
	@Override
	protected Set<URL> scan(Model toScan, URL origin) {
		return getSeeAlsos(toScan, new HashSet<URL>() );
	}

	/**
	 * Retrieve all rdfs:seeAlso links from a Model, and add each link to 
	 * the provided set as a URL object.
	 * 
	 * @param model the model to scan
	 * @param set the Set to which URLs will be added
	 * @return the provided set
	 */
	public static Set<URL> getSeeAlsos(Model model, Set<URL> set)
	{
		NodeIterator iter = model.listObjectsOfProperty(RDFS.seeAlso);
		while (iter.hasNext())
		{
			RDFNode node = iter.nextNode();
			String seeAlso = node.toString(); 
			try
			{
				set.add( new URL(seeAlso) );
			} catch (MalformedURLException e)
			{
				LOGGER.log(Level.WARNING, "Unable to build URL", e);
			}
					
		}
		return set;				
	}	
}

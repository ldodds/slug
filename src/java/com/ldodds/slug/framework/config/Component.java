package com.ldodds.slug.framework.config;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Tagging interface to mark classes capable of being 
 * used within the Slug Configuration Framework
 */
public interface Component 
{
	/**
	 * Requests that the component instance configure itself 
	 * based on properties associated with a given RDF resource.
	 * 
	 * Method should only be called once in a Component's life 
	 * cycle.
	 * 
	 * @param self Reference to the Jena Resource from which the 
	 * Component was instantiated.
	 */
	void configure(Resource self);
	
	boolean isConfigured();
}

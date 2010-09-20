package com.ldodds.slug.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author ldodds
 *
 */
public class SCUTTERVOCAB
{
	private static Model m_model = ModelFactory.createDefaultModel();
    
	public static final String NS = "http://purl.org/net/scutter/";
    
	public static String getURI() {return NS;}
    
	public static final Resource NAMESPACE = m_model.createResource( NS );            
    
	public static final Resource Representation = m_model.createResource( NS + "Representation");
	public static final Resource Reason = m_model.createResource( NS + "Reason");
	public static final Resource Fetch = m_model.createResource( NS + "Fetch");
	
	public static final Property skip = m_model.createProperty( NS + "skip" );
	public static final Property fetch = m_model.createProperty( NS + "fetch" );
	public static final Property latestFetch = m_model.createProperty( NS + "latestFetch" );	
	public static final Property source = m_model.createProperty( NS + "source" );
	public static final Property origin = m_model.createProperty( NS + "origin" );
	public static final Property status = m_model.createProperty( NS + "status" );
	public static final Property etag = m_model.createProperty( NS + "etag" );
	public static final Property lastModified = m_model.createProperty( NS + "lastModified" );
	public static final Property contentType = m_model.createProperty( NS + "contentType" );
	public static final Property rawTripleCount = m_model.createProperty( NS + "rawTripleCount" );
	public static final Property localCopy = m_model.createProperty( NS + "localCopy" );
	public static final Property error = m_model.createProperty( NS + "error" );		

}

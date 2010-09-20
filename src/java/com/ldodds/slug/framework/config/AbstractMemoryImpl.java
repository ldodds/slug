package com.ldodds.slug.framework.config;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;

/**
 * Default implementation of the memory interface.
 * 
 * Sub-classes should extend this to implement the load/save 
 * behaviour.
 * 
 * This implementation does not use transactions and is not 
 * intended for multi-threaded use, so care should be taken to 
 * also supply this behaviour
 * @author ldodds
 *
 */
public abstract class AbstractMemoryImpl implements Memory {

	protected Model _model;	
	protected Logger _logger;
	  
	public AbstractMemoryImpl() {
		_logger = Logger.getLogger(getClass().getPackage().getName());
	}
	  
	public void addLocalCopy(Resource representation, File localCopy) {
		// just in case
		if (representation.hasProperty(SCUTTERVOCAB.localCopy)
				&& !localCopy.toString().equals(
						representation.getProperty(SCUTTERVOCAB.localCopy)
								.getObject().toString())) {

			representation.removeAll(SCUTTERVOCAB.localCopy);
		}

		representation
				.addProperty(SCUTTERVOCAB.localCopy, localCopy.toString());
	}

	public void addRawTripleCount(Resource representation, long size) {
        if (representation.hasProperty(SCUTTERVOCAB.latestFetch))
        {
            Resource fetch = (Resource)representation.getProperty(SCUTTERVOCAB.latestFetch).getObject();
            //FIXME
            fetch.addProperty(SCUTTERVOCAB.rawTripleCount, size + "" );
        }
	}

	public void annotateFetch(Resource fetch, int code,
			Map<String, List<String>> headers) {
        fetch.addProperty(SCUTTERVOCAB.status, code + "");
        if (headers.containsKey("Content-Type"))
        {
          List<String> values = (List<String>)headers.get("Content-Type");
          fetch.addProperty(SCUTTERVOCAB.contentType, values.get(0) );      
        }
        if (headers.containsKey("Last-Modified"))
        {
        	List<String> values = (List<String>)headers.get("Last-Modified");
          fetch.addProperty(SCUTTERVOCAB.lastModified, values.get(0) );
        }
        if (headers.containsKey("ETag"))
        {
        	List<String> values = (List<String>)headers.get("ETag");
          fetch.addProperty(SCUTTERVOCAB.etag, values.get(0) );      
        }
	}

	public ResIterator getAllRepresentations() {
		    return _model.listSubjectsWithProperty(RDF.type, SCUTTERVOCAB.Representation);      
	}

	  public Resource getOrCreateRepresentation(URL url) 
	  {
	    Resource rep = getRepresentation(url);
	    if (rep == null)
	    {
	      rep = makeRepresentation(url);
	    }
	    return rep;
	  }


	public Resource getOrCreateRepresentation(URL url, URL origin) {
        Resource rep = getOrCreateRepresentation(url);
        if (rep == null) {
        	return null;
        }
        _model.add(rep, SCUTTERVOCAB.origin, _model.createResource( origin.toString() ) );
        return rep;        
	}

	public Resource getRepresentation(URL url) {
        StmtIterator iter = _model.listStatements(null, SCUTTERVOCAB.source, 
        _model.createResource(url.toString()) );
        if (!iter.hasNext()) {
        	return null;  
        }
        return iter.nextStatement().getSubject();
	}

	  private Resource makeRepresentation(URL url)
	  {
	    _model.enterCriticalSection(Lock.WRITE);
	    Resource rep = _model.createResource(SCUTTERVOCAB.Representation);    
	    try {
	      _model.add(rep, SCUTTERVOCAB.source, 
	      _model.createResource(url.toString()) );
	    } 
	    finally {
	      _model.leaveCriticalSection();
	    }
	    return rep;
	  }
	
	public Resource makeFetch(Resource representation) {
        Resource fetch = _model.createResource(SCUTTERVOCAB.Fetch);        
		
        fetch.addProperty(DC.date, 
                _model.createTypedLiteral( DateUtils.getNow(), XSDDatatype.XSDdateTime ) );
                
            representation.addProperty(SCUTTERVOCAB.fetch, fetch);
            
            if (representation.hasProperty(SCUTTERVOCAB.latestFetch))
            {
              representation.removeAll(SCUTTERVOCAB.latestFetch);
            }
            representation.addProperty(SCUTTERVOCAB.latestFetch, fetch);

		return fetch;
	}

	public void makeReasonAndError(Resource fetch, String msg) {
        Resource reason = _model.createResource(SCUTTERVOCAB.Reason);
    	
        reason.addProperty(DC.description, msg);
        reason.addProperty(DC.date, 
            _model.createTypedLiteral( DateUtils.getNow(), XSDDatatype.XSDdateTime ) );       
        
        fetch.addProperty(SCUTTERVOCAB.error, reason);
	}

	public void makeReasonAndError(Resource fetch, Exception e) {
	    makeReasonAndError(fetch, e.getClass().getName() + " " + e.getMessage());  
	}

	public void makeReasonAndSkip(Resource representation, String msg) {
        Resource reason = _model.createResource(SCUTTERVOCAB.Reason);
    	
        reason.addProperty(DC.description, msg);
        reason.addProperty(DC.date, 
            _model.createTypedLiteral( DateUtils.getNow(), XSDDatatype.XSDdateTime ) );       
        representation.addProperty(SCUTTERVOCAB.skip, reason);        
	}

}

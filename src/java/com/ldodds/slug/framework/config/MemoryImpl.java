package com.ldodds.slug.framework.config;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.io.File;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

/**
 * Extension to base memory implementation that uses the Jena critical section code
 * to allow multi-threaded access to a single Jena Model that contains the crawl 
 * memory.
 * 
 * @author Leigh Dodds
 */
public abstract class MemoryImpl extends AbstractMemoryImpl 
{
  
  public MemoryImpl() {
    _logger = Logger.getLogger(getClass().getPackage().getName());
  }
  
  public Resource getRepresentation(URL url) 
  {
        _model.enterCriticalSection(Lock.READ);
        Resource s = null;        
        try
        {
        	s = super.getRepresentation(url);
        }
        finally
        {
            _model.leaveCriticalSection();
        }
        return s;    
  }
    
    public Resource getOrCreateRepresentation(URL url, URL origin) 
    {        
        _model.enterCriticalSection(Lock.WRITE);
        try
        {
            return super.getOrCreateRepresentation(url, origin);
        } finally
        {
            _model.leaveCriticalSection();
        }
    }
    
    public void addRawTripleCount(Resource representation, long size)
    {
        _model.enterCriticalSection(Lock.WRITE);
        try
        {
        	super.addRawTripleCount(representation, size);
        } finally
        {
            _model.leaveCriticalSection();
        }
    }
    
  
  public Resource makeFetch(Resource representation) 
  {
        _model.enterCriticalSection(Lock.WRITE);        
        try
        {
        	return super.makeFetch(representation);
        } finally
        {
            _model.leaveCriticalSection();
        }
  }

  public void annotateFetch(Resource fetch, int code, Map<String,List<String>> headers) 
    {
        _model.enterCriticalSection(Lock.WRITE);
        try
        {
        	super.annotateFetch(fetch, code, headers);
        } finally
        {
           _model.leaveCriticalSection(); 
        }
  }

  public void makeReasonAndSkip(Resource representation, String msg) 
    {    
        _model.enterCriticalSection(Lock.WRITE);
        try
        {
        	super.makeReasonAndSkip(representation, msg);
        } finally {
            _model.leaveCriticalSection();
        }
  }

  public void makeReasonAndError(Resource fetch, String msg) 
    {        
        _model.enterCriticalSection(Lock.WRITE);
        try
        {
        	super.makeReasonAndError(fetch, msg);
        } finally
        {
            _model.leaveCriticalSection();
        }
  }



  public void addLocalCopy(Resource representation, File localCopy) {
	  _model.enterCriticalSection(Lock.WRITE);
	  try {
		  super.addLocalCopy(representation, localCopy);
	  } finally {
		  _model.leaveCriticalSection();
	  }
	}
  
}

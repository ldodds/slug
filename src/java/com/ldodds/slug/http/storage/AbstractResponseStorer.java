package com.ldodds.slug.http.storage;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.ConsumerImpl;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.framework.Task;
import com.ldodds.slug.http.Response;

/**
 * Abstract base class for consumers that store the crawled data
 * 
 * Derived classes should implement the store method.
 * 
 * @author ldodds
 */
public abstract class AbstractResponseStorer extends ConsumerImpl
{

    public void consume(Task workItem, Result result)
    {
    	if (result == null) {
    		return;
    	}
    	
    	try
    	{    	
			Response response = (Response)result;
			Resource representation =
				getMemory().getRepresentation( response.getRequestURL() );

			getLogger().finest("Storing response for " + workItem.getId());
			//TODO support filtering before call to store, e.g. based on the response type.
			store(representation, response);
			
    	} catch (Exception e)
    	{
    		e.printStackTrace();						
    	}    	
    }


    abstract void store(Resource resource, Response response) throws Exception;

}

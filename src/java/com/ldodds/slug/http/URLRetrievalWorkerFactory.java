package com.ldodds.slug.http;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.*;
import com.ldodds.slug.framework.config.Memory;
import com.ldodds.slug.framework.config.MemoryFactory;
import com.ldodds.slug.http.throttle.AbstractThrottle;

/**
 * @author ldodds
 */
public class URLRetrievalWorkerFactory extends WorkerFactoryImpl
{
  
    private Consumer _consumer;
	private MemoryFactory memoryFactory;
	private Resource scutter;
	
	/**
	 * @see com.ldodds.slug.framework.WorkerFactoryImpl#createWorker(java.lang.String)
	 */
	protected Worker createWorker(String name)
	{
        URLRetrievalWorker worker = new URLRetrievalWorker( memoryFactory.getMemory() , name);
        worker.setConsumer(_consumer); 
        worker.setThrottle( AbstractThrottle.getThrottle(scutter) );
        return worker;
	}

    public void setConsumer(Consumer consumer)
    {
        _consumer = consumer;        
    }

	public void setMemoryFactory(MemoryFactory memoryFactory)
	{
		this.memoryFactory = memoryFactory;
	}
    
    public void setController(Controller controller)
    {
        super.setController(controller);
        if (_consumer != null)
        {
        	_consumer.setController(controller);
        }
    }

    public void setScutter(Resource scutter) {
       this.scutter = scutter;
    }
}

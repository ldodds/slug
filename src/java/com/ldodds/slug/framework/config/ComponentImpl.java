package com.ldodds.slug.framework.config;

import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Default implementation of the {@see Component} 
 * interface. By default the configure method does 
 * nothing.
 * 
 * @author ccslrd
 */
public class ComponentImpl implements Component 
{
	protected boolean _configured;
	private Logger logger;
	
	public ComponentImpl() 
	{
		super();
		logger = Logger.getLogger(getClass().getName());		
		_configured = false;
	}

	public void configure(Resource self) 
	{
		if (_configured)
		{
			return;
		}
		_configured = doConfig(self);
	}
	
	public boolean isConfigured()
	{
		return _configured;
	}
	
	protected boolean doConfig(Resource self)
	{
		return true;
	}

	protected Component instantiateReferenced(Resource self, Property property) {
		  Statement s = self.getProperty( property );
		  Resource r = (Resource)s.getObject();
	      ComponentFactory componentFactory = new ComponentFactory();
	      try {
	    	  return componentFactory.instantiate(r);	    	 
	      } catch (Exception e) {
	    	  throw new RuntimeException(e);
	      }		
	}

	protected Object instantiateObject(Resource self, Property property) {
		  Statement s = self.getProperty( property );
		  Resource r = (Resource)s.getObject();
	      ComponentFactory componentFactory = new ComponentFactory();
	      try {
	    	  return componentFactory.instantiateObject(r);	    	 
	      } catch (Exception e) {
	    	  throw new RuntimeException(e);
	      }		
	}	
	
	protected Logger getLogger() {
		return logger;
	}
}

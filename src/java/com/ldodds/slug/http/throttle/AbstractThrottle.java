package com.ldodds.slug.http.throttle;

import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.ldodds.slug.framework.config.ComponentFactory;
import com.ldodds.slug.framework.config.ComponentImpl;
import com.ldodds.slug.vocabulary.CONFIG;

public abstract class AbstractThrottle extends ComponentImpl implements Throttle {

	private Logger _logger;
	
	public AbstractThrottle() {
		_logger = Logger.getLogger( getClass().getPackage().getName() );		
	}
	
	public static Throttle getThrottle(Resource scutter) {
		if ( !scutter.hasProperty(CONFIG.throttle) ) {
			return new NullThrottle();	
		}
		Statement s = scutter.getProperty( CONFIG.throttle );
		Resource r = (Resource)s.getObject();
	    ComponentFactory componentFactory = new ComponentFactory();
	    try {
	    	Throttle throttle = (Throttle)componentFactory.instantiate(r);
	    	return throttle;
	    } catch (Exception e) {
	    	  throw new RuntimeException(e);
	    }			    
	}

	protected Logger getLogger() {
		return _logger;
	}
}

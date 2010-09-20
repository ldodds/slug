package com.ldodds.slug.http.throttle;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.vocabulary.CONFIG;

public class SimpleThrottle extends AbstractThrottle {

	private long timeout;
	
	public SimpleThrottle() {
		
	}
	
	public SimpleThrottle(long timeout) {
		this.timeout = timeout;
	}
	
	public void connected(URLTask task) {
	}

	public void connecting(URLTask task) {
	}

	public void pause(URLTask task) {
		try {
			long timeout = getTimeout();
			getLogger().finest("Pausing for " + timeout );
			Thread.sleep( timeout );
		} catch (InterruptedException e) {
			//noop
		}
	}

	protected long getTimeout() {
		return getConfiguredTimeout();
	}
	
	protected long getConfiguredTimeout() {
		return timeout;
	}
	
	@Override
	protected boolean doConfig(Resource self) {
		timeout = 0;
		if ( self.hasProperty(CONFIG.timeout) ) {
			Statement s = self.getProperty(CONFIG.timeout);
			if ( s.getObject().isLiteral() ) {
				timeout = s.getLong();
			}			
		}
		return super.doConfig(self);
	}

	
}

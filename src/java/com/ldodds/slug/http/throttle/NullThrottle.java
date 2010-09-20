package com.ldodds.slug.http.throttle;

import com.ldodds.slug.framework.config.ComponentImpl;
import com.ldodds.slug.http.URLTask;

/**
 * Does nothing
 * 
 * @author ldodds
 */
public class NullThrottle extends ComponentImpl implements Throttle {

	public void connected(URLTask task) {
	}

	public void connecting(URLTask task) {
	}

	public void pause(URLTask task) {
	}

}

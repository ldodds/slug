package com.ldodds.slug.http.throttle;

import com.ldodds.slug.framework.config.Component;
import com.ldodds.slug.http.URLTask;

public interface Throttle extends Component {
	
	/**
	 * Trigger the throttle to pause for a period before performing a task. The logic for how long a pause is 
	 * required is determined by the throttle implementation
	 */
	void pause(URLTask task);
	void connecting(URLTask task);
	void connected(URLTask task);
}

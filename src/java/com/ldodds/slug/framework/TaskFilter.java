package com.ldodds.slug.framework;

import com.ldodds.slug.framework.config.Component;

public interface TaskFilter extends Component
{
	boolean accept(Task task);	
}

package com.ldodds.slug.framework;

import com.ldodds.slug.framework.config.ComponentImpl;

public class TaskFilterImpl extends ComponentImpl implements TaskFilter 
{
	public boolean accept(Task task) 
	{
		return false;
	}

}

package com.ldodds.slug.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

public class DelegatingTaskFilterImpl implements TaskFilter
{
	private List<TaskFilter> filters;
	private boolean configured;
	
	public DelegatingTaskFilterImpl() 
	{
		filters = new ArrayList<TaskFilter>();
		configured = false;
	}

	public DelegatingTaskFilterImpl(List filters)
	{
		this.filters = filters;
	}
	
	public void addFilter(TaskFilter filter)
	{
		filters.add( filter );
	}
	public boolean accept(Task task) 
	{
    	for (Iterator<TaskFilter> iter = filters.iterator(); iter.hasNext();)
        {
            TaskFilter filter = iter.next();
            if (!filter.accept(task))
            {
            	return false;
            }
        }
    	return true;
	}

	public void configure(Resource self) 
	{
    	for (Iterator<TaskFilter> iter = filters.iterator(); iter.hasNext();)
        {
            TaskFilter filter = iter.next();
            filter.configure(self);
        }
    	configured = true;    	
	}

	public boolean isConfigured()
	{
		return configured;
	}
}

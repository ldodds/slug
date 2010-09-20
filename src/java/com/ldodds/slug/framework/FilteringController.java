package com.ldodds.slug.framework;

import java.util.List;

/**
 * A version of the {@link Controller} class that 
 * applies one or more {@link TaskFilter}s to {@link Task}s 
 * added by its {@link Worker}s 
 *  
 * @author Leigh Dodds
 */
public class FilteringController extends Controller {

	private DelegatingTaskFilterImpl _filter;

	/**
	 * @param workItems
	 * @param factory
	 * @param numberOfWorkers
	 * @param monitor
	 */
	public FilteringController(WorkerFactory factory,
			int numberOfWorkers, Monitor monitor) {
		super(factory, numberOfWorkers, monitor);
		_filter = new DelegatingTaskFilterImpl();
	}
	
	/**
	 * @param workItems
	 * @param factory
	 * @param numberOfWorkers
	 * @param monitor
	 */
	public FilteringController(List<Task> workItems, WorkerFactory factory,
			int numberOfWorkers, Monitor monitor) {
		super(workItems, factory, numberOfWorkers, monitor);
		_filter = new DelegatingTaskFilterImpl();
	}

	public synchronized boolean addWorkItem(Task workItem) 
	{
		if ( _filter.accept(workItem) )
		{		
			return super.addWorkItem(workItem);
		}
		else
		{
			_logger.finest("Filtered work item: " + workItem);
			return false;
		}
	}
	
	public synchronized void addFilter(TaskFilter filter)
	{
		_filter.addFilter(filter);
	}
}

package com.ldodds.slug.framework;

import java.util.*;
import java.util.logging.*;

/**
 * Implementation of the {@link Monitor} interface.
 * @author ldodds
 */
public class MonitorImpl implements Monitor
{
    private Map<Worker, Task> _activeWorkers;
    private Logger _logger; 
    private int _completedTasks;
    private int _successes;
    private int _failures;
    private int _noops;
    
    private Controller controller;
    
    public MonitorImpl()
    {
        _activeWorkers = new HashMap<Worker, Task>();
        _logger = Logger.getLogger(getClass().getPackage().getName());
        _completedTasks = 0;
        _successes = 0;
        _failures = 0;
        _noops = 0;
    }
    
	/**
	 * @see com.ldodds.slug.framework.Monitor#completedTask(com.ldodds.slug.framework.Worker, java.lang.Object)
	 */
	public synchronized void completedTask(Worker worker, Task workItem, Result result)
	{
		String status = null;
		
		if ( result.isSuccess() ) {
			status = "SUCCESS";
			_successes++;
			if (result.isNoOp()) {
				status = "NOOP";
				_noops++;				
			}
		} else {
			status = "FAILURE";
			_failures++;
		}
		
		String logMessage = status + ": " + workItem.getId();
		if (result.getMessage() != null && !"".equals( result.getMessage() ) ) {
			logMessage = logMessage + " (" + result.getMessage() + ")";
		}
		logMessage = logMessage + " [" + worker.getName() + "]";
		_logger.fine( logMessage  );
		_activeWorkers.remove(worker);
		_completedTasks++;
		
		if (_completedTasks % 1000 == 0)
		{
            _logger.log(Level.INFO, getStatistics());			
		}
	}

	private String getStatistics() {
//		"Completed 1000 of 345000 (800 successes; 200 failures; 2 noops; 3 tasks per second); 30/60 active workers"
		StringBuffer statistics = new StringBuffer();
		statistics.append( "Completed " + _completedTasks + ", " + controller.getQueueSize() + " remaining" );
		statistics.append( " (" + _successes + " successes; ");
		statistics.append( _failures + " failures; ");
		statistics.append( _noops + " noops; ");
		statistics.append( getTasksPerSecond() + " tasks per second)");
		statistics.append( "; " + controller.getNumberOfWorkers()  + "/" + controller.getNumberOfWorkers() + " workers active" );
		return statistics.toString();
	}
	
	private double getTasksPerSecond() {
		Date now = Calendar.getInstance().getTime();
		long runtimeSeconds = ( now.getTime() - controller.getStarted().getTime() ) / 1000l;
		return runtimeSeconds / _completedTasks;
	}
	
	/**
	 * @see com.ldodds.slug.framework.Monitor#getNumberOfActiveWorkers()
	 */
	public synchronized int getNumberOfActiveWorkers()
	{
		return _activeWorkers.keySet().size();
	}
	
	/**
	 * @see com.ldodds.slug.framework.Monitor#startingTask(com.ldodds.slug.framework.Worker, java.lang.Object)
	 */
	public synchronized void startingTask(Worker worker, Task workItem)
	{
		_logger.fine("START: " + workItem.getId()  + " [" + worker.getName() + "]");
		_activeWorkers.put(worker, workItem);
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}

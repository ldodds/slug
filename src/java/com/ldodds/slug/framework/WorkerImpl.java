package com.ldodds.slug.framework;

import java.util.logging.Logger;

/**
 * @author ldodds
 */
public abstract class WorkerImpl implements Worker
{
    private String _name;
    protected Controller _controller;
    protected Monitor _monitor;
    protected boolean _shouldStop;
    protected Logger _logger;
        
    public WorkerImpl(String name)
    {
        _name = name;
        _shouldStop = false;
        _logger = Logger.getLogger( getClass().getPackage().getName() );
    }
    
	/**
	 * @see com.ldodds.slug.framework.Worker#setController(com.ldodds.slug.framework.Controller)
	 */
	public void setController(Controller controller)
	{
		_controller = controller;
	}

	/**
	 * @see com.ldodds.slug.framework.Worker#setMonitor(com.ldodds.slug.framework.Monitor)
	 */
	public void setMonitor(Monitor monitor)
	{
        _monitor = monitor;		
	}

	/**
	 * @see com.ldodds.slug.framework.Worker#getName()
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * @see com.ldodds.slug.framework.Worker#stop()
	 */
	public void stop()
	{
        _shouldStop = true;
	}

}

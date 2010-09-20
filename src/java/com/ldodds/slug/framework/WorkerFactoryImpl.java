package com.ldodds.slug.framework;

/**
 * @author ldodds
 */
public abstract class WorkerFactoryImpl implements WorkerFactory
{
    private Monitor _monitor;
    protected Controller _controller;
            
	/**
	 * @see com.ldodds.slug.framework.WorkerFactory#getWorker(java.lang.String)
	 */
	public Worker getWorker(String name)
	{
        Worker worker = createWorker(name);
        worker.setMonitor(_monitor);
        worker.setController(_controller);
		return worker;
	}

    protected abstract Worker createWorker(String name);
    
	/**
	 * @see com.ldodds.slug.framework.WorkerFactory#setMonitor(com.ldodds.slug.framework.Monitor)
	 */
	public void setMonitor(Monitor monitor)
	{
        _monitor = monitor;
	}

	/**
	 * @see com.ldodds.slug.framework.WorkerFactory#setController(com.ldodds.slug.framework.Controller)
	 */
	public void setController(Controller controller)
	{
        _controller = controller;
	}

}

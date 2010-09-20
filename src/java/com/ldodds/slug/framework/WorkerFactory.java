package com.ldodds.slug.framework;

/**
 * @author ldodds
 */
public interface WorkerFactory
{
    public void setMonitor(Monitor monitor);
    public void setController(Controller controller);
    public Worker getWorker(String name);
}

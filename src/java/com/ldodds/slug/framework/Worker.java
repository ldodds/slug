package com.ldodds.slug.framework;

/**
 * @author ldodds
 */
public interface Worker extends Runnable
{
    public void setMonitor(Monitor monitor);    
    public void setController(Controller controller);
    public void stop();
    public String getName();
}

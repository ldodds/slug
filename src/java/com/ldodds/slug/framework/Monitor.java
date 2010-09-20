package com.ldodds.slug.framework;

/**
 * Observes the progress of a web crawl, noting when 
 * tasks start and finish. Can be used to generate metrics 
 * during the crawl
 * 
 * @author ldodds
 */
public interface Monitor
{
    public void startingTask(Worker worker, Task workItem);
    public void completedTask(Worker worker, Task workItem, Result result);
    
    public int getNumberOfActiveWorkers();
    
    public void setController(Controller controller);
    //TODO need metrics
    
}

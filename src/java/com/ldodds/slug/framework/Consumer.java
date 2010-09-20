package com.ldodds.slug.framework;

import com.ldodds.slug.framework.config.Component;
import com.ldodds.slug.framework.config.MemoryHolder;

/**
 * Consumes the results of carrying out a particular 
 * work item.
 * 
 * {@link Worker}s delegate to Consumers to process the results of 
 * a particular work item.
 * 
 * @author ldodds
 */
public interface Consumer extends MemoryHolder, Component
{
    public void consume(Task workItem, Result result);
    public void setController(Controller controller);

}

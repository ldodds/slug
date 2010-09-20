package com.ldodds.slug.http.filter;

import java.util.logging.Level;

import com.ldodds.slug.framework.Task;
import com.ldodds.slug.framework.TaskFilter;
import com.ldodds.slug.framework.config.ComponentImpl;
import com.ldodds.slug.http.URLTask;

public abstract class URLTaskFilter extends ComponentImpl implements TaskFilter
{
    public boolean accept(Task task)
    {
        if (! (task instanceof URLTask) )
        {
            return true;
        }
        
        return acceptURL( (URLTask) task);
    }

    protected void logRejection(URLTask task, String reason) {
    	getLogger().log(Level.FINER, "REJECT: " + task.getId() + "(" + reason +")");
    }
    
    protected abstract boolean acceptURL(URLTask task);
}

package com.ldodds.slug.http.filter;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.rdf.RDFParsingConsumer;

/**
 * Trivial {@link URLTaskFilter} that simply ensures that 
 * URLs are only visited once within a particular crawl.
 * 
 * Note that this is slightly redundant as {@link RDFParsingConsumer} 
 * already checks to see whether a URL has been fetched since 
 * the crawl started.
 * 
 * @author ldodds
 * @deprecated behaviour has been rolled into the core Controller class
 */
public class SingleFetchFilter extends URLTaskFilter
{
    private Set<URL> _visited;
    
    public SingleFetchFilter()
    {
        _visited = new HashSet<URL>(100);        
    }
    
    protected boolean acceptURL(URLTask task)
    {
        URL url = task.getURL();
        if (_visited.contains(url))
        {
        	logRejection(task, "visited");
            return false;
        }
        _visited.add(url);
        return true;
    }
}

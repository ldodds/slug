package com.ldodds.slug.http.filter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.vocabulary.CONFIG;

public class DepthFilter extends URLTaskFilter 
{
    private static final int DEFAULT_DEPTH = 2;
    
	private int _depth;
	
	public DepthFilter() 
	{
		_depth = DEFAULT_DEPTH;
	}

	public DepthFilter(int depth)
	{
		_depth = depth;
	}
	
	protected boolean acceptURL(URLTask task) 
	{
		boolean accept = task.getDepth() < _depth;
		if (!accept) {
		  logRejection(task, "depth");
		}
		return accept;
	}

	protected boolean doConfig(Resource self) 
	{
		if (self.hasProperty(CONFIG.depth))
		{
			_depth = self.getProperty(CONFIG.depth).getInt();
			getLogger().config("Crawl depth:" + _depth);
			return true;
		}
		return false;
	}
	
	public int getDepth()
	{
		return _depth;
	}
}

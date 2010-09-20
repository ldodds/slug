package com.ldodds.slug.http.filter;

import java.util.regex.*;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.vocabulary.CONFIG;

public class RegexFilter extends URLTaskFilter
{
    private Pattern _pattern;

    public RegexFilter()
    {
    	super();
    }
    
    public RegexFilter(String regex)
    {
        _pattern = Pattern.compile(regex);
    }
    
    public RegexFilter(Pattern pattern)
    {
        _pattern = pattern;
    }
    
    protected boolean acceptURL(URLTask task)
    {
        Matcher matcher = _pattern.matcher(task.getURL().toString());
        boolean accept = !matcher.find();
        if (!accept) {
        	logRejection(task, "regex");
        }
        return accept;
    }

    protected boolean doConfig(Resource self) 
    {
    	if (self.hasProperty(CONFIG.filter))
    	{
    		String regex = self.getProperty(CONFIG.filter).getString();
    		getLogger().config("Filter regex:" + regex);
    		_pattern = Pattern.compile(regex);
    		return true;
    	}
    	return false;
    }
    
    public Pattern getPattern()
    {
    	return _pattern;
    }
}

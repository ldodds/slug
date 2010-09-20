package com.ldodds.slug.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLTaskImpl implements URLTask {
	private URL _url;
	private int _depth;
	
	public URLTaskImpl(URL url) 
	{
		_url = url;
		_depth = 0;
	}

	public URLTaskImpl(URLTask task, URL url)
	{
		_url = url;
		_depth = task.getDepth() + 1;
		
	}
	
	public URLTaskImpl(URL url, int depth)
	{
		_url = url;
		_depth = depth;
	}
	
	public URL getURL() 
	{
		return _url;
	}

	public String getId()
	{
		return _url.toString();
	}
	
	public int getDepth()
	{
		return _depth;
	}
	
    public HttpURLConnection openConnection() throws IOException
    {
        return (HttpURLConnection)_url.openConnection();
    }
    
	public String toString()
	{
		return "[" + this.getId() + "]" + _url.toString() + ", depth=" + _depth;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof URLTask) {
			URLTask other = (URLTask)obj;
			return (other.getId().equals( this.getId() ) && 
					other.getURL().equals( this.getURL() ) && 
					other.getDepth() == this.getDepth() );
		}
		return false;
	}
	
	
}

package com.ldodds.slug.http;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.ldodds.slug.framework.ResultImpl;

/**
 * 
 * @author ldodds
 */
public class Response extends ResultImpl
{
    private URL _url;
    private Map<String,List<String>> _responseHeaders;
    private StringBuffer _content;
    
    public Response(URL url, Map<String,List<String>> headers, StringBuffer content)
    {
    	super(true, false);
        _url = url;
        _responseHeaders = headers;
        _content = content;
    }
    
    public URL getRequestURL()
    {
        return _url;
    }
    
    public Map<String,List<String>> getHeaders()
    {
        return _responseHeaders;
    }
    
    public StringBuffer getContent()
    {
        return _content;
    }
    
    public String getContentType() {
    	List<String> contentTypes = _responseHeaders.get("Content-Type");
    	//HACK
    	if (contentTypes == null) {
    		contentTypes = _responseHeaders.get("content-type");
    	}
    	String contentType = null;
    	if (contentTypes != null && !contentTypes.isEmpty()) {
    		contentType = contentTypes.get(0);
    	}
    	return contentType;
    }
    
    public String getContentTypeWithoutCharset() {
    	String contentType = getContentType();
    	if (contentType == null) {
    		return null;
    	}
    	if (contentType.indexOf(";") == -1) {
    		return contentType;
    	}
    	return contentType.split(";")[0];
    }
}

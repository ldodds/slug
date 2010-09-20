package com.ldodds.slug.http;

import java.util.logging.*;
import java.io.*;
import java.net.*;

import com.hp.hpl.jena.rdf.model.*;

import com.ldodds.slug.framework.*;
import com.ldodds.slug.framework.config.Memory;
import com.ldodds.slug.http.throttle.Throttle;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;

/**
 * @author ldodds
 */
public class URLRetrievalWorker extends ProducerWorkerImpl
{
	//used to get etags, lastModified, add skips and Reasons
	private Memory _memory;
	private Throttle throttle;
	
	public URLRetrievalWorker(Memory memory, String name)
	{
		super(name);
		_memory = memory;
	}

	/**
	 * @see com.ldodds.slug.framework.ProducerWorkerImpl#doTask(java.lang.Object)
	 */
	protected Result doTask(Task workItem) {
		URLTask urlTask = (URLTask)workItem;
		pause(urlTask);		
		Response response = null;
		Resource fetch = null;
		try {
			Resource rep = _memory.getOrCreateRepresentation( urlTask.getURL() );
			if (rep != null && rep.hasProperty(SCUTTERVOCAB.skip)) {
				_logger.finer("SKIP " + workItem.getId());
				return ResultImpl.noop();                    
			}
			fetch = _memory.makeFetch(rep);
			
			HttpURLConnection connection = urlTask.openConnection();
			configureConnection(urlTask, rep, connection);

			try
			{
				_logger.finest("Connecting to " + workItem.getId() );
				connecting(urlTask);
				connection.connect();
				connected(urlTask);
				_memory.annotateFetch(fetch, connection.getResponseCode(), connection.getHeaderFields());

			} catch (UnknownHostException uhe)
			{
				_memory.makeReasonAndSkip(rep, "UnknownHostException: " + uhe.getMessage());              
				return ResultImpl.failure("Unknown host");            
			} catch (IOException ie)
			{
				_memory.makeReasonAndError(fetch, ie);
				return ResultImpl.failure(ie.getMessage());
			}

			if (connection.getResponseCode()== HttpURLConnection.HTTP_NOT_FOUND)
			{
				_memory.makeReasonAndSkip(rep, "404, Not Found");
				return ResultImpl.failure("404");
			}

			if (connection.getResponseCode()== HttpURLConnection.HTTP_FORBIDDEN)
			{				
				_memory.makeReasonAndSkip(rep, "403, Not Authorized");
				return ResultImpl.failure("403");
			}

			if (connection.getResponseCode()== HttpURLConnection.HTTP_GONE)
			{				
				_memory.makeReasonAndSkip(rep, "410, Gone");
				return ResultImpl.failure("410");
			}     

			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED)
			{				
				return ResultImpl.noop();
			}

			//success, and not no-op
			response = new Response(
					urlTask.getURL(), 
					connection.getHeaderFields(), 
					readContent(connection) );

		} catch (Exception e)
		{
			String msg = e.getMessage();
			if (msg != null && msg.startsWith("Server returned HTTP response code: ") ) {
				msg = msg.substring(36, 39);
			}
			else {
				_logger.log(Level.SEVERE, "Unexpected exception processing " + workItem.getId(), e);
				
			}			
			_memory.makeReasonAndError(fetch, e);
			return ResultImpl.failure(msg);
		}

		return response;
	}

	private StringBuffer readContent(HttpURLConnection connection) 
	throws IOException
	{
		BufferedReader in = new BufferedReader( 
				new InputStreamReader(connection.getInputStream()), 1024);

		StringBuffer content = new StringBuffer();
		String line = in.readLine();
		while (line != null)
		{
			content.append(line + "\n");
			line = in.readLine();
		}            
		return content; 
	}

	/**
	 * @param rep
	 * @param connection
	 */
	private void configureConnection(URLTask task, Resource rep, HttpURLConnection connection)
	{
		connection.setInstanceFollowRedirects(true);
		//set timeouts
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);
		
		boolean conditionalGet = false;
		
		if (rep != null && rep.hasProperty(SCUTTERVOCAB.latestFetch))
		{
			Resource latestFetch = rep.getProperty(SCUTTERVOCAB.latestFetch).getResource();
			if (latestFetch.hasProperty(SCUTTERVOCAB.etag))
			{                
				connection.addRequestProperty("If-None-Match", 
						latestFetch.getProperty(SCUTTERVOCAB.etag).getObject().toString() );
				conditionalGet = true;
			}
			if (latestFetch.hasProperty(SCUTTERVOCAB.lastModified))
			{
				connection.addRequestProperty("If-Modified-Since",
						latestFetch.getProperty(SCUTTERVOCAB.lastModified).getObject().toString() );
				conditionalGet = true;
			}

		}
		if (conditionalGet) {
			_logger.finest("Performing conditional GET for " + task.getId() );
		}
		connection.addRequestProperty("Accept", "application/rdf+xml");
	}

	private void pause(URLTask task) {
		if ( throttle != null )
			throttle.pause(task);
	}
	private void connecting(URLTask task) {
		if ( throttle != null )
			throttle.connecting(task);	
	}
	private void connected(URLTask task) {
		if ( throttle != null )
			throttle.connected(task);		
	}	
	public void setThrottle(Throttle throttle) {
		this.throttle = throttle;
	}
}

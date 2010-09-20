package com.ldodds.slug.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.framework.ResultImpl;
import com.ldodds.slug.framework.config.Memory;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;

import junit.framework.TestCase;

//import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

public class URLRetrievalWorkerTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSkipResources() throws Exception {
		URL first = new URL("http://www.example.org/a.rdf" );
		
		Resource resource = createMock(Resource.class);
		expect( resource.hasProperty(SCUTTERVOCAB.skip) ).andReturn(true);
		
		Memory memory = createMock(Memory.class);
		expect( memory.getOrCreateRepresentation(first) ).andReturn(resource);				
		URLRetrievalWorker worker = new URLRetrievalWorker(memory, "Test-1");

		replay(memory);
		replay(resource);
		
		Result result = worker.doTask( new URLTaskImpl(first, 1) );
		assertEquals(true, result.isNoOp());
		
		verify(memory);
		verify(resource);
	}
	
	public void testUnknownHost() throws Exception {
		confirmExceptionTriggersFailure(new UnknownHostException(), true);
	}
	
	public void testIOException() throws Exception {
		confirmExceptionTriggersFailure(new IOException(), false );
	}

	public void testRuntimeException() throws Exception {
		confirmExceptionTriggersFailure(new RuntimeException(), false );
	}

	public void testIOExceptionFromOpenConnection() throws Exception {

		URL first = new URL("http://www.example.org/a.rdf" );

		//create mock memory and record its expected calls
		Memory memory = createMock(Memory.class);
		expect( memory.getOrCreateRepresentation(first) ).andReturn(null);		
		expect( memory.makeFetch( null ) ).andReturn(null);
		memory.makeReasonAndError((Resource)anyObject(), (Exception)anyObject() );
			
		URLRetrievalWorker worker = new URLRetrievalWorker(memory, "Test-1");
		
		URLTask task = createMock(URLTask.class);
		expect( task.getURL() ).andReturn(first).anyTimes();
		expect( task.getId() ).andReturn("test").anyTimes();
		expect( task.openConnection() ).andThrow( new IOException() );
		
		replay(memory);
		replay(task);
		
		Result result = worker.doTask( task );
		assertNotNull(result);
		assertTrue( !result.isSuccess() );

		verify(task);
		verify(memory);
	}

	public void testIOExceptionFromOpenConnectionWithServerError() throws Exception {

		URL first = new URL("http://www.example.org/a.rdf" );

		//create mock memory and record its expected calls
		Memory memory = createMock(Memory.class);
		expect( memory.getOrCreateRepresentation(first) ).andReturn(null);		
		expect( memory.makeFetch( null ) ).andReturn(null);
		memory.makeReasonAndError((Resource)anyObject(), (Exception)anyObject() );
			
		URLRetrievalWorker worker = new URLRetrievalWorker(memory, "Test-1");
		
		URLTask task = createMock(URLTask.class);
		expect( task.getURL() ).andReturn(first).anyTimes();
		expect( task.getId() ).andReturn("test").anyTimes();
		expect( task.openConnection() ).andThrow( new IOException("Server returned HTTP response code: 500 for URL: http://www.bbc.co.uk/programmes/b00gknrn.rdf") );
				
		replay(memory);
		replay(task);
		
		Result result = worker.doTask( task );
		assertNotNull(result);
		assertTrue( !result.isSuccess() );
		assertEquals("500", result.getMessage());

		verify(task);
		verify(memory);

	}	
	
	private Result confirmExceptionTriggersFailure(Exception e, boolean skip) throws Exception {
		URL first = new URL("http://www.example.org/a.rdf" );

		//create mock memory and record its expected calls
		Memory memory = createMock(Memory.class);
		expect( memory.getOrCreateRepresentation(first) ).andReturn(null);		
		expect( memory.makeFetch( null ) ).andReturn(null);
		if (skip) {
			memory.makeReasonAndSkip((Resource)anyObject(), (String)anyObject() );
		}
		else {
			memory.makeReasonAndError((Resource)anyObject(), (Exception)anyObject() );
		}
	
		
		URLRetrievalWorker worker = new URLRetrievalWorker(memory, "Test-1");

		//create mock connection, and record its expected calls
		HttpURLConnection connection = createMock(HttpURLConnection.class);
		connection.connect();
		expectLastCall().andThrow( e );
		connection.setInstanceFollowRedirects(true);
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);
		
		connection.addRequestProperty("Accept", "application/rdf+xml");
		
		URLTask task = createMock(URLTask.class);
		expect( task.getURL() ).andReturn(first).anyTimes();
		expect( task.getId() ).andReturn("test").anyTimes();
		expect( task.openConnection() ).andReturn( connection );
		
		replay(memory);
		replay(connection);
		replay(task);
		
		Result result = worker.doTask( task );
		assertNotNull(result);
		assertTrue( !result.isSuccess() );

		verify(task);
		verify(connection);
		verify(memory);
		return result;
	}
	
}

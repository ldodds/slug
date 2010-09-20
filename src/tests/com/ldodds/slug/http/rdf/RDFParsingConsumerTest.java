package com.ldodds.slug.http.rdf;

import static org.easymock.EasyMock.*;
//import static org.easymock.classextension.EasyMock.createMock;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.Controller;
import com.ldodds.slug.framework.config.Memory;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.URLTaskImpl;

import junit.framework.TestCase;

public class RDFParsingConsumerTest extends TestCase {

	private AbstractRDFConsumer consumer;
	private Memory memory;
	private Controller controller;
	private URLTask task;
	private Resource self;
	
	public RDFParsingConsumerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseValidDocument() throws Exception {
		URL url = new URL("http://www.example.org");
		Resource rep = createMock(Resource.class);
		memory = createMock(Memory.class);
		expect( memory.getRepresentation(url) ).andReturn( rep );
		memory.addRawTripleCount(rep, 1);
		
		consumer = new RDFParsingConsumer();		
		consumer.setMemory(memory);
		consumer.setController(controller);
		consumer.configure(self);
		
		Map<String,List<String>> headers = addHeaders(null, "Content-Type", "application/rdf+xml");
		
		task = new URLTaskImpl( url );
		Response result = createResult("http://www.example.org", headers, "valid.n3", "RDF/XML");
		
		replay(memory);
		consumer.consume(task, result);
		verify(memory);
		
		assertNotNull( result.getContext(AbstractRDFConsumer.RDF_MODEL) );
	}

	public void testParseValidDocumentWithCharset() throws Exception {
		URL url = new URL("http://www.example.org");
		Resource rep = createMock(Resource.class);
		memory = createMock(Memory.class);
		expect( memory.getRepresentation(url) ).andReturn( rep );
		memory.addRawTripleCount(rep, 1);
		
		consumer = new RDFParsingConsumer();		
		consumer.setMemory(memory);
		consumer.setController(controller);
		consumer.configure(self);
		
		Map<String,List<String>> headers = addHeaders(null, "Content-Type", "application/rdf+xml; charset=UTF-8");
		
		task = new URLTaskImpl( url );
		Response result = createResult("http://www.example.org", headers, "valid.n3", "RDF/XML");
		
		replay(memory);
		consumer.consume(task, result);
		verify(memory);
		
		assertNotNull( result.getContext(AbstractRDFConsumer.RDF_MODEL) );
	}
	
	public void testParseWithWrongMimeType() throws Exception {
		URL url = new URL("http://www.example.org");
		Resource rep = createMock(Resource.class);
		memory = createMock(Memory.class);
		
		consumer = new RDFParsingConsumer();		
		consumer.setMemory(memory);
		consumer.setController(controller);
		consumer.configure(self);
		
		Map<String,List<String>> headers = addHeaders(null, "Content-Type", "test/html");
		
		task = new URLTaskImpl( url );
		Response result = createResult("http://www.example.org", headers, "valid.n3", "RDF/XML");
		
		replay(memory);
		consumer.consume(task, result);
		verify(memory);
		
		assertNull( result.getContext(AbstractRDFConsumer.RDF_MODEL) );
		
	}
	
	public void testParseInValidDocument() throws Exception {
		URL url = new URL("http://www.example.org");
		Resource rep = createMock(Resource.class);
		memory = createMock(Memory.class);
		expect( memory.getRepresentation(url) ).andReturn( rep );
		//memory.addRawTripleCount(rep, 1);
		
		consumer = new RDFParsingConsumer();		
		consumer.setMemory(memory);
		consumer.setController(controller);
		consumer.configure(self);
		
		Map<String,List<String>> headers = addHeaders(null, "Content-Type", "application/rdf+xml");
		
		task = new URLTaskImpl( url );
		Response result = new Response( url , headers, new StringBuffer( "ddfdsfsdfdsfsd" ) );
		
		replay(memory);
		consumer.consume(task, result);
		verify(memory);
		
		assertNull( result.getContext(AbstractRDFConsumer.RDF_MODEL) );
	}	
	
	private Response createResult(String url, Map<String,List<String>> headers, String filename, String format) 
		throws Exception {

		Model model = ModelFactory.createDefaultModel();
		InputStream in = getClass().getResourceAsStream(filename);
		model.read(in, "", "N3");
		StringWriter writer = new StringWriter();
		model.write(writer, format);
		
		return new Response( new URL(url), headers, writer.getBuffer() );
	}
	
	private Map<String,List<String>> addHeaders(Map<String,List<String>> headers, String header, String value) {
		if (headers == null) {
			headers = new HashMap<String, List<String>>();
		}
		List<String> values = new ArrayList<String>();
		values.add(value);
		headers.put(header, values);
		return headers;
	}
}

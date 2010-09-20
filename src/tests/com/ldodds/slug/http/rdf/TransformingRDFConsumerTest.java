package com.ldodds.slug.http.rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.URLTaskImpl;
import com.ldodds.slug.vocabulary.CONFIG;

import junit.framework.TestCase;

public class TransformingRDFConsumerTest extends TestCase {

	private Model model;
	private TransformingRDFConsumer consumer;
	
	protected void setUp() throws Exception {
		super.setUp();
		model = ModelFactory.createDefaultModel();
		consumer = new TransformingRDFConsumer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoConfigWithEmptyValue() throws Exception {
		Resource self = model.createResource("http://www.example.org/");
		model.add(self, CONFIG.transform, "");
		try {
			consumer.doConfig(self);
			fail("Should fail early if it cannot compile XSLT");
		} catch (RuntimeException e) {
			//ok
		}
	}

	public void testDoConfigWithMissingFile() throws Exception {
		Resource self = model.createResource("http://www.example.org/");
		model.add(self, CONFIG.transform, "/tmp/doesnotexist.xsl");
		try {
			consumer.doConfig(self);
			fail("Should fail early if it cannot compile XSLT");
		} catch (RuntimeException e) {
			//ok
		}
	}

	public void testDoConfigWithValidXSLT() throws Exception {
		Resource self = createConfiguration("identity.xsl");
		consumer.doConfig(self);
		assertNotNull( consumer.getTransform() );
		assertNotNull( consumer.getTemplates() );
	}
	
	public void testIsAcceptableWithNoConfig() throws Exception {
		
		Response response = createResponse("http://www.example.org", "text/html", "london-gazette.htm");
		URL url = new URL("http://www.example.org");
		URLTask task = new URLTaskImpl( url );
		
		assertTrue( consumer.canProcess(task, response) );
	}
	
	public void testIsNotAcceptableUsingURLFiltering() throws Exception {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk");
		
		consumer.configure(s);

		URL url = new URL("http://www.example.org");
		URLTask task = new URLTaskImpl( url );
		
		Response response = createResponse("http://www.example.org", "text/html", "london-gazette.htm");
		
		assertTrue( !consumer.canProcess(task, response) );
		
	}
	
	public void testGetContent() throws Exception {
		Resource self = createConfiguration("RDFa2RDFXML.xsl");
		consumer.configure(self);
		
		Response response = createResponse("http://www.example.org", "text/html", "london-gazette.htm");

		String content = consumer.getContent(response);
		assertNotNull( content );
	}

	public void testGetContentWithInvalidXML() throws Exception {
		Resource self = createConfiguration("RDFa2RDFXML.xsl");
		consumer.configure(self);
		
		Response response = createResponse("http://www.example.org", "text/html", "invalid.xml");

		String content = consumer.getContent(response);
		//not null as it'll be an empty document
		assertNotNull( content );
	}

	
	public void testGetModel() throws Exception {
		Resource self = createConfiguration("RDFa2RDFXML.xsl");
		consumer.configure(self);
		
		Response response = createResponse("http://www.example.org", "text/html", "london-gazette.htm");

		URL url = new URL("http://www.example.org");
		URLTask task = new URLTaskImpl( url );
		
		Model data = consumer.getModel( task , response, "http://www.example.org");
		assertNotNull( data );
		assertTrue( !data.isEmpty() );
	}	

	public void testGetModelWithTidyup() throws Exception {
		Resource self = createConfiguration("RDFa2RDFXML.xsl");
		consumer.configure(self);
		
		Response response = createResponse("http://www.example.org", "text/html", "careers.htm");

		URL url = new URL("http://www.example.org");
		URLTask task = new URLTaskImpl( url );
		
		Model data = consumer.getModel( task , response, "http://www.example.org");
		assertNotNull( data );
		assertTrue( !data.isEmpty() );
		//data.write(System.out);
		
		response = createResponse("http://www.example.org", "text/html", "careers-invalid.htm");
		Model data2 = consumer.getModel( task , response, "http://www.example.org");
		assertNotNull( data2 );
		assertTrue( !data2.isEmpty() );
		//data2.write(System.out);
		//we don't assert direct equality here as in all likelihood the data may end up being different, e.g. the DOM structure
		//may have been tidied, meaning that some literal values are lost
		//assertEquals( data, data2 );
		//instead hope we get the same amount of data
		assertTrue( data.size() == data2.size() );
	}	

	
	public void testGetModelWithInvalidXML() throws Exception {
		Resource self = createConfiguration("RDFa2RDFXML.xsl");
		consumer.configure(self);
		
		Response response = createResponse("http://www.example.org", "text/html", "invalid.xml");

		URL url = new URL("http://www.example.org");
		URLTask task = new URLTaskImpl( url );
		
		Model data = consumer.getModel( task , response, "http://www.example.org");
		assertTrue( data.isEmpty() );
	}	
	
	/**
	 * @return
	 */
	private Resource createConfiguration(String xsl) {
		Resource self = model.createResource("http://www.example.org/");
		model.add(self, CONFIG.transform, this.getClass().getResource(xsl).getFile() );
		return self;
	}

	private Response createResponse(String u, String mimetype, String file) throws MalformedURLException, IOException {
		URL url = new URL(u);
		URLTask task = new URLTaskImpl( url );
		
		List<String> contentType = new ArrayList<String>();
		contentType.add(mimetype);		
		Map<String,List<String>> headers = new HashMap<String,List<String>>();
		headers.put("Content-Type", contentType);
		
		Response response = new Response(
				url, 
				headers, 
				readContent( file ) );
		
		return response;		
	}
	private StringBuffer readContent(String resource) throws IOException {
		InputStream in = this.getClass().getResourceAsStream(resource);
		InputStreamReader r = new InputStreamReader( in );
		BufferedReader reader = new BufferedReader( r );
		StringBuffer content = new StringBuffer();
		String line = null;
		while ( (line = reader.readLine() ) != null ) {
			content.append(line);
		}
			
	    return content;
	}
}

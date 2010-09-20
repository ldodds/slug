package com.ldodds.slug.http.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.vocabulary.CONFIG;

import junit.framework.TestCase;

public class FilteringRDFConsumerTest extends TestCase {

	FilteringRDFConsumer consumer;
	
	protected void setUp() throws Exception {
		super.setUp();
		consumer = new TestFilteringRDFConsumer();		
	}

	public void testWithoutConfig() throws Exception {
		assertTrue( consumer.isAcceptable("http://www.example.org") );
	}

	public void testWithConfiguration() throws Exception {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk");
		
		consumer.configure(s);
		assertTrue( ! consumer.isAcceptable("http://www.example.org") );
		assertTrue( consumer.isAcceptable("http://www.bbc.co.uk") );
	}

	
	public class TestFilteringRDFConsumer extends FilteringRDFConsumer {

		@Override
		protected String getContent(Response response) {
			return null;
		}

		@Override
		protected RDFReader getReader(Response response, Model model) {
			return null;
		}
		
	}
}

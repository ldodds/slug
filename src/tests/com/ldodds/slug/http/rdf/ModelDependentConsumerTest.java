package com.ldodds.slug.http.rdf;

import java.net.URL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.framework.ResultImpl;
import com.ldodds.slug.http.*;

import junit.framework.TestCase;

public class ModelDependentConsumerTest extends TestCase {

	private ModelDependentConsumer consumer;
	
	public ModelDependentConsumerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConsumeIfModelPresent() throws Exception {
	
		consumer = new TestConsumer(false);
		
		URLTask task = new URLTaskImpl( new URL("http://www.example.org") );
		
		Result result = new ResultImpl(true, false);
		result.addContext(AbstractRDFConsumer.RDF_MODEL, ModelFactory.createDefaultModel() );
		consumer.consume(task, result);
	}

	public void testConsumeIfModelNotPresent() throws Exception {
		
		consumer = new TestConsumer(false);
		
		URLTask task = new URLTaskImpl( new URL("http://www.example.org") );
		
		Result result = new ResultImpl(true, false);
		consumer.consume(task, result);
	}
	
	static class TestConsumer extends ModelDependentConsumer {

		private boolean failIfProcessCalled;
		
		public TestConsumer(boolean failIfProcessCalled) {
			this.failIfProcessCalled = failIfProcessCalled;
		}
		@Override
		protected void processModel(URLTask task, Result result, Model model) {
			System.out.println("processModel");
			if (failIfProcessCalled) {
				throw new RuntimeException("Should not have called this method");
			}
			
		}
		
	}
}

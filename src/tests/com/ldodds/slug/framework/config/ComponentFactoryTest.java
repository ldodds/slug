package com.ldodds.slug.framework.config;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ldodds.slug.http.rdf.RDFParsingConsumer;
import com.ldodds.slug.http.storage.ResponseStorer;
import com.ldodds.slug.vocabulary.CONFIG;

import junit.framework.TestCase;

public class ComponentFactoryTest extends TestCase 
{
	private Model _config;
	private ComponentFactory _factory;
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ComponentFactoryTest.class);
	}

	protected void setUp() throws Exception 
	{
		super.setUp();
        Model config = ModelFactory.createDefaultModel();		
		config.read( this.getClass().getResourceAsStream("test-config.rdf"), "");
        
        Model schema = ModelFactory.createDefaultModel();
        schema.read( this.getClass().getResourceAsStream("/config.rdfs"), "");
        
        _config = ModelFactory.createRDFSModel(schema, config);
        _factory = new ComponentFactory();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'com.ldodds.slug.framework.config.ComponentFactory.instantiate(Model, String)'
	 */
	public void testInstantiateModelString() throws Exception 
	{
		Component component = _factory.instantiate(_config, "storer2");
		assertNotNull(component);
		assertTrue(component instanceof ResponseStorer);
		
	}

	/*
	 * Test method for 'com.ldodds.slug.framework.config.ComponentFactory.instantiate(Resource)'
	 */
	public void testInstantiateResource() throws Exception
	{
		Resource storer2 = _config.getResource("storer2");
		assertNotNull(storer2);
		Component component = _factory.instantiate(storer2);
		assertNotNull(component);
		assertTrue(component instanceof ResponseStorer);
		
	}

	/*
	 * Test method for 'com.ldodds.slug.framework.config.ComponentFactory.instantiate(List)'
	 */
	public void testInstantiateList() throws Exception 
	{
		ResIterator iter = 
			_config.listSubjectsWithProperty(RDF.type, CONFIG.Component);
		List toCreate = new ArrayList();
		while (iter.hasNext())
		{
			toCreate.add( iter.nextResource() );
		}
		List created = _factory.instantiate(toCreate);
		assertEquals(toCreate.size(), created.size());		
	}

	/*
	 * Test method for 'com.ldodds.slug.framework.config.ComponentFactory.instantiate(Seq)'
	 */
	public void testInstantiateSeq() throws Exception 
	{
		Resource withSeq = _config.getResource("component-test-seq");
		Resource consumers = withSeq.getProperty(CONFIG.consumers).getSeq();
		List created = _factory.instantiate( (Seq)consumers);
		assertEquals(2, created.size());
		assertTrue( created.get(0) instanceof ResponseStorer);
		assertTrue( created.get(1) instanceof RDFParsingConsumer);		
	}

	public void testConfigureIsCalled() throws Exception
	{
		Resource withSeq = _config.getResource("component-test-seq");
		Resource consumers = withSeq.getProperty(CONFIG.consumers).getSeq();
		List created = _factory.instantiate( (Seq)consumers);
		for (int i=0; i<created.size(); i++)
		{
			Component component = (Component)created.get(i);
			assertTrue(component.isConfigured());
		}
	}
	/*
	 * Test method for 'com.ldodds.slug.framework.config.ComponentFactory.instantiateComponentsFor(Resource, Property)'
	 */
	public void testInstantiateComponentsFor() throws Exception 
	{
		Resource scutter = _config.getResource("component-test-seq");
		List components = _factory.instantiateComponentsFor(scutter, CONFIG.consumers);
		
		assertEquals(2, components.size());
		
		scutter = _config.getResource("component-test-resource");
		components = _factory.instantiateComponentsFor(scutter, CONFIG.consumers);
		
		assertEquals(1, components.size());
		
		scutter = _config.getResource("component-test-resource");
		components = _factory.instantiateComponentsFor(scutter, CONFIG.filters);
		
		assertEquals(0, components.size());
		
		scutter = _config.getResource("component-test-fail-with-literal");
		try
		{
			components = 
				_factory.instantiateComponentsFor(scutter, CONFIG.workers);
			fail("Should have thrown exception");
			
		} catch (Exception e)
		{
		//fine
		}
		
		
		
	}

}

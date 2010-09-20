package com.ldodds.slug.framework.config;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ldodds.slug.vocabulary.CONFIG;
import java.io.File;
import junit.framework.TestCase;

public class MemoryFactoryTest extends TestCase 
{
  private Model _config;
  
  public static void main(String[] args) 
  {
    junit.textui.TestRunner.run(MemoryFactoryTest.class);
  }

  public MemoryFactoryTest(String arg0) 
  {
    super(arg0);
  }

  protected void setUp() throws Exception 
  {
    super.setUp();
        Model config = ModelFactory.createDefaultModel();   
    config.read( this.getClass().getResourceAsStream("test-config.rdf"), "");
        
        Model schema = ModelFactory.createDefaultModel();
        schema.read( this.getClass().getResourceAsStream("/config.rdfs"), "");
        
        _config = ModelFactory.createRDFSModel(schema, config);
  }

  protected void tearDown() throws Exception 
  {
    super.tearDown();
    _config.close();
        _config = null;
  }

  /*
   * Test method for
   * 'com.ldodds.slug.framework.config.MemoryFactory.getMemoryFor(Resource)'
   */
  public void testGetMemory() 
    {
        Resource resource = _config.getResource("file-memory-scutter");
        Memory memory = new MemoryFactory(resource).getMemory();
        assertNotNull(memory);
        assertTrue( memory instanceof FileMemory);
        
        resource = _config.getResource("db-memory-scutter");
        memory = new MemoryFactory(resource).getMemory();
        assertNotNull(memory);
        assertTrue( memory instanceof SharedDatabaseMemory);

  }



}

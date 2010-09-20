package com.ldodds.slug.framework.config;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ldodds.slug.vocabulary.CONFIG;

/**
 * Responsible for creating {@link Memory} objects for use 
 * by the {@link Scutter} and the {@link Consumer}s.
 * 
 * @author Leigh Dodds
 */
public class MemoryFactory 
{
  private Memory memory;
  private Resource forResource;
  
  public MemoryFactory(Resource forResource)  {
	this.forResource = forResource;  
  }
  
  public Memory getMemory() {
    if ( !forResource.hasProperty(CONFIG.hasMemory) ) {    
      return new NullMemory();
    }
    
    return createMemory( forResource.getProperty(CONFIG.hasMemory).getResource() );
  }
  
  private Memory createMemory(Resource resource)
  {
    if (!resource.hasProperty(RDF.type, CONFIG.Memory)) {
      return new NullMemory();
    }
    if (resource.hasProperty(RDF.type, CONFIG.FileMemory))  {
      if (memory == null) {
    	  memory = createFileMemory(resource);
      }       
      return memory;
    }
    if (resource.hasProperty(RDF.type, CONFIG.DatabaseMemory)) {
      if (memory == null) {    	  
    	  memory = createDatabaseMemory(resource);
      }
      return memory;
    }
    
    return new NullMemory(); 
  }
  
  private Memory createFileMemory(Resource resource)
  {
    if (resource.hasProperty(CONFIG.file))
    {
      return createFileMemory(
          resource.getProperty(CONFIG.file).getString());
    }
    return null;
  }
  
  private Memory createFileMemory(String fileName)
  {
    return new FileMemory(fileName);
  }
    
  private Memory createDatabaseMemory(Resource resource)
  {
    if (resource.hasProperty(RDF.type, CONFIG.DatabaseMemory))
    {
      //TODO make this more robust
      return new SharedDatabaseMemory
          (
        resource.getProperty(CONFIG.user).getString(),
        resource.getProperty(CONFIG.pass).getString(),
        resource.getProperty(CONFIG.modelURI).getResource().getURI(),
        resource.getProperty(CONFIG.dbURL).getString(),
        resource.getProperty(CONFIG.dbName).getString(),
        resource.getProperty(CONFIG.driver).getString()
          );
    }
    return null;
  }
  
}

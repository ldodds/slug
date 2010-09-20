package com.ldodds.slug.framework.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ldodds.slug.vocabulary.CONFIG;

/**
 * Factory class responsible for creating Component instances from the 
 * crawler configuration.
 * 
 * @author ldodds
 */
public class ComponentFactory 
{
  /**
   * Instantiate a resource from the supplied model as a Component instance
   * 
   * @param model the model containing the configuration
   * @param id the identifier of the resource to create
   * @return a Component instance
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public Component instantiate(Model model, String id)
    throws ClassNotFoundException, IllegalAccessException, 
    InstantiationException
  {
    Resource resource = model.getResource(id);
    return instantiate(resource);
  }
  
  /**
   * Instantiate the provided resource as a Component
   * 
   * @param resource
   * @return a Component instance
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public Component instantiate(Resource resource)
    throws ClassNotFoundException, IllegalAccessException, 
      InstantiationException
  {
	Object obj = instantiateObject(resource);
	if (! (obj instanceof Component) ) {
	      throw new RuntimeException("Configured class " + resource.getURI() + "doesn't implement Component");
	}
	  
    Component component = (Component)instantiateObject(resource);
    component.configure(resource);
    return component;
  }
  
  public Object instantiateObject(Resource resource)
  	throws ClassNotFoundException, IllegalAccessException, 
  	InstantiationException {
	    if ( !resource.hasProperty(CONFIG.impl) )
	    {
	      return null;
	    }
	    
	    Class<?> clazz = Class.forName(
	        resource.getProperty(CONFIG.impl).getString());
	    Object obj = clazz.newInstance();
	    return obj;
  }

  /**
   * Instantiate a List of Resources as Components, returning them 
   * within a new list.
   * 
   * @param resources
   * @return
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public List<Component> instantiate(List<Resource> resources)
  throws ClassNotFoundException, IllegalAccessException, 
    InstantiationException
  {
    if (resources == null)
    {
      return Collections.emptyList();
    }
    List<Component> instantiated = new ArrayList<Component>();
    
    for (int i=0; i<resources.size(); i++)
    {
      Resource resource = (Resource)resources.get(i);
      instantiated.add( instantiate(resource) );
    }
    return instantiated;
  }
  
  /**
   * Instantiate the resources referred to in the provided Seq as Components, 
   * returning them as a List.
   * 
   * @param sequence
   * @return
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public List<Component> instantiate(Seq sequence)
  throws ClassNotFoundException, IllegalAccessException, 
    InstantiationException  
  {
    if (sequence == null)
    {
      return Collections.emptyList();
    }
    List<Component> instantiated = new ArrayList<Component>();
    for (int i=1; i<=sequence.size(); i++)
    {
      Resource resource = (Resource)sequence.getResource(i);
      instantiated.add( instantiate(resource) );
    }
    return instantiated;
  }
  
  /**
   * Instantiate components associated with a specific Resource via a specific Property
   * @param resource the Resource whose components are being created
   * @param property the Property being used to identify the components
   * @return a list of the component instances, will contain single item if not a multi-valued property
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public List<Component> instantiateComponentsFor(Resource resource, Property property)
  throws ClassNotFoundException, IllegalAccessException, 
    InstantiationException
  {
    if (!resource.hasProperty(property))
    {
      return Collections.emptyList();
    }
    
    Statement statement = resource.getProperty(property);
    if (!statement.getObject().isResource())
    {
      throw new IllegalArgumentException("Property value is not a resource");
    }
    Resource value = (Resource)statement.getObject();
    if (value.hasProperty(RDF.type, RDF.Seq))
    {
      return instantiate( resource.getProperty(property).getSeq() );
    }
    else
    {
      Component component = instantiate(value);
      if (component != null)
      {
        return Collections.singletonList( component );
      }
      
    }
    return Collections.emptyList();
  }
}

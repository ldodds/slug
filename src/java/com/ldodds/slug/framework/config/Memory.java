package com.ldodds.slug.framework.config;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResIterator;

/**
 * Interface describing operations required in a 
 * Scutter memory.
 * 
 * Provides convenience methods for accessing and 
 * updating memory using terms from the ScutterVocab.
 * 
 * @author Leigh Dodds
 */
public interface Memory 
{
  void load();
  void save();

  /**
   * Resource Iterator. May be null if there are no resources
   * @return may be null
   */
  ResIterator getAllRepresentations();
  
  /**
   * Find the RDF resource (of rdf:type Representation) that has
   * a scutter:source property of that url 
   * @param url
   * @return the representation, or null if it cannot be found
   */
  Resource getRepresentation(URL url);
  Resource getOrCreateRepresentation(URL url);
  Resource getOrCreateRepresentation(URL url, URL origin);
  void addRawTripleCount(Resource representation, long size);
  Resource makeFetch(Resource representation);
  void annotateFetch(Resource fetch, int code, Map<String, List<String>> headers);
  void makeReasonAndSkip(Resource representation, String msg);
  void makeReasonAndError(Resource fetch, String msg);
  void makeReasonAndError(Resource fetch, Exception e);
  void addLocalCopy(Resource representation, File localCopy);

}

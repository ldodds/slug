package com.ldodds.slug.http.rdf;

import java.io.StringReader;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.ConsumerImpl;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.framework.Task;
import com.ldodds.slug.http.LoggingErrorHandler;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;

/**
 * Abstract base class for consuming components that parse and process RDF data
 * 
 * Sub-classes should implement the getModel method to provide the RDF model 
 * associated with the results of this Task. The processModel method can also be 
 * overridden in order to provide additional custom behaviour.
 * 
 * The class will also ensure that the generated RDF Model is stored as additional 
 * context in the current Result object, enabling additional "downstream" consumers 
 * to rely directly on the Jena model and not have to repeatedly process the 
 * data.
 * 
 * @author ldodds
 */
public abstract class AbstractRDFConsumer extends ConsumerImpl {

	/**
	 * Context URI for storing and accessing RDF Model data in a Result object.
	 */
	public static final String RDF_MODEL = "http://purl.org/NET/schemas/slug/context/RDFModel";
	
	public void consume(Task workItem, Result result) {
		URLTask urlWorkItem = (URLTask) workItem;
		Response response = (Response) result;

		if ( ! canProcess(urlWorkItem, response) ) {
			//FIXME mis-leading of canProcess decision not related to content type
			getLogger().log(Level.FINE, "Cannot parse response of format: " + response.getContentType());
			return;
		}
		
		//Find the RDF resource (of rdf:type Representation) that has 
		//a scutter:source property of that url 
		Resource representation = getMemory()
				.getRepresentation(urlWorkItem.getURL());
		
		String baseURL = urlWorkItem.getURL().toString();
		
		try {
			
			Model model = getModel(urlWorkItem, response, baseURL);
				
			if (model != null) {
				getLogger().log(Level.FINEST, "Processing parsed model for " + baseURL);
				processModel(urlWorkItem, result, representation, model);
			}			
			
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Exception when consuming response as RDF", e);
		}
	}

	/**
	 * Attempt to parse the results of the task as RDF. If the Content-Type 
	 * header in the response is not set, or is not acceptable then 
	 * the response will not be processed.
	 * 
	 * @param task the task that generated the response
	 * @param response the response
	 * @param baseURL the base URL
	 * @return the model to be used in subsequent processing. If null, then no further processing is carried out
	 */
	protected Model getModel(URLTask task, Response response, String baseURL) {
		
		Model model = ModelFactory.createDefaultModel();
		RDFReader reader = getReader(response, model);
		
		configureReader(reader);
		
		LoggingErrorHandler errorHandler = new LoggingErrorHandler(task.getURL()
				.toString());
		
		reader.setErrorHandler(errorHandler);
		String content = getContent(response);
		
		if (content == null || "".equals(content)) {
			return null;
		}
			
		reader.read(model, new StringReader(content), baseURL);
		
		if ( errorHandler.isError() ) {
			getLogger().log(Level.SEVERE, "Error processing RDF, not adding Model to context");
			return null;
		}
		return model;
	}

	/**
	 * Process the generated RDF model.
	 * 
	 * Sub-classes can override this method to carry out custom behaviour, e.g. to carry out 
	 * additional post-processing of the model. The default implementation updates the 
	 * scutter memory and stores the results of the parsing.
	 * 
	 * @param task the task that generated the response data
	 * @param representation reference to the information about the task in the Scutter memory
	 * @param model the generated Model
	 */
	protected void processModel(URLTask task, Result result, Resource representation, Model model) {
		updateMemory(representation, model);
		addToContext(result, model);
	}	
	
	/**
	 * Update the Scutter memory with information and/or statistics about the retrieved 
	 * data. The default implementation simply adds the raw triple count.
	 *
	 * Override this method to carry out additional updates
	 * 
	 * @param representation the Resource referencing the work item in the Scutter memory
	 * @param model the parsed response data
	 */
	protected void updateMemory(Resource representation, Model model) {
		memory.addRawTripleCount(representation, model.size());
	}
	

	/**
	 * Adds the provided model as context to the Result object.
	 * 
	 * @param result
	 * @param model
	 */
	protected void addToContext(Result result, Model model) {
		result.addContext(getContextURI(), model);
	}

	/**
	 * @return the context uri for storing the parsed Model.
	 */
	protected String getContextURI() {
		return RDF_MODEL;
	}

	/**
	 * Can be overridden by sub-classes to configure the RDFReader before it is used to 
	 * parse an RDF/XML document.
	 *
	 * The default implementation does nothing.
	 * 
	 * @param reader
	 */
	protected void configureReader(RDFReader reader) {
		
	}

	/**
	 * Return an RDFReader instance suitable for parsing this data. Default implementation 
	 * just returns an RDF/XML parser
	 * 
	 * @param model
	 * @return
	 */
	protected abstract RDFReader getReader(Response response, Model model);

	/**
	 * Can the consumer parse this response?
	 * 
	 * @param response
	 * @return true if this can be parsed, false otherwise
	 */	
	protected abstract boolean canProcess(URLTask task, Response response);
	
	/**
	 * Get the response body for parsing as RDF/XML.
	 * 
	 * Sub-classes can override this method to, e.g. translate the original response.
	 * 
	 * @param response the response from the current task
	 * @return a String containing the RDF/XML data
	 */ 
	protected String getContent(Response response) {
		return response.getContent().toString();
	}
	
}
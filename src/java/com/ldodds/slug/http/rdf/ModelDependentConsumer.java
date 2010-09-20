package com.ldodds.slug.http.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.ldodds.slug.framework.ContextDependentConsumer;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.framework.Task;
import com.ldodds.slug.http.URLTask;

public abstract class ModelDependentConsumer extends
		ContextDependentConsumer {

	@Override
	protected void consumeTheTask(Task task, Result result) {
		Model model = (Model)result.getContext( getContextURI() );
		processModel( (URLTask)task, result, model);
	}

	@Override
	protected String getContextURI() {
		return AbstractRDFConsumer.RDF_MODEL;
	}

	/**
	 * Process the RDF model.
	 * 
	 * @param task the task that generated the response data
	 * @param result the result object
	 * @param model the generated Model
	 */	
	protected abstract void processModel(URLTask task, Result result, Model model);
	
}

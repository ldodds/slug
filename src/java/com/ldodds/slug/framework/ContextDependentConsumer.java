package com.ldodds.slug.framework;

/**
 * Abstract base class for components that are dependent not only on the 
 * Results, but on some context associated with the results by a previous 
 * consumer.
 * 
 * This allows context associated with results to be used as a simple way to 
 * pass data between components.
 * 
 * @author ldodds
 *
 */
public abstract class ContextDependentConsumer extends ConsumerImpl {

	public void consume(Task workItem, Result result) {
		if ( meetsCriteria(result) ) {
			consumeTheTask(workItem, result);
		}				
	}

	/**
	 * Does the result have the required context?
	 * 
	 * @param result
	 * @return
	 */
	protected boolean meetsCriteria(Result result) {
		return result.getContext( getContextURI() ) != null;
	}
	
	/**
	 * Actually consume the task results.
	 * 
	 * @param workItem
	 * @param result
	 */
	protected abstract void consumeTheTask(Task workItem, Result result);
	
	/**
	 * Return the URI of the context object
	 * @return
	 */
	protected abstract String getContextURI();
	
}

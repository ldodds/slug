package com.ldodds.slug.framework;

/**
 * The results of a Task
 * 
 * The results may be annotated with additional context, specified by URI, that can be 
 * used as a means of passing information between chains of Consumers.
 * 
 * @author ldodds
 */
public interface Result {

	Object getContext(String url);
	void addContext(String url, Object context);	
	
	String getMessage();
	
	/**
	 * Was the task successful?
	 * 
	 * @return true if the task was successful, false otherwise
	 */
	boolean isSuccess();
	
	/**
	 * Was the task a no-op? i.e. did it succeed but not generate 
	 * any useful results?
	 * 
	 * @return if there are no results, false otherwise.
	 */
	boolean isNoOp();
}

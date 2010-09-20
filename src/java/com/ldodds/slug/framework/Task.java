package com.ldodds.slug.framework;

/**
 * A task to be carried out by a {@link Worker}
 * 
 * @author ccslrd
 */
public interface Task 
{
	/**
	 * Identifier for the task. Should be unique for this specific task item.
	 * @return
	 */
	String getId();
}

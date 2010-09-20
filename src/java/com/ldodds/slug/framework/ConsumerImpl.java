package com.ldodds.slug.framework;

import com.ldodds.slug.framework.config.ComponentImpl;
import com.ldodds.slug.framework.config.Memory;

/**
 * Abstract base class for consuming components.
 * 
 * @author ldodds
 */
public abstract class ConsumerImpl extends ComponentImpl implements Consumer {

	protected Controller controller;
	protected Memory memory;

	
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	protected Memory getMemory() {
		return memory;
	}
	
	protected Controller getController() {
		return controller;
	}
}

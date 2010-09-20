package com.ldodds.slug.http.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.config.Memory;

public interface DataStorer {

	/**
	 * 
	 * @param representation reference to resource in scutter memory
	 * @param source source of data to be stored
	 * @param origin url of where the data originated from
	 */
	void store(Memory memory, Resource representation, URL origin, DataSource source)
		throws IOException;
	
	void setBaseDirectory(File base);
}

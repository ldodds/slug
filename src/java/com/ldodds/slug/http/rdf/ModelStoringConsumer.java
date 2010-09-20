package com.ldodds.slug.http.rdf;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.Result;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.storage.DataSource;
import com.ldodds.slug.http.storage.DataStorer;
import com.ldodds.slug.http.storage.FileStorer;
import com.ldodds.slug.http.storage.ResponseStorer;
import com.ldodds.slug.vocabulary.CONFIG;

public class ModelStoringConsumer extends ModelDependentConsumer {
    private DataStorer storer;
    
	@Override
	protected void processModel(URLTask task, Result result, Model model) {
		//TODO
		Response resp = (Response)result;
		
		Response response = (Response)result;
		Resource representation =
			getMemory().getRepresentation( response.getRequestURL() );
		
		//TODO support other format outputs via configuration
		DataSource source = new ModelDataSource(model, "N-TRIPLE");
		
		storeModel(resp, representation, source);
		
	}

	/**
	 * @param resp
	 * @param representation
	 * @param source
	 */
	protected void storeModel(Response resp, Resource representation,
			DataSource source) {
		try {
			storer.store(getMemory(), representation, resp.getRequestURL(), source);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Unable to serialize Model", e);
		}
	}

    protected boolean doConfig(Resource self) 
    {
    	//if we don't already have a cache, then look for one
    	//in the config.
    	File base = null;
    	if (self.hasProperty(CONFIG.cache))
    	{
    		String directory = self.getProperty(CONFIG.cache).getString();
    		base = new File(directory);
    	}    	
    	else
    	{
    		base = ResponseStorer.getDefaultCacheDir();
    	}
		if (self.hasProperty(CONFIG.storer)) {
			storer = (DataStorer) instantiateObject(self, CONFIG.storer);
		} else {
			storer = new FileStorer();
		}
		storer.setBaseDirectory(base);
    	return true;
    }
	
    public class ModelDataSource implements DataSource {

    	private Model model;
    	private String format;
    	
    	public ModelDataSource(Model model, String format) {
    		this.model = model;
    		this.format = format;
    	}
    	
		public String getData() {

			StringWriter writer = new StringWriter();
			model.write(writer, format);
			return writer.getBuffer().toString();
		}
    	
    }
}

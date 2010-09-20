package com.ldodds.slug.framework.config;

import java.io.*;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFSyntax;
import com.ldodds.slug.vocabulary.SCUTTERVOCAB;

/**
 * Implementation of the Memory interface that stores data in a file as RDF/XML.
 * 
 * @author ldodds
 *
 */
class FileMemory extends MemoryImpl 
{
	private String _fileName;
	public FileMemory(String fileName) 
	{
		_fileName = fileName;
		_model = null;
	}

	public void load()
	{
		//FIXME read as ntriples
		if (_model != null)
		{
			return;
		}
		_model = ModelFactory.createDefaultModel();
		File file = new File(_fileName);
		if (! (file.length() == 0L))
		{
			try {
				
				_model.read( new FileInputStream(_fileName), "" );
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		_logger.log(Level.INFO, "Memory Loaded");
	}

	public void save() 
	{
		//FIXME write as ntriples
        RDFWriter writer = _model.getWriter("RDF/XML-ABBREV");
        _model.getGraph().getPrefixMapping().setNsPrefix("dc", DC.getURI());
        _model.getGraph().getPrefixMapping().setNsPrefix("scutter", SCUTTERVOCAB.getURI());
                        
        //Switch off serializing non-repeating properties as attributes
        writer.setProperty("blockRules", new Resource[] {RDFSyntax.propertyAttr});
        
        writer.setProperty("prettyTypes", new Resource[] { SCUTTERVOCAB.Fetch,
                                                           SCUTTERVOCAB.Reason,
                                                           SCUTTERVOCAB.Representation});
        try {
        	writer.write(_model, new FileOutputStream(_fileName), "");
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        
		_model.close();
		_logger.log(Level.INFO, "Memory Saved");		
	}

}

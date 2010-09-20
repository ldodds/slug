package com.ldodds.slug.http.rdf;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.http.Response;
import com.ldodds.slug.vocabulary.CONFIG;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TransformingRDFConsumer extends FilteringRDFConsumer {
	
	private String transformation;
	private Templates templates;
	
	@Override
	protected String getContent(Response response) {
		
		if (templates == null) {
			return super.getContent(response);
		}
		
		String result = tryToTransform(response, false);
		if (result == null) {
			result = tryToTransform(response, true);
		}
		return result;
	}

	/**
	 * @param response
	 * @return
	 */
	protected String tryToTransform(Response response, boolean tidy) {
		StringWriter writer = new StringWriter();
		
		try {			
			//Transformer transformer = tidy ? TransformerFactory.newInstance().newTransformer() : templates.newTransformer();
			Transformer transformer = templates.newTransformer();
			
			Result result = new StreamResult( writer );
			
			Source source = tidy ? getTidySource(response) : getSource(response);
			
			transformer.transform( source , result);
			
		} catch (TransformerException e) {
			getLogger().log(Level.SEVERE, "Unable to perform transformation", e);
			return null;
		} catch (SAXException se) {
			getLogger().log(Level.SEVERE, "Unable to parse response", se);
			return null;			
		}
		return writer.getBuffer().toString();
	}

	@Override
	protected RDFReader getReader(Response response, Model model) {
		return model.getReader();
	}

	protected Source getSource(Response response) throws SAXException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		InputSource in = new InputSource( new StringReader( response.getContent().toString() ) );
		return new SAXSource(reader, in);
	}

	protected Source getTidySource(Response response) throws SAXException {
		XMLReader reader = new org.cyberneko.html.parsers.SAXParser();
		reader.setFeature("http://cyberneko.org/html/features/insert-namespaces", true);
		reader.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
		InputSource in = new InputSource( new StringReader( response.getContent().toString() ) );
		return new SAXSource(reader, in);		
	}
	
	@Override
	protected boolean doConfig(Resource self) {
		if ( self.hasProperty(CONFIG.transform) ) {
			transformation = self.getProperty(CONFIG.transform).getString();
		}

		if (transformation != null) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();				
				factory.setAttribute(net.sf.saxon.FeatureKeys.DTD_VALIDATION, Boolean.FALSE);
				Source source = new StreamSource( transformation );
				templates = factory.newTemplates(source);
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Unable to parse transformation", e);
				throw new RuntimeException(e);
			}
		}
		return super.doConfig(self);
	}


	protected Templates getTemplates() {
		return templates;
	}
	
	protected String getTransform() {
		return transformation;
	}
}

package com.ldodds.slug.http.scanner;

import java.net.URL;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import junit.framework.TestCase;

public class SeeAlsoScannerTest extends TestCase {

	private SeeAlsoScanner scanner;
	private Model empty;
	private URL origin;
	
	public SeeAlsoScannerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		empty = ModelFactory.createDefaultModel();
		origin = new URL("http://www.example.org");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWithEmptyModel() {
		scanner = new SeeAlsoScanner();		
		
		Set<URL> found = scanner.findURLs(ModelFactory.createDefaultModel(), origin );
		
		assertNotNull(found);
		assertEquals(0, found.size());
	}

	public void testWithNoSeeAlsos() {
		scanner = new SeeAlsoScanner();		
		
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, OWL.sameAs, model.createResource("http://www.example.com") );
		
		Set<URL> found = scanner.findURLs( model , origin );
		
		assertNotNull(found);
		assertEquals(0, found.size());
	}
	
	public void testSeeAlso() {
		scanner = new SeeAlsoScanner();		
		
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.example.com/a.rdf") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.example.com/b.rdf") );

		Set<URL> found = scanner.findURLs( model , origin );
		
		assertNotNull(found);
		assertEquals(2, found.size());
	}		
}

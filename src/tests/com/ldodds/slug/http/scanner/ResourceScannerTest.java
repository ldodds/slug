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

public class ResourceScannerTest extends TestCase {

	private ResourceScanner scanner;
	private Model empty;	
	private URL origin;
	
	public ResourceScannerTest(String name) {
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
		scanner = new ResourceScanner();		
		
		Set<URL> found = scanner.findURLs(ModelFactory.createDefaultModel(), origin );
		
		assertNotNull(found);
		assertEquals(0, found.size());
	}

	public void testWithNoResources() {
		scanner = new ResourceScanner();		
		
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, OWL.sameAs, model.createLiteral("http://www.example.com") );
		
		Set<URL> found = scanner.findURLs( model , origin );
		
		assertNotNull(found);
		assertEquals(0, found.size());
	}
	
	public void testWithObjectResources() {
		scanner = new ResourceScanner();		
		
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.example.com/a.rdf") );
		model.add(resource, OWL.sameAs, model.createResource("http://www.example.com/b.rdf#foo") );
		model.add(resource, DC.relation, model.createResource("http://www.example.com/b.rdf#bar") );
		//should not be in there
		model.add(resource, DC.relation, model.createResource("ftp://www.example.com/c.rdf") );
		
		Set<URL> found = scanner.findURLs( model , origin );
		
		assertNotNull(found);
		assertEquals(3, found.size());
	}		

	public void testWithSubjectResources() {
		scanner = new ResourceScanner();		
		
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, OWL.sameAs, model.createResource("http://www.example.com/b.rdf#foo") );
		model.add(model.createResource("http://www.example.com/other"), OWL.sameAs, resource );
		
		Set<URL> found = scanner.findURLs( model , origin );
		
		assertNotNull(found);
		assertEquals(2, found.size());
	}		
	
}

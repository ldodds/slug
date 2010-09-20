package com.ldodds.slug.http.scanner;

import java.net.URL;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ldodds.slug.vocabulary.CONFIG;

import junit.framework.TestCase;

public class RegexResourceScannerTest extends TestCase {

	private RegexResourceScanner scanner;
	private URL origin;
	
	
	public RegexResourceScannerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		scanner = new RegexResourceScanner();
		origin = new URL("http://www.example.org");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConfigureAcceptance() {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk");
		
		scanner.configure(s);
		assertEquals("http://www.bbc.co.uk", scanner.getAcceptance().pattern());
		assertEquals(null, scanner.getReplacementRegex());
		assertEquals(null, scanner.getReplacementValue());
		
	}
	
	public void testConfigureAll() {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk");
		config.add(s, CONFIG.replacementRegex, "#[a-z0-9]");
		config.add(s, CONFIG.replacementValue, ".rdf");
		
		scanner.configure(s);
		assertEquals("http://www.bbc.co.uk", scanner.getAcceptance().pattern());
		assertEquals("#[a-z0-9]", scanner.getReplacementRegex());
		assertEquals(".rdf", scanner.getReplacementValue());
		
	}

	public void testSimpleAcceptance() throws Exception {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk");
		scanner.configure(s);

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.example.com/a.rdf") );
		model.add(resource, OWL.sameAs, model.createResource("http://www.bbc.co.uk/programmes/w0000cjg.rdf") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.bbc.co.uk") );
		
		//System.out.println("Starting scan");
		Set<URL> found = scanner.findURLs(model, origin);
		//System.out.println("Finished scan");
		assertEquals(2, found.size());
		assertTrue( found.contains( new URL("http://www.bbc.co.uk") ) );
		assertTrue( found.contains( new URL("http://www.bbc.co.uk/programmes/w0000cjg.rdf") ) );
	}
	
	public void testReplacement() throws Exception {
		Model config = ModelFactory.createDefaultModel();
		Resource s = config.createResource("http://www.example.org/test#scanner1");
		config.add(s, CONFIG.filter, "http://www.bbc.co.uk/(programmes|music)/");
		config.add(s, CONFIG.replacementRegex, "#[a-z0-9]+");
		config.add(s, CONFIG.replacementValue, ".rdf");		
		scanner.configure(s);

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource("http://www.example.org");
		model.add(resource, DC.title, model.createLiteral("Title") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.bbc.co.uk/programmes/w0006q6r#programme") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.bbc.co.uk/music/artists/4a4ee089-93b1-4470-af9a-6ff575d32704#artist") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.bbc.co.uk/programmes/b0074swp#programme") );
		model.add(resource, RDFS.seeAlso, model.createResource("http://www.bbc.co.uk/ontologies/programmes/2008-02-28.shtml") );
		
		//System.out.println("Starting scan");
		Set<URL> found = scanner.findURLs(model, origin);
		//System.out.println("Finished scan");
		assertEquals(3, found.size());
		
		assertTrue( found.contains( new URL("http://www.bbc.co.uk/programmes/w0006q6r.rdf") ) );
		assertTrue( found.contains( new URL("http://www.bbc.co.uk/music/artists/4a4ee089-93b1-4470-af9a-6ff575d32704.rdf") ) );
		assertTrue( found.contains( new URL("http://www.bbc.co.uk/programmes/b0074swp.rdf") ) );
	}	
}

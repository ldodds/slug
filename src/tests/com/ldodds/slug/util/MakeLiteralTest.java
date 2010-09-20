package com.ldodds.slug.util;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.DC;
import com.ldodds.slug.util.MakeLiteral;

import junit.framework.TestCase;

public class MakeLiteralTest extends TestCase 
{
	public static final String RULES = 
		"@prefix test: <http://www.ldodds.com/ns/test/>.\n" +
		"@prefix dc: <http://purl.org/dc/elements/1.1/>.\n" +
		"[testing: (?s dc:creator ?o), makeLiteral(?o, ?literal) -> (?s test:literal ?literal)]";
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(MakeLiteralTest.class);
	}

	public MakeLiteralTest(String arg0) 
	{
		super(arg0);
	}

	protected void setUp() throws Exception 
	{
		super.setUp();
	}

	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	public void test() throws Exception
	{
		Model model = ModelFactory.createDefaultModel();
		model.add( model.createResource("http://www.ldodds.com"), 
				   DC.creator, 
				   model.createResource("http://www.ldodds.com/authors#me"));
		
		BuiltinRegistry.theRegistry.register( new MakeLiteral() );
		
        List rules = Rule.parseRules( Rule.rulesParserFromReader( new BufferedReader( new StringReader(RULES) ) ) );
        assertTrue( rules.size() > 0 );
        GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);        
        InfModel inference = ModelFactory.createInfModel(reasoner, model);

        Model results = inference.getDeductionsModel();
		
        assertTrue( results.size() > 0 );
        assertTrue( results.contains(null, model.createProperty("http://www.ldodds.com/ns/test/literal"), 
        		model.createLiteral("http://www.ldodds.com/authors#me")) );
	}
	
}

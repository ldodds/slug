package com.ldodds.slug.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;

/**
 * Utility class to process a Slug memory and generate 
 * a Jena LocationMapping configuration file.
 * 
 * Application accepts two parameters: 
 * 
 * <ol>
 * <li>File containing memory data</li>
 * <li>File containing processing rules</li>
 * </ol>
 * 
 * @author Leigh Dodds
 */
public class GenerateLocationMapping 
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
		throws Exception
	{
		if (args.length < 2)
		{
			showUsage();
			System.exit(1);
		}
		//read the memory
		Model model = FileManager.get().loadModel(args[0]);
		
		//register the custom built-in
		BuiltinRegistry.theRegistry.register( new MakeLiteral() );
		
		//load and parse the rules
        List rules = Rule.parseRules( 
        		Rule.rulesParserFromReader( 
        				new BufferedReader( new FileReader( args[1] ) ) ) );

        //create the reasoner
        GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);        
        InfModel inference = ModelFactory.createInfModel(reasoner, model);

        //retrieve the newly inferred triples
        Model results = inference.getDeductionsModel();
        
        //write out the results
        if (args.length == 3)
        {
        	results.write( new FileOutputStream(args[2]), "N3");
        }
        else
        {
        	results.write(System.out, "N3");
        }
		
	}
	
	public static void showUsage()
	{
		System.out.println("java com.ldodds.slug.util.GenerateLocationMapping <memory-file> <rules-file>");
	}

}

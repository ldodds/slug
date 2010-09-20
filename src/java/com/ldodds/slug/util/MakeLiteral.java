package com.ldodds.slug.util;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinException;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class MakeLiteral extends BaseBuiltin {

	public String getName() 
	{
		return "makeLiteral";
	}

	public int getArgLength()  {
		return 2;
	}
	
    /**
     * This method is invoked when the builtin is called in a rule body.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes, some of which may be Node_RuleVariables.
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @param context an execution context giving access to other relevant data
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
    	checkArgs(length, context);
    	Node node = getArg(0, args, context);
        BindingEnvironment env = context.getEnv();
        Node newLiteral = null;
    	if (node.isLiteral())
    	{
    		newLiteral = Node.createLiteral( node.getLiteral() );    		
    	}
    	else if (node.isBlank())
    	{
    		newLiteral = Node.createLiteral( node.getBlankNodeId().toString() );
    	}
    	else if (node.isURI())
    	{
    		newLiteral = Node.createLiteral( node.getURI() );
    	}
    	else
    	{
    		throw new BuiltinException(this, context, "can't create literal");
    	}
    	env.bind(args[1], newLiteral);
        return true;
    }
    
    public void headAction(Node[] args, RuleContext context) {
        // Can't be used in the head
        throw new BuiltinException(this, context, "can't do " + getName() + " in rule heads");
    }	
}

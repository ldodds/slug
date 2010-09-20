package com.ldodds.slug.http.rdf;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.http.rdf");
		//$JUnit-BEGIN$
		suite.addTestSuite(ModelDependentConsumerTest.class);
		suite.addTestSuite(TaskGeneratingConsumerTest.class);
		suite.addTestSuite(FilteringRDFConsumerTest.class);
		suite.addTestSuite(TransformingRDFConsumerTest.class);
		suite.addTestSuite(RDFParsingConsumerTest.class);
		//$JUnit-END$
		return suite;
	}

}

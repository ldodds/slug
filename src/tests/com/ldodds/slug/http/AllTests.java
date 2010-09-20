package com.ldodds.slug.http;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.http");
		suite.addTest(com.ldodds.slug.http.storage.AllTests.suite());
		suite.addTest(com.ldodds.slug.http.filter.AllTests.suite());		
		suite.addTest(com.ldodds.slug.http.scanner.AllTests.suite());
		suite.addTest(com.ldodds.slug.http.rdf.AllTests.suite());
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}

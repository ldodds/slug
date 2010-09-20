package com.ldodds.slug.http.scanner;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.http.scanner");
		//$JUnit-BEGIN$
		suite.addTestSuite(SeeAlsoScannerTest.class);
		suite.addTestSuite(ResourceScannerTest.class);
		suite.addTestSuite(RegexResourceScannerTest.class);
		//$JUnit-END$
		return suite;
	}

}

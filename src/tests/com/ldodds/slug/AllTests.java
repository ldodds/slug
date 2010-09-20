package com.ldodds.slug;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug");
		suite.addTest(com.ldodds.slug.framework.AllTests.suite());
		suite.addTest(com.ldodds.slug.http.AllTests.suite());
		suite.addTest(com.ldodds.slug.util.AllTests.suite());
		
		//$JUnit-BEGIN$
		//$JUnit-END$
		return suite;
	}

}
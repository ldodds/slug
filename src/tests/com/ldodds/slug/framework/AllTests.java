package com.ldodds.slug.framework;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug");
		suite.addTest(com.ldodds.slug.framework.config.AllTests.suite());
		
		//$JUnit-BEGIN$
		suite.addTestSuite(ControllerTest.class);
		//$JUnit-END$
		return suite;
	}

}
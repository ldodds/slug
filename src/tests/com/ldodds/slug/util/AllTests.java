package com.ldodds.slug.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.util");
		//$JUnit-BEGIN$
		suite.addTestSuite(MakeLiteralTest.class);
		//$JUnit-END$
		return suite;
	}

}

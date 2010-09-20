package com.ldodds.slug.framework.config;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.ldodds.slug.framework.config");
		//$JUnit-BEGIN$
		suite.addTestSuite(MemoryFactoryTest.class);
		suite.addTestSuite(ComponentFactoryTest.class);
		//$JUnit-END$
		return suite;
	}

}

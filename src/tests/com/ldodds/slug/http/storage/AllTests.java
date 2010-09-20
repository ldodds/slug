package com.ldodds.slug.http.storage;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.http.storage");
		//$JUnit-BEGIN$
		suite.addTestSuite(FileStorerTest.class);
		//$JUnit-END$
		return suite;
	}

}

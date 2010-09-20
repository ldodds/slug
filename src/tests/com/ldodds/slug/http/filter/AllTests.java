package com.ldodds.slug.http.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.ldodds.slug.http");
		//$JUnit-BEGIN$
		suite.addTestSuite(RegexFilterTest.class);
		suite.addTestSuite(URLTaskImplTest.class);
		suite.addTestSuite(SingleFetchFilterTest.class);
		//$JUnit-END$
		return suite;
	}

}

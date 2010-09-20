package com.ldodds.slug.http.filter;

import java.net.URL;

import com.ldodds.slug.http.URLTaskImpl;
import com.ldodds.slug.http.filter.DepthFilter;

import junit.framework.TestCase;

public class SingleFetchFilterTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SingleFetchFilterTest.class);
	}

	public SingleFetchFilterTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAllowed() throws Exception
	{
		URLTaskImpl task = new URLTaskImpl(new URL("http://www.example.org"));
		DepthFilter filter = new DepthFilter(3);
		assertTrue( filter.accept(task));
	}
	
	public void testNotAllowed() throws Exception
	{
		URLTaskImpl task = new URLTaskImpl(new URL("http://www.example.org"), 3);
		DepthFilter filter = new DepthFilter(2);
		assertTrue( !filter.accept(task) );
	}
}

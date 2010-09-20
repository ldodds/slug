package com.ldodds.slug.http.filter;

import java.net.URL;

import com.ldodds.slug.http.URLTaskImpl;

import junit.framework.TestCase;

public class URLTaskImplTest extends TestCase {

	public static void main(String[] args) 
	{
		junit.textui.TestRunner.run(URLTaskImplTest.class);
	}

	public URLTaskImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'com.ldodds.slug.http.URLTaskImpl.URLTaskImpl(URL)'
	 */
	public void testURLTaskImplURL() throws Exception
	{
		URL url = new URL("http://www.ldodds.com");
		URLTaskImpl task = new URLTaskImpl( url );
		assertEquals( url, task.getURL() );
	}

	/*
	 * Test method for 'com.ldodds.slug.http.URLTaskImpl.URLTaskImpl(URLTask, URL)'
	 */
	public void testURLTaskImplURLTaskURL() throws Exception
	{
		URL url1 = new URL("http://www.ldodds.com");
		URL url2 = new URL("http://www.ldodds.com/ldodds.rdf");
		URLTaskImpl task = new URLTaskImpl( url1 );
		
		URLTaskImpl task2 = new URLTaskImpl( task, url2 );
		assertEquals(url2, task2.getURL());
		assertTrue( !url1.equals(task2.getURL()) );
		assertEquals( 1, task2.getDepth() );
		assertTrue( task.getDepth() < task2.getDepth() );
	}

	/*
	 * Test method for 'com.ldodds.slug.http.URLTaskImpl.URLTaskImpl(URL, int)'
	 */
	public void testURLTaskImplURLInt() {

	}

}

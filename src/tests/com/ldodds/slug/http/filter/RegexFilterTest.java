package com.ldodds.slug.http.filter;

import java.net.URL;
import java.util.regex.Pattern;

import com.ldodds.slug.http.URLTask;
import com.ldodds.slug.http.URLTaskImpl;
import com.ldodds.slug.http.filter.RegexFilter;

import junit.framework.TestCase;

public class RegexFilterTest extends TestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(RegexFilterTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /*
     * Test method for 'com.ldodds.slug.http.RegexFilter.acceptURL(URLTask)'
     */
    public void testAcceptURL() throws Exception
    {
        URL url = new URL("http://www.ldodds.com/ldodds.rdf");
        URLTask task = new URLTaskImpl(url);
        
        RegexFilter filter = new RegexFilter("ldodds");
        
        assertFalse( filter.accept(task) );
        assertFalse( filter.acceptURL(task) );
        
        url = new URL("http://www.livejournal.com/foo.rdf");
        task = new URLTaskImpl(url);
        
        assertTrue( filter.accept(task) );
        assertTrue( filter.acceptURL(task) );
        
        Pattern pattern = Pattern.compile("livejournal");
        filter = new RegexFilter(pattern);
        
        assertFalse( filter.accept(task) );
    }

}

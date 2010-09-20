package com.ldodds.slug.http.storage;

import static org.easymock.EasyMock.*;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

public class FileStorerTest extends TestCase {

	private FileStorer storer;
	private File tester;
	
	public FileStorerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
		if (tester != null) {
			tester.delete();
		}
	}

	public void testGetFileNoFile() throws Exception {
		File tmp = getTempDir();
		storer = new FileStorer(tmp);
		
		URL simple = new URL("http://www.example.org/");
		
		File filename = storer.getFile(simple);
		assertNotNull(filename);
		assertEquals(tmp.getAbsolutePath() + File.separator + "www.example.org/index", filename.getAbsolutePath());		
	}

	public void testGetFileNoFileWithPath() throws Exception {
		File tmp = getTempDir();
		storer = new FileStorer(tmp);
		
		URL simple = new URL("http://www.example.org/a/b/c/");
		
		File filename = storer.getFile(simple);
		assertNotNull(filename);
		assertEquals(tmp.getAbsolutePath() + File.separator + "www.example.org/a/b/c", filename.getAbsolutePath());		
	}

	
	public void testGetFile() throws Exception {
		File tmp = getTempDir();
		storer = new FileStorer(tmp);
		
		URL simple = new URL("http://www.example.org/a/b/c/filename.rdf");
		
		File filename = storer.getFile(simple);
		assertNotNull(filename);
		assertEquals(tmp.getAbsolutePath() + File.separator + "www.example.org/a/b/c/filename.rdf", filename.getAbsolutePath());
	}

	public void testGetFileWithQueryString() throws Exception {
		File tmp = getTempDir();
		storer = new FileStorer(tmp);
		
		URL simple = new URL("http://www.example.org/a/b/c/filename?foo=bar");
		
		File filename = storer.getFile(simple);
		assertNotNull(filename);
		assertEquals(tmp.getAbsolutePath() + File.separator + "www.example.org/a/b/c/filename_foo_bar", filename.getAbsolutePath());
	}

	public void testGetFileName() throws Exception {
		File tmp = getTempDir();
		storer = new FileStorer(tmp);
		
		URL simple = new URL("http://www.example.org/a/b/c/filename.rdf");
		
		String filename = storer.getFileName(simple);
		assertNotNull(filename);
		assertEquals("/a/b/c/filename.rdf", filename );
	}
	
	
	private File getTempDir() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		return new File(tmpdir);
	}

	
}

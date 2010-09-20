package com.ldodds.slug.http.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ldodds.slug.framework.config.Memory;

public class FileStorer implements DataStorer {

	private File base;
	
	public FileStorer() {
		this(null);
	}
	
	public FileStorer(File base) {
		this.base = base;
	}
	
	public void store(Memory memory, Resource representation, URL origin,
			DataSource source) throws IOException {

		File localCopy = getFile( origin );			
		store(source, localCopy);
		memory.addLocalCopy(representation, localCopy);
	}

	protected void store(DataSource source, File toFile) throws IOException
	{
		BufferedWriter out = 
			new BufferedWriter( new FileWriter(toFile) ); 

		out.write( source.getData() );
		
		out.flush();
		out.close();		
	}
	
	protected File getFile(URL url)
	{
		String fileName = getFileName(url);
		
		File domain = new File(base, url.getHost());
		if (!domain.exists())
		{
			domain.mkdir();
		}
		File representation = new File(domain, fileName);
		File parent = representation.getParentFile();
		parent.mkdirs();
		return representation;
	}

	/**
	 * @param url
	 * @return
	 */
	protected String getFileName(URL url) {
		String fileName = url.getFile();
		
		if (fileName == null || "".equals(fileName) || "/".equals(fileName) ) {
			fileName = "index";
		}
		
		fileName = fileName.replace('?','_')
						   .replace('&', '_')
						   .replace(';', '_')
						   .replace('=', '_');
		return fileName;
	}
	

	public void setBaseDirectory(File dir) {
		base = dir;
	}
	
}

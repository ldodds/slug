package com.ldodds.slug.framework.config;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class NullMemory implements Memory {
	
	public void addLocalCopy(Resource representation, File localCopy) {
	}

	public void addRawTripleCount(Resource representation, long size) {
	}

	public void annotateFetch(Resource fetch, int code,
			Map<String, List<String>> headers) {
	}

	public boolean canBeFetched(Resource representation, Date date) {
		return true;
	}

	public ResIterator getAllRepresentations() {
		return null;
	}

	public Resource getOrCreateRepresentation(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Resource getOrCreateRepresentation(URL url, URL origin) {
		// TODO Auto-generated method stub
		return null;
	}

	public Resource getRepresentation(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public void load() {
	}

	public Resource makeFetch(Resource representation) {
		return null;
	}

	public void makeReasonAndError(Resource fetch, String msg) {
	}

	public void makeReasonAndError(Resource fetch, Exception e) {
	}

	public void makeReasonAndSkip(Resource representation, String msg) {
	}

	public void save() {
	}

	public void store(Resource resource, StringBuffer content, URL requestURL)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}

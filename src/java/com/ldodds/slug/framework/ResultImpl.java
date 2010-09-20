package com.ldodds.slug.framework;

import java.util.HashMap;
import java.util.Map;

public class ResultImpl implements Result {

	private boolean success;
	private boolean noop;
	private String msg;
	
	private Map<String, Object> context;
	
	public ResultImpl(boolean success, boolean noop) {
		this(success, noop, "");
	}

	public ResultImpl(boolean success, boolean noop, String msg) {
		this.success = success;
		this.noop = noop;
		context = new HashMap<String,Object>();
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public void addContext(String url, Object data) {
		context.put(url, data);
	}

	public Object getContext(String url) {
		return context.get(url);
	}

	public Object removeContext(String url) {
		return context.remove(url);
	}
	
	public boolean isNoOp() {
		return noop;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public static Result failure(String msg) {
		return new ResultImpl(false, true, msg);
	}
	
	public static Result noop() {
		return new ResultImpl(true, true);
	}
	
}

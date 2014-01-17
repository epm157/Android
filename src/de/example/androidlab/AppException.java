package de.example.androidlab;

import java.util.HashMap;
import java.util.Map;

public class AppException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,Object> runtimeValues;
	private int exceptionCode;
	
	public AppException(AppExceptions ex) {
		super("L2PDropbox exception occured! code: " + ex.getExceptionCode());
		this.exceptionCode = ex.getExceptionCode();
		runtimeValues  = new HashMap<String, Object>();
	}
	
	public int getExceptionCode() {
		return exceptionCode;
	}
	
	public AppException addRuntimeValue(String key, Object value) {
		runtimeValues.put(key, value);
		return this;
	}
}

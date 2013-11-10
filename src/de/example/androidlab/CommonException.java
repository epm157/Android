package de.example.androidlab;

import java.util.HashMap;
import java.util.Map;

public class CommonException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,Object> runtimeValues;
	private int number;
	
	public CommonException(AuthenticationErrors err) {
		super(err.getMessage());
		this.number = err.getNumber();
		runtimeValues  = new HashMap<String, Object>();
	}
	
	public int getNumber() {
		return number;
	}

	public CommonException addRuntimeValue(String key, Object value) {
		runtimeValues.put(key, value);
		return this;
	}
}

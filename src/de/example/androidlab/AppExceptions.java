package de.example.androidlab;

public enum AppExceptions {
	
	APP_IS_NOT_AUTHORIZED(101),
	CANNOT_PARSE_RETURNED_JSON(102), 
	APP_IS_NOT_REGISTERED(103),       //We don't have device_code yet
	REQUIRE_FIELD_NOT_IN_JSON(104);  
	
	
	int exceptionCode;
	private AppExceptions(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	
	public int getExceptionCode() {
		return exceptionCode;
	}

}

package de.example.androidlab;

public enum AuthenticationErrors implements CommonErrors{
	
	APP_IS_NOT_AUTHORIZED(101,"The application is not authorized"),
	CANNOT_PARSE_RETURNED_JSON(102,"The returned JSON object is not parsable");
	
	private AuthenticationErrors(int number, String message) {
		this.number = number;
		this.message = message;
	}
	
	private int number;
	private String message;

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public String getMessage() {
		return message;
	}

}

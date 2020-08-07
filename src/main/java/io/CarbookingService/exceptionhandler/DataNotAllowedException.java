package io.CarbookingService.exceptionhandler;

public class DataNotAllowedException extends Exception{

	private static final long serialVersionUID = 1L;

	public DataNotAllowedException(String message) {
		super(message);
	}

}

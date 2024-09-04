package com.edugreat.akademiksresource.exception;

/*
 * The application-wide exception
 */
public class AcademicException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode;

	public AcademicException(String message, String errorCode) {

		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {

		return this.errorCode;
	}

	public void setErrorCode(String error) {

		this.errorCode = error;
	}

}

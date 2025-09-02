package com.edugreat.akademiksresource.exception;

import java.io.Serial;

/*
 * The application-wide exception
 */
public class AcademicException extends RuntimeException {

    /**
     * 
     */
    @Serial
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

package com.edugreat.akademiksresource.util;

public class ApiResponseObject<T> {
	
	private T data;
	private String error;
	private boolean success;
	
	public ApiResponseObject(T data, String error, boolean success) {
		
		this.data = data;
		this.error = error;
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public String getError() {
		return error;
	}

	public boolean isSuccess() {
		return success;
	}
	
	
	

}

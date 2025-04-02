package com.edugreat.akademiksresource.dto;

public class AdminsDTO extends AppUserDTO {

//	property specifically used to enable json de-serialize logged in user properly
	private String type = "admin";
	
	public AdminsDTO() {
		super();
	}

	public AdminsDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);

	}

	public AdminsDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



}

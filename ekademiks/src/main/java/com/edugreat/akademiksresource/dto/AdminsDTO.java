package com.edugreat.akademiksresource.dto;

public class AdminsDTO extends AppUserDTO {


	
	public AdminsDTO() {
		super("admin");
	}

	public AdminsDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);

	}

	public AdminsDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);

	}

	
}

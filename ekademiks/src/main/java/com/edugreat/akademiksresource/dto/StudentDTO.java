package com.edugreat.akademiksresource.dto;

import java.util.Set;

//Data transfer object for the Student object
public class StudentDTO extends AppUserDTO{

	public StudentDTO() {
		super();
	}
	public StudentDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);
		
	}

	public StudentDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);
		
	}
	@Override
	public Set<String> getRoles() {
		// TODO Auto-generated method stub
		return super.getRoles();
	}
	

	

	


	
}

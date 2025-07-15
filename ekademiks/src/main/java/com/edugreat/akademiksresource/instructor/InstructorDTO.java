package com.edugreat.akademiksresource.instructor;

import com.edugreat.akademiksresource.dto.AppUserDTO;

public class InstructorDTO extends AppUserDTO {

	public InstructorDTO() {
		super("instructor");
		
	}

	public InstructorDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);
	
	}

	public InstructorDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);
		
	}

	
	

}

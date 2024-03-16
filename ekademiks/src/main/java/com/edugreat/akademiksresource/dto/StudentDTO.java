package com.edugreat.akademiksresource.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.edugreat.akademiksresource.enums.Roles;

import lombok.NoArgsConstructor;

//Data transfer object for the Student object
@NoArgsConstructor
public class StudentDTO extends AppUserDTO{

	public StudentDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);
		
	}

	public StudentDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);
		
	}

	

	@Override
	public Set<Roles> getUserRoles() {
		
		return new HashSet<>(List.of(Roles.Student));
	}
	


	
}

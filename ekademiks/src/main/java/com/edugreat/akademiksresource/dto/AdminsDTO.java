package com.edugreat.akademiksresource.dto;

import java.util.HashSet;
import java.util.Set;

import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.exception.AcademicException;


public class AdminsDTO extends AppUserDTO {
	
	public AdminsDTO() {
		super();
	}

	public AdminsDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);
		
	}

	public AdminsDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);
		
	}

//	@Override
//	public Set<Roles> getUserRoles() {
//		
//		Set<Roles> roles = null;
//		try {
//			roles = new HashSet<>();
//			for(String role: super.getRoles()) {
//				roles.add(Roles.valueOf(role));
//			}
//		} catch (Exception e) {
//			throw new AcademicException("Unsupported role", Exceptions.BAD_REQUEST.name());
//		}
//		
//		
//		return roles;
//	}
	

	
}

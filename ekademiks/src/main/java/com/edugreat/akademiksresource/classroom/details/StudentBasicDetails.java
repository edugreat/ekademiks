package com.edugreat.akademiksresource.classroom.details;

import com.edugreat.akademiksresource.model.Student;

public record StudentBasicDetails(
		Integer id,
		String firstName,
		String lastName,
		String email,
		String mobileNumber,
		String status,
		InstitutionBasicDetails institution
		
		) {
	
	public StudentBasicDetails(Student student) {
		
		this(student.getId(), student.getFirstName(), student.getLastName(),student.getEmail(),student.getMobileNumber(),student.getStatus(),
				
				student.getInstitution() != null ? new InstitutionBasicDetails(student.getInstitution()) : null);
		
	}

}

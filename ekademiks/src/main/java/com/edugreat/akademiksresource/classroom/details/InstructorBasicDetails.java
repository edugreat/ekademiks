package com.edugreat.akademiksresource.classroom.details;

import java.util.Set;

import com.edugreat.akademiksresource.instructor.Instructor;

public record InstructorBasicDetails(
		Integer id,
		String firstName,
		String lastName,
		String email,
		String mobileNumber,
		Set<String> roles
		) {
	
	public InstructorBasicDetails(Instructor instructor) {
		
		this(instructor.getId(), instructor.getFirstName(), instructor.getLastName(),instructor.getEmail(), instructor.getMobileNumber(), instructor.getRoles());
	}

}

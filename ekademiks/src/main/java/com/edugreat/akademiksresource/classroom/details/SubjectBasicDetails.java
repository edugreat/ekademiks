package com.edugreat.akademiksresource.classroom.details;

import com.edugreat.akademiksresource.classroom.ClassroomSubject;

public record SubjectBasicDetails(
		
		Integer id,
		String name,
		InstructorBasicDetails instructor
		
		) {

	public SubjectBasicDetails(ClassroomSubject cs){
		
		this(cs.getId().getSubjectId(), cs.getSubject().getSubjectName(), cs.getInstructor() !=null ? new InstructorBasicDetails(cs.getInstructor()) : null);
	}
}

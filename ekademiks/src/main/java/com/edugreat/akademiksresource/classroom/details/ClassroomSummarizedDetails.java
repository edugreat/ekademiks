package com.edugreat.akademiksresource.classroom.details;

import java.util.Set;
import java.util.stream.Collectors;

import com.edugreat.akademiksresource.classroom.Classroom;

// Data served o the primary instructor of a classroom
public record ClassroomSummarizedDetails(
		Integer id,
		String name,
		String level,
		Integer academicYear,
		String creationDate,
		Integer studentCount,
		String primaryInstructorName,
		String lastModifiedDate,
		String lastModifiedBy,
		Set<SubjectBasicDetails> assignedSubjects
		
		) {
	
	public ClassroomSummarizedDetails(Classroom classroom) {
		
		this(
			classroom.getId(),
			classroom.getName(),
			classroom.getLevel().getCategory().name(),
			classroom.getAcademicYear(),
			classroom.getCreationDate().toString(),
			classroom.getActiveStudents().size(),
			classroom.getPrimaryInstructor() != null ?
					classroom.getPrimaryInstructor().getFirstName()+" "+classroom.getPrimaryInstructor().getLastName() :"Not Assigned",
			classroom.getLastModified().toString(),
			classroom.getLastModifiedBy(),
			classroom.getClassroomSubjects()
			.stream()
			.map(SubjectBasicDetails::new)
			.collect(Collectors.toSet())
					
					
					
				
				
				
				
		  );
	}

}

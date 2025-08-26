package com.edugreat.akademiksresource.classroom.details;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.edugreat.akademiksresource.classroom.Classroom;

// Data served to Institution ADMIN on the classroom details page
public record ClassroomFullDetails(
		ClassroomBasicDetails classroom,
		InstructorBasicDetails primaryInstructor,
		Set<InstructorBasicDetails> allInstructors,
		Set<SubjectBasicDetails> subjects,
		Set<StudentBasicDetails> students,
		Integer studentCount,
		LocalDateTime lastUpdated
		) {
	
      public ClassroomFullDetails(Classroom classroom) {
		
		this(
				new ClassroomBasicDetails(classroom),
				classroom.getPrimaryInstructor() != null ? new InstructorBasicDetails(classroom.getPrimaryInstructor()) : null,
						
				classroom.getAllInstructors().stream()
				.map(InstructorBasicDetails::new)
				.collect(Collectors.toSet()),
				classroom.getClassroomSubjects()
				.stream().map(SubjectBasicDetails::new)
				.collect(Collectors.toSet()), 
				classroom.getActiveStudents().stream()
				.map(StudentBasicDetails::new)
				.collect(Collectors.toSet()), 
				classroom.getActiveStudents().size(), 
				classroom.getLastModified()
				);
	}
	
	
	

}

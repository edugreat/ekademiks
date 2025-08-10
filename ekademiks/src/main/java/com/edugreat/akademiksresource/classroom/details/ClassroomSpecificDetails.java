package com.edugreat.akademiksresource.classroom.details;

import java.util.Set;
import java.util.stream.Collectors;

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.classroom.StudentElectiveEnrollment;

// Data served to instructors assigned to manage specific subjects in a classroom
public record ClassroomSpecificDetails
      (
      Set<SubjectBasicDetails> assignedSubjects,
      Long studentCount,
      Set<StudentBasicDetails> managedStudents
		
		
		) {
	
	   public ClassroomSpecificDetails(Classroom classroom, Integer instructorId) {
		   
		   this(
				classroom.getClassroomSubjects()
				.stream()
				.filter(cs -> cs.getInstructor().getId().equals(instructorId))
				.map(SubjectBasicDetails::new)
				.collect(Collectors.toSet()),
				
				classroom.getClassroomSubjects()
				.stream()
				.filter(cs -> cs.getInstructor().getId().equals(instructorId))
				.count(),
				
				classroom.getClassroomSubjects()
				.stream()
				.filter(cs -> cs.getInstructor().getId().equals(instructorId))
				.flatMap(cs -> cs.getElectiveEnrollments().stream())
				.map(StudentElectiveEnrollment::getStudent)
				.map(StudentBasicDetails::new)
				.collect(Collectors.toSet())
				
				
				
				   
			  );
	   }
	   
	 
}

package com.edugreat.akademiksresource.classroom;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnrollmentRequest(
		
		@NotNull(message = "Provide identifiers of students to enroll")
          List<Integer> studentIds,
          
          @Positive(message = "Missing data about the enrolling officer.")
          Integer enrollmentOfficer,
          
          @Positive(message = "Classroom identifier not provided")
          Integer classroomId,
          
          @Positive(message = "No identification found for your institution")
          Integer institutionId
          
		
		
		
		
		
		) {

}

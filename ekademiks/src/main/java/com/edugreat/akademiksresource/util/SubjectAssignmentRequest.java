package com.edugreat.akademiksresource.util;

import jakarta.validation.constraints.Positive;

public record SubjectAssignmentRequest(
		
		@Positive(message = "Invalid subject identifier")
		Integer subjectId,
		@Positive(message = "Invalid instructor identifier")
		Integer InstructorId
		
		) {

}

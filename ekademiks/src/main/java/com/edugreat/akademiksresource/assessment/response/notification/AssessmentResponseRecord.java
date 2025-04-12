package com.edugreat.akademiksresource.assessment.response.notification;

import java.time.LocalDate;

// stores information such regarding the assessment or assignment the student has attempted

 
/**
 * topic: topic for the assessment/assignment
 * postedOn: when the assessment/assignment was posted
 * respondedOn: when the student attempted the assignment
 * studentId: ID of the student
 */
public record AssessmentResponseRecord(
		String topic, 
		LocalDate postedOn,
		LocalDate respondedOn, 
		Integer studentId,
		Integer instructorId)
{

}

package com.edugreat.akademiksresource.assessment.response.notification;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// stores information such regarding the assessment or assignment the student has attempted

 
/**
 * topic: topic for the assessment/assignment
 * postedOn: when the assessment/assignment was posted
 * respondedOn: when the student attempted the assignment
 * studentId: ID of the student
 */

 @Builder()
 @NoArgsConstructor
 @AllArgsConstructor
 @Setter
 @Getter
 @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentResponseRecord{
	 @EqualsAndHashCode.Include
	private String topic;
	
	private LocalDate postedOn;
	
	@EqualsAndHashCode.Include
	private LocalDate respondedOn;
	
	@EqualsAndHashCode.Include
	private Integer studentId;
	@EqualsAndHashCode.Include
	private Integer instructorId;
	
	
	
}




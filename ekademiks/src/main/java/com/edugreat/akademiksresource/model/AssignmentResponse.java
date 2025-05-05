package com.edugreat.akademiksresource.model;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class AssignmentResponse{
	
	private Integer assignmentDetailsId;
	
	private LocalDate postedOn;
	
	LocalDate submittedOn = LocalDate.now();
	
	private double score;
	
	private Integer instructorId;
	
	private String topic;
	

	public AssignmentResponse(Integer assignmentDetailsId, double score, 
			Integer instructorId, LocalDate postedOn, String topic) {
		this.assignmentDetailsId = assignmentDetailsId;
		this.postedOn = postedOn;
		this.score = score;
		this.instructorId = instructorId;
		this.topic = topic;
	}

	public Integer getAssignmentDetailsId() {
		return assignmentDetailsId;
	}

	public double getScore() {
		return score;
	}
	
	public Integer getInstructorId() {
		
		return instructorId;
	}
	
	public LocalDate getPostedOn(){
		
		return postedOn;
		
	}
	
	public String getTopic() {
		
		return topic;
	}

	public LocalDate getSubmittedOn() {
		return submittedOn;
	}
	
	
}


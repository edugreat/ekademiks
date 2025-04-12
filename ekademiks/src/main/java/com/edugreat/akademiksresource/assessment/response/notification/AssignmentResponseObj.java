package com.edugreat.akademiksresource.assessment.response.notification;

import java.util.ArrayList;
import java.util.List;

// object that wraps student's response to assignments, used for computing their performance
public class AssignmentResponseObj {
	
//  students selected options
	private List<String> selectedOptions = new ArrayList<>();
	
//	unique identifier for the student who took the assignment
	private Integer studentId;
	
	public AssignmentResponseObj(List<String> selectedOptions) {
		this.selectedOptions = selectedOptions;
		
	}
	
	public AssignmentResponseObj() {}

	

	public List<String> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(List<String> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	public void setStudentId(Integer studentId) {
		
		this.studentId = studentId;
	}
	
	public Integer getStudentId() {
		
		return studentId;
	}
	
}

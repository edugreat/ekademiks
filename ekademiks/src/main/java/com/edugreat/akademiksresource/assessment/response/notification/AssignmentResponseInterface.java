package com.edugreat.akademiksresource.assessment.response.notification;

import java.util.List;

// interface that provides contracts for processing assessment response as well as send notifications
// to appropriate broadcasting channels
public interface AssignmentResponseInterface {
	
//	type tells the the type of assignment(OBJ, THEORY PDF). detailsId uniquely reveals details about the assignment
	void processAssignmentResponse(AssignmentResponseObj response, String type, Integer detailsId);
	
//	returns previous notifications on assignment respondent(information about students' attempts to assessments
	List<AssessmentResponseRecord> getPreviousResponses(Integer instructorId);

}

package com.edugreat.akademiksresource.assessment.response.notification;

import java.util.Set;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//  interface that provides implementable contracts for establishing connection to notifications such as : students' responses to assessments and assignments
public interface AssessmentResponseConnector {
	
//	connects instructor to the network for receiving notifications on students' response to assessments
	SseEmitter establishConnection(Integer instructorId);
	
//	returns all connected instructors ID for scheduled notifications
	Set<Integer> getConnectedInstructorsId();
	
	

}

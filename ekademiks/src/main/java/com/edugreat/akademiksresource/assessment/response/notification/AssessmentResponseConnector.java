package com.edugreat.akademiksresource.assessment.response.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//  interface that provides implementable contracts for establishing connection to notifications such as : students' responses to assessments and assignments
public interface AssessmentResponseConnector {
	
//	connects instructor to the network for receiving notifications on students' response to assessments
	SseEmitter establishConnection(Integer instructorId);
	
	

}

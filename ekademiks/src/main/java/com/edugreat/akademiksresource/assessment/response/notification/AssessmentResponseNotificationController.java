package com.edugreat.akademiksresource.assessment.response.notification;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class AssessmentResponseNotificationController {
	
	private final AssignmentResponseInterface responseInterface;
	
	private final AssignmentResponseBroadcaster broadcaster;
	
	private final AssessmentResponseConnector connector;
	
	
	
	
	public AssessmentResponseNotificationController(AssignmentResponseInterface responseInterface,
			AssignmentResponseBroadcaster broadcaster, AssessmentResponseConnector connector) {
		this.responseInterface = responseInterface;
		this.broadcaster = broadcaster;
		this.connector = connector;
	}




	@GetMapping("/admins/assessment/notify_me")
	public SseEmitter connectToNotifications(@RequestParam("_xxid") String instructorId ) {
		
	
//		establish first connection
		SseEmitter emitter = connector.establishConnection(Integer.parseInt(instructorId));
		
//		publish previous notifications
		if(emitter != null) {
			
			
			
			broadcaster.broadcastPreviousNotifications(responseInterface.getPreviousResponses(Integer.parseInt(instructorId)));
		}
		
		return emitter;
	}
	
	@PostMapping("/tests/post-reponse")
	public HttpStatus processAssignmentResponse(@RequestBody AssignmentResponseObj response,
			@RequestHeader String type, @RequestHeader String detailsId) {
		
		try {
			
			responseInterface.processAssignmentResponse(response, type, Integer.parseInt(detailsId));
		
			return HttpStatus.OK;
		} catch (Exception e) {
			
			System.out.println(e);
			
			return HttpStatus.BAD_REQUEST;
		}
		
		
	}
	
	
	
	

}

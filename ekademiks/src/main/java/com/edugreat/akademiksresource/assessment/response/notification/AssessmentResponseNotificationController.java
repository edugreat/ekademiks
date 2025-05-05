package com.edugreat.akademiksresource.assessment.response.notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Assessment response notification", description = "Endpoints for managing notifications on student assessment attempts")
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
	@Operation(summary = "connect to notification", description = "Connect logged in instructor to this notification")
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
	@Operation(summary = "process response", description = "upload new assessment response for processing")
	@ApiResponses(value = {
			@ApiResponse(responseCode =  "200", description = "submission received successfullly"),
			@ApiResponse(responseCode = "200", description = "Request failure")
	})
	public ResponseEntity<Object> processAssignmentResponse(@RequestBody AssignmentResponseObj response,
			@RequestHeader String type, @RequestHeader String detailsId) {
		
		try {
			
			responseInterface.processAssignmentResponse(response, type, Integer.parseInt(detailsId));
		
			
		} catch (Exception e) {
			
			
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Ok", HttpStatus.BAD_REQUEST);
	}
	
	
	
	

}

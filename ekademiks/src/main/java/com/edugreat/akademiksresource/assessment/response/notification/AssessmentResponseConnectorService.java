package com.edugreat.akademiksresource.assessment.response.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// service that provides implementations to contracts of the interface, as well as receive and broadcasts notifications to connected concerned instructors
@Service
public class AssessmentResponseConnectorService implements AssessmentResponseConnector {

//	thread-safe map of connectors whose keys are instructo's ID and values are their connection to notifications
	private Map<Integer, SseEmitter> connectors = new ConcurrentHashMap<>();
	
	
	@Override
	public SseEmitter establishConnection(Integer instructorId) {
		
		
		
		final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		
		connectors.putIfAbsent(instructorId, emitter);
		
		emitter.onError(e -> connectors.remove(instructorId));
		emitter.onCompletion(() -> connectors.remove(instructorId));
		
		emitter.onTimeout(() -> emitter.complete());
		
		System.out.println("connection made");
		
		return emitter;
	}
	
	
//	publish previous notifications to the instructor whose ID is referenced, if connected. The key of the map is the total number of respondent to the assignment so far
	@RabbitListener(queues = {"${previous.assessment.response.notification.queue}"})
	void publishReviousNotifications(List<AssessmentResponseRecord> notifications) throws IOException {
		
		System.out.println("publishing previous notifications");
		final Integer recipientId = notifications.get(0).instructorId();
		if(connectors.containsKey(recipientId)) {
			
			
			connectors.get(recipientId).send(SseEmitter.event().data(notifications).name("responseUpdate"));
			
		}
	}
	
//	publish instant notifications to instructor
	@RabbitListener(queues = {"${instant.assessment.response.notification.queue}"})
	void publishInstantNotification(AssessmentResponseRecord notification) throws IOException {
		
		System.out.println("publishing instant notification");
		
		if(connectors.containsKey(notification.instructorId())) {
			
			
			
			connectors.get(notification.instructorId()).send(SseEmitter.event().data(notification).name("responseUpdate"));
		
			System.out.println("notification sent successfully");
		}
		
		
		
		
		
	}
	

}

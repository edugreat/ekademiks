package com.edugreat.akademiksresource.assessment.response.notification;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// service that provides implementations to contracts of the interface, as well as receive and broadcasts notifications to connected concerned instructors
@Service
public class AssessmentResponseConnectorService implements AssessmentResponseConnector {

//	thread-safe map of connectors whose keys are instructo's ID and values are their connection to notifications
	private Map<Integer, SseEmitter> connectors = new ConcurrentHashMap<>();
	
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	
	@Override
	public SseEmitter establishConnection(Integer instructorId) {
		
		
		
		final SseEmitter emitter = new SseEmitter(0L);
		
		
		
		connectors.put(instructorId, emitter);
		
		scheduleHeartbeat(emitter);
		
		emitter.onError(e ->System.out.println("emitter error: "+e));
		emitter.onCompletion(() -> connectors.remove(instructorId));
		
		emitter.onTimeout(() -> emitter.complete());
		
		System.out.println("connection made");
		
		return emitter;
	}
	
	
//	publish previous notifications to the instructor whose ID is referenced, if connected. The key of the map is the total number of respondent to the assignment so far
	@RabbitListener(queues = {"${previous.assessment.response.notification.queue}"})
	void publishReviousNotifications(AssessmentResponseRecord notification) throws IOException {
		
		System.out.println("publishing previous notifications");
		final Integer recipientId = notification.getInstructorId();
		if(connectors.containsKey(recipientId)) {
			System.out.println("contains ID");
			
			connectors.get(recipientId).send(SseEmitter.event().data(notification).name("responseUpdate"));
			
		}
	}
	
//	publish instant notifications to instructor
	@RabbitListener(queues = {"${instant.assessment.response.notification.queue}"})
	void publishInstantNotification(AssessmentResponseRecord notification) throws IOException {
		
		System.out.println("id: "+notification.getInstructorId());
		System.out.println("publishing instant notification---");
		
		if(connectors.containsKey(notification.getInstructorId())) {
			
			System.out.println("notification sent successfully");
			
			connectors.get(notification.getInstructorId()).send(SseEmitter.event().data(notification).name("responseUpdate"));
		}
		
		
		
		
		
	}
	
	private void scheduleHeartbeat(SseEmitter emitter) {
		
		
		
		executorService.scheduleAtFixedRate(() -> {
			
			try {
				emitter.send(SseEmitter.event().comment("").name("heartbeat"));
			} catch (IOException e) {
				System.out.println("error sending heartbeat: "+e);
			}
			
			
		}, 30, 30, TimeUnit.SECONDS);
		
	}
	

}

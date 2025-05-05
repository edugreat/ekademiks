package com.edugreat.akademiksresource.assessment.response.notification;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

// service that provides implementations to contracts of the interface, as well as receive and broadcasts notifications to connected concerned instructors
@Service
@Slf4j
public class AssessmentResponseConnectorService implements AssessmentResponseConnector {

//	thread-safe map of connectors whose keys are instructo's ID and values are their connection to notifications
	private Map<Integer, SseEmitter> connectors = new ConcurrentHashMap<>();
	
//	thread-safe  heart-beat executors
	private Map<Integer, ScheduledExecutorService> heartbeatExecutors = new ConcurrentHashMap<>();
	
	
	
	
	@Override
	public SseEmitter establishConnection(Integer instructorId) {
		
		
		
		final SseEmitter emitter = new SseEmitter(0L);
		
		
		
		connectors.put(instructorId, emitter);
		
		
	ScheduledExecutorService executor = 	scheduleHeartbeat(connectors.get(instructorId));
		
	heartbeatExecutors.put(instructorId, executor);
	
		emitter.onError(e -> cleanup(instructorId));
		emitter.onCompletion(() -> cleanup(instructorId));
		
		emitter.onTimeout(() -> cleanup(instructorId));
		
		log.info("connection made");
		
		return emitter;
	}
	
	
//	publish previous notifications to the instructor whose ID is referenced, if connected. The key of the map is the total number of respondent to the assignment so far
	@RabbitListener(queues = {"${previous.assessment.response.notification.queue}"})
	void publishReviousNotifications(AssessmentResponseRecord notification) throws IOException {
		
		
		final Integer recipientId = notification.getInstructorId();
		if(connectors.containsKey(recipientId)) {
			
			log.info("publishing previous notifications to: {}", recipientId);
			
			connectors.get(recipientId).send(SseEmitter.event().data(notification).name("responseUpdate"));
			
		}
	}
	
//	publish instant notifications to instructor
	@RabbitListener(queues = {"${instant.assessment.response.notification.queue}"})
	void publishInstantNotification(AssessmentResponseRecord notification) throws IOException {
		
		System.out.println("id: "+notification.getInstructorId());
		System.out.println("publishing instant notification---");
		
		if(connectors.containsKey(notification.getInstructorId())) {
			
			connectors.get(notification.getInstructorId()).send(SseEmitter.event().data(notification).name("responseUpdate"));

		}
		
		
		
		
		
	}
	
	@Override
	public Set<Integer> getConnectedInstructorsId(){
		
		return new HashSet<>(connectors.keySet());
		
	}
	
	private ScheduledExecutorService scheduleHeartbeat(SseEmitter emitter) {
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		
		
		executorService.scheduleAtFixedRate(() -> {
			
			try {
				emitter.send(SseEmitter.event().comment("").name("heartbeat"));
			} catch (IOException e) {
				log.info("heartbeat error: {}", e);
			}
			
			
		}, 30, 30, TimeUnit.SECONDS);
		
		
		return executorService;
	}
	
//	cleanup code after emitter error and timeouts to avoid concurrency issues
	private void cleanup(Integer instructorId) {
		
		log.info("performing cleanup");
		
		connectors.remove(instructorId);
		
		ScheduledExecutorService heartbeatExecutor = heartbeatExecutors.remove(instructorId);
		if(heartbeatExecutor != null) {
			
			heartbeatExecutor.shutdown();
		}
	}
	

}

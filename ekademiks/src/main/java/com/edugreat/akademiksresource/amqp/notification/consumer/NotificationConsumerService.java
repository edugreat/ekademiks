package com.edugreat.akademiksresource.amqp.notification.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumerService implements NotificationConsumer {

	private static final long HEARTBEAT_INTERVAL = 30000;//30 seconds heart-beat interval

	private Map<Integer, SseEmitter> clients = new ConcurrentHashMap<>();
	
//	thread-safe heart-beat executor service
	private Map<Integer, ScheduledExecutorService> heartbeatExecutors = new ConcurrentHashMap<>();
	
	
	@RabbitListener(queues = { "${previous.notification.queue}" })
	 void consumePreviousNotification(AssessmentUploadNotification notification) {
		
//		get the receipient of this notification
		final Integer studentId = notification.getReceipientIds().get(0);

		if (studentId != null && clients.containsKey(studentId)) {

			try {
				notify(notification, studentId);
			} catch (IOException e) {
				
				
				log.info("error notifying: {}", studentId);
				
				
			}

		}

	}

	@RabbitListener(queues = { "${instant.notification.queue}" })
	 void consumeInstantNotification(AssessmentUploadNotification notification) {
		
		

//		get the receipient of this notification
		final List<Integer> recipientIds = notification.getReceipientIds();
		
		

//		 A case where the notification is meant for all users
		if (recipientIds == null || recipientIds.size() == 0) {
			
			

				clients.forEach((studentId, emitter) -> {
					
					try {
						notify(notification, studentId);
					} catch (IOException e) {
						
						log.info(String.format("Error notifying %s", studentId));
						
						
					}
					
									
				});
				
		


//				a case where notification is meant for some currently logged in students
		}else if(recipientIds.size() > 0) {
      recipientIds.forEach(id -> {
				
				if(clients.containsKey(id)) {
					
					try {
						
						notify(notification, id);
					} catch (Exception e) {
						

					}
				}
			});
			
		}
		

	}

	@Override
public SseEmitter establishConnection(Integer studentId) {
		
		
		
		final SseEmitter emitter = new SseEmitter(0L);
		
		
		
		clients.put(studentId, emitter);
		
		
	ScheduledExecutorService executorService = 	startHeartbeat(emitter);
	
	heartbeatExecutors.put(studentId, executorService);
		
		emitter.onError(e -> {
			cleanup(studentId);
			log.info("emitter error: {},", e.getMessage());
		});
		emitter.onCompletion(() -> {
			cleanup(studentId);
			log.info("emitter completed for: {}", studentId);
		});
		
		emitter.onTimeout(() -> {
			cleanup(studentId);
			log.info("emitter timedout for: {}", studentId);
		});
		
		
		
		return emitter;
	}

	private void notify(AssessmentUploadNotification notification, final Integer studentId) throws IOException {

		clients.get(studentId).send(SseEmitter.event().data(notification).name("notifications"));

	}

	@Override
	public synchronized  SseEmitter disconnectFromSSE(Integer studentId) {
		
		if(clients.containsKey(studentId)) {
			
			return clients.remove(studentId);
		}
		
		return null;
	}
	
	 private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {  
	        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();  
	        executorService.scheduleAtFixedRate(() -> {  
	            try {  
	                emitter.send(SseEmitter.event().comment("heartbeat").name("heartbeat"));  
	            } catch (IOException e) {  
	                log.error("Error sending heartbeat: " + e.getMessage());  
	               
	            }  
	        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);  
	        
	        return executorService;
	    }
	 
//	 cleans up tasks after encountering emitter errors, to avoid concurrency issues
	 private void cleanup(Integer studentId) {
		 
		 clients.remove(studentId);
		 
		 ScheduledExecutorService scheduledExecutor = heartbeatExecutors.remove(studentId);
		 if(scheduledExecutor != null) {
			 
			 scheduledExecutor.shutdown();
		 }
		 
		 
	 }

}

package com.edugreat.akademiksresource.amqp.notification.consumer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumerService implements NotificationConsumer {

	private static final long HEARTBEAT_INTERVAL = 30000;//30 seconds heart-beat interval

	private static Map<Integer, SseEmitter> clients = new ConcurrentHashMap<>();
	
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
		
		

//		get the recipient of this notification
		final List<Integer> recipientIds = notification.getReceipientIds();
		

//		 A case where the notification is meant for all users
		if (recipientIds == null || recipientIds.size() == 0) {
			
			
			
			for(Map.Entry<Integer, SseEmitter> entry: clients.entrySet()) {
				
				final SseEmitter emitter = entry.getValue();
				final Integer userId = entry.getKey();
				
				if(!isConnectionAlive(userId, emitter)) {
					
					cleanup(userId);
					
					continue;
				}
				
				
				try {
					
					notify(notification, userId);
					
				} catch (Exception e) {
					log.info(String.format("Error notifying :{}", userId));
				}
				
				
			}
				
		


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
		
		if(clients.containsKey(studentId)) return clients.get(studentId);
		
		
		
		final SseEmitter emitter = new SseEmitter(0L);
		
		
		
		clients.put(studentId, emitter);
		
		
	ScheduledExecutorService executorService = 	startHeartbeat(emitter, studentId);
	
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
	public SseEmitter disconnectFromSSE(Integer studentId) {
		
		if(clients.containsKey(studentId)) {
			
			return clients.remove(studentId);
		}
		
		return null;
	}
	
	 private  ScheduledExecutorService startHeartbeat(SseEmitter emitter, Integer connectionId) {  
	        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();  
	        executorService.scheduleAtFixedRate(() -> {  
	        	
	        	if(clients.containsKey(connectionId)) {
	        		
	        		if(!isConnectionAlive(connectionId, emitter)) {
	        			
	        			cleanup(connectionId);
	        			
	        			return;
	        		}
            		
            		try {
						clients.get(connectionId).send(SseEmitter.event().comment("heartbeat").name("heartbeat"));
					} catch (IOException e) {
						log.error("Error sending heartbeat:{}",  connectionId);
						cleanup(connectionId);
						return;
					}
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
	 
//	 checks if the user connection is still alive
	 private boolean isConnectionAlive(Integer userId, SseEmitter emitter) {
		
		 try {
			emitter.send(SseEmitter.event().comment("ping"));
			
			return true;
		} catch (IOException e) {
			
			log.info("lost connection for notification");
			
			cleanup(userId);
			
			return false;
			
		}
		 
	 }

}

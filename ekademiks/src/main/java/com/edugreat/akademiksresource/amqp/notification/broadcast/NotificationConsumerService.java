package com.edugreat.akademiksresource.amqp.notification.broadcast;

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

@Service
public class NotificationConsumerService implements NotificationConsumer {

	private static final long HEARTBEAT_INTERVAL = 30000;//30 seconds heart-beat interval

	private Map<Integer, SseEmitter> clients = new ConcurrentHashMap<>();
	
	private final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumerService.class);

	@RabbitListener(queues = { "${notification.queue}" })
	 void consumePreviousNotification(AssessmentUploadNotification notification) {

		
		
//		get the receipient of this notification
		final Integer studentId = notification.getReceipientIds().get(0);

		if (studentId != null && clients.containsKey(studentId)) {

			try {
				notify(notification, studentId);
			} catch (IOException e) {
				
				clients.remove(studentId);
				
				LOGGER.info(String.format("Error notifying %s", studentId));
				
				
			}

		}

	}

	@RabbitListener(queues = { "${notification.queue}" })
	 void consumeInstantNotification(AssessmentUploadNotification notification) {

//		get the receipient of this notification
		final List<Integer> receipientIds = notification.getReceipientIds();
		
		List<Integer> toBeRemoved = new ArrayList<>();

//		 A case where the notification is meant for all users
		if (receipientIds == null || receipientIds.size() == 0) {
			
			

				clients.forEach((studentId, emitter) -> {
					
					try {
						notify(notification, studentId);
					} catch (IOException e) {
						
						LOGGER.info(String.format("Error notifying %s", studentId));
						toBeRemoved.add(studentId);
						
					}
					
									
				});
				
		

//			removes the client that caused the exception
			toBeRemoved.forEach(clients::remove);
		}

	}

	@Override
	public SseEmitter establishConnection(Integer studentId) {

		SseEmitter emitter = new SseEmitter(1000L * 20 * 60);

		clients.putIfAbsent(studentId, emitter);

		emitter.onCompletion(() -> {

			clients.remove(studentId);
		});

		emitter.onTimeout(() -> emitter.complete());

		emitter.onError(e -> {

			clients.remove(studentId);
			LOGGER.info(String.format("Error establishing connection %s", e.getMessage()));
		});
		
		//startHeartbeat(emitter, studentId);
		

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
	
	 private void startHeartbeat(SseEmitter emitter, Integer studentId) {  
	        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();  
	        executorService.scheduleAtFixedRate(() -> {  
	            try {  
	                emitter.send(SseEmitter.event().data("heartbeat").name("heartbeat"));  
	            } catch (IOException e) {  
	                LOGGER.error("Error sending heartbeat: " + e.getMessage());  
	                clients.remove(studentId); // Remove the emitter if it fails  
	                executorService.shutdown(); // Stop the heartbeat task if emitter is no longer valid  
	            }  
	        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);  
	    }

}

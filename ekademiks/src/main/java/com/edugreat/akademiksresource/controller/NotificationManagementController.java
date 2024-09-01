package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.dao.NotificationDao;
import com.edugreat.akademiksresource.dto.NotificationsResponseDTO;
import com.edugreat.akademiksresource.model.Notification;

//   This controller handles notifications communications to the clients which have connected to be notified.
@RestController
public class NotificationManagementController {
//	The id reference to the student who gets notified

//	Inject the Notification repository
	@Autowired
	private NotificationDao notificationsRepo;

//	List of connected clients awaiting notifications
	private Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

//	Server notification endpoint
	@GetMapping("/notice/notify_me")
	public SseEmitter streamNotificaions(@RequestParam Integer studentId) throws IOException {

		// Get the authentication object, just to ensure only authenticated students get
		// the notifications
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//		Notification service is designed for authenticated users only
		if (authentication != null) {

			
//			Creates new emitter with a longer timeout(5 minutes) that emits to the students upon login
			SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

			emitters.put(studentId, emitter);

//			Periodic heartbeats sent to  client to keep connection alive
			conectionHeartBeat(studentId, emitter);
			
//			Send all unread messages to the student upon login
			List<Notification> unreadNotifications = notificationsRepo.getUnreadNotificationsFor(studentId);
			unreadNotifications.forEach(notification -> {
//				Create notification response object
				NotificationsResponseDTO theNotification = mapToNotificationResponse(notification);

				try {
					emitter.send(SseEmitter.event().data(theNotification).name("notifications"));
				} catch (IOException e) {
					emitters.remove(studentId);
				}
			});

//			Once events is completed or is timed out, remove the emitter
			emitter.onCompletion(() -> emitters.remove(studentId));
			emitter.onTimeout(() -> emitters.remove(studentId));

//			Returns the emitter to the client just to establish open communication channel
			return emitter;
		}

		return null;

	}



//	Method to sent instant(eg, notification sent while the student is yet logged in notification to the student
	public void sendNotification(Notification notification, Integer studentId) {
//		Get the student's communication channel
		SseEmitter emitter = emitters.get(studentId);
//		Send notification if communication channel is still active
		if (emitter != null) {
//			Create notification response
			NotificationsResponseDTO theNotification = mapToNotificationResponse(notification);
			try {
				emitter.send(SseEmitter.event().data(theNotification).name("notifications"));
			} catch (Exception e) {
//				Removes notification emitter for the student if there is error
				emitters.remove(studentId);

//				Completes current emitter upon error. Let the client re-establish connection
				emitter.complete();
			}
		}

	}

	private NotificationsResponseDTO mapToNotificationResponse(Notification notification) {
//	Get a local date time of the format YYYY-MM-DD, 10 character string
		String createdAt = String.valueOf(notification.getCreatedAt()).substring(0, 11);

		return new NotificationsResponseDTO(notification.getId(), notification.getType(),notification.getMessage(), createdAt);
	}
	
//	Sends periodic heartbeat to the client to keep connection alive
	
	private void conectionHeartBeat(Integer studentId, SseEmitter emitter) {
		
		Runnable heartbeat =() ->{
			
			try {
				emitter.send(SseEmitter.event().comment("heartbeat"));
			} catch (Exception e) {
			
				emitters.remove(studentId);
			}
		};
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		executor.scheduleAtFixedRate(heartbeat, 0, 2, TimeUnit.MINUTES);
	}
}

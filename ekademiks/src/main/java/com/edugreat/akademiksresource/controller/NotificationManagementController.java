package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.dao.AssessmentNotificationDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.NotificationsResponseDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Notification;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;

//   This controller handles notifications communications to the clients which have connected to be notified.
@RestController
public class NotificationManagementController {


//	Inject the AssessmentUploadNotification repository
	@Autowired
	private AssessmentNotificationDao assessmentNotificationsDao;

//	List of connected clients awaiting notifications
	private Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Autowired
	private StudentDao studentDao;

//	Server notification endpoint
	@GetMapping("/notice/notify_me")
	public SseEmitter streamNotificaions(@RequestParam Integer studentId) throws IOException {

		// Get the authentication object, just to ensure only authenticated students get
		// the notifications
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//		AssessmentUploadNotification service is designed for authenticated users only
		if (authentication != null) {

//			Creates new emitter with a longer timeout(30 minutes) that emits to the students upon login
			SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

			emitters.put(studentId, emitter);

//			Periodic heartbeats sent to  client to keep connection alive
			conectionHeartBeat(studentId, emitter);

//			Send all unread assessment notifications to the student upon login
			List<AssessmentUploadNotification> unreadAssessmentNotifications = assessmentNotificationsDao
					.getUnreadNotificationsFor(studentId);

			unreadAssessmentNotifications.forEach(notification -> {
//				Create notification response object
				NotificationsResponseDTO theNotification = mapToNotificationResponse(notification);

				try {
					emitter.send(SseEmitter.event().data(theNotification).name("notifications"));
				} catch (IOException e) {
					emitters.remove(studentId);
				}
			});

//			cleanup code that deletes notifications when all the students have read them
			List<AssessmentUploadNotification> notifications = assessmentNotificationsDao.findAll();
			removeAllReadNotifications(notifications);

//			Once events is completed or is timed out, remove the emitter
			emitter.onCompletion(() -> emitters.remove(studentId));
			emitter.onTimeout(() -> emitters.remove(studentId));

//			Returns the emitter to the client just to establish open communication channel
			return emitter;
		}

		return null;

	}

//	endpoint that removes notifications for the given student once they've read them
	@PatchMapping("/notice/read")
	@Transactional
	public void notificationIsRead(@RequestBody Integer notificationId, @RequestParam("studentId") String id) {

		Integer studentId = Integer.parseInt(id);

//		Fetches the notification if exists
		Optional<AssessmentUploadNotification> optional = assessmentNotificationsDao.findById(notificationId);

		Optional<Student> optionalStudent = studentDao.findById(studentId);

		if (optional.isPresent() && optionalStudent.isPresent()) {

//			get the notification
			AssessmentUploadNotification assessmentUploadNotification = optional.get();

//			get the notified student
			Student student = optionalStudent.get();

//			Remove the current notification from the student list of notifications
			student.getNotifications().remove(assessmentUploadNotification);

			studentDao.saveAndFlush(student);

		}

	}

//	Method to sent instant(eg, notification sent while the student is yet logged in notification to the student
	public void sendNotification(AssessmentUploadNotification assessmentUploadNotification, Integer studentId) {
//		Get the student's communication channel
		SseEmitter emitter = emitters.get(studentId);
//		Send notification if communication channel is still active
		if (emitter != null) {
//			Create notification response
			NotificationsResponseDTO theNotification = mapToNotificationResponse(assessmentUploadNotification);
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

//	Removes all read notifications
	@Transactional
	private <T extends Notification> void removeAllReadNotifications(List<T> notifications) {

//			List of notifications that have been read by every student, they should be deleted from the database
		List<AssessmentUploadNotification> staleNotifications = new ArrayList<>();

		notifications.forEach(notification -> {

//				get count of students yet to read the notification
			int unreadStudentCount = studentDao.getUnreadNotificationCountForStudents(notification.getId());

//				delete notification from the database if every student has read the notification
			if (unreadStudentCount == 0) {

//					mark for deletion
				staleNotifications.add((AssessmentUploadNotification) notification);

			}

		});

		if (staleNotifications.size() > 0) {

//				delete all stale notifications
			assessmentNotificationsDao.deleteAll(staleNotifications);
		}

	}

	private NotificationsResponseDTO mapToNotificationResponse(
			AssessmentUploadNotification assessmentUploadNotification) {
//	Get a local date time of the format YYYY-MM-DD, 10 character string
		String createdAt = String.valueOf(assessmentUploadNotification.getCreatedAt()).substring(0, 10);

		return new NotificationsResponseDTO(assessmentUploadNotification.getId(),
				assessmentUploadNotification.getMetadata(), assessmentUploadNotification.getType(),
				assessmentUploadNotification.getMessage(), createdAt);
	}

//	Sends periodic heartbeat to the client to keep connection alive

	private void conectionHeartBeat(Integer studentId, SseEmitter emitter) {

		Runnable heartbeat = () -> {

			try {
				emitter.send(SseEmitter.event().comment("heartbeat"));
			} catch (Exception e) {

				emitters.remove(studentId);
			}
		};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		executor.scheduleAtFixedRate(heartbeat, 0, 1, TimeUnit.MINUTES);
	}
}

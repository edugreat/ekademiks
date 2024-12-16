
package com.edugreat.akademiksresource.amq.notification.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.amqp.notification.broadcast.NotificationConsumer;
import com.edugreat.akademiksresource.amqp.notification.consumer.NotificationBroadcast;
import com.edugreat.akademiksresource.contract.NotificationInterface;
import com.edugreat.akademiksresource.dao.AssessmentNotificationDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;

//   This controller handles notifications communications to the clients which have connected to be notified.
@RestController
public class NotificationManagementController {

	@Autowired
	private NotificationInterface notificationInterface;

	@Autowired
	private NotificationBroadcast notificationBroadcast;

	@Autowired
	private NotificationConsumer notificationConsumer;

	@Autowired
	private AssessmentNotificationDao assessmentNotificationsDao;

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

//			delete all read notifications
			notificationInterface.deleteReadNotifications();

//			
			final SseEmitter emitter = notificationConsumer.establishConnection(studentId);

			if (emitter != null) {

				List<AssessmentUploadNotification> notifications = notificationInterface
						.unreadNotificationsFor(studentId);

				notifications.forEach(notification -> notification.setReceipientIds(List.of(studentId)));

				notificationBroadcast.previousNotification(notifications);
			}

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
			student.getAssessmentNotifications().remove(assessmentUploadNotification);

			studentDao.saveAndFlush(student);

		}

	}

}

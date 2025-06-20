
package com.edugreat.akademiksresource.amq.notification.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.amqp.notification.broadcast.NotificationBroadcast;
import com.edugreat.akademiksresource.amqp.notification.consumer.NotificationConsumer;
import com.edugreat.akademiksresource.contract.NotificationInterface;
import com.edugreat.akademiksresource.dao.AssessmentNotificationDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;

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
	@GetMapping(path = "/notice/notify_me", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<?>> streamNotificaions(@RequestParam Integer _xxid) throws IOException {

//			delete all read notifications
			notificationInterface.deleteReadNotifications();
			
			Flux<ServerSentEvent<?>> connection = notificationConsumer.establishConnection(_xxid);
			
			List<AssessmentUploadNotification> notifications = notificationInterface.unreadNotificationsFor(_xxid);
			
			notifications.forEach(n -> n.setReceipientIds(List.of(_xxid)));
			notificationBroadcast.getPreviousNotifications(notifications);
			
			 return connection;

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

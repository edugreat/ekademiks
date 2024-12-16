
package com.edugreat.akademiksresource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.amqp.notification.consumer.NotificationBroadcast;
import com.edugreat.akademiksresource.contract.NotificationInterface;
import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

//  Rest controller class that provides endpoint for the admin to post notifications which get persisted to the database, then
// notifies the student who are currently online about new assessment.
// Students get notified by an event emitter
@RestController
@RequestMapping("/admins/notify")
public class NotificationController {

	@Autowired
	private NotificationInterface notificationInterface;

	@Autowired
	private NotificationBroadcast notificationBroadcast;

	@PostMapping
	public ResponseEntity<Object> postAssessmentNotiification(@RequestBody NotificationRequestDTO notificationDTO) {

		if (notificationDTO != null) {

			AssessmentUploadNotification instantNotification = notificationInterface
					.postAssessmentNotification(notificationDTO);

			notificationBroadcast.instantNotification(instantNotification);

			return new ResponseEntity<>(HttpStatus.OK);

		}

		throw new IllegalArgumentException("Something went wrong!");
	}

}
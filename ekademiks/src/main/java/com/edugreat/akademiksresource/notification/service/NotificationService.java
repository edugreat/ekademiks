package com.edugreat.akademiksresource.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.NotificationInterface;
import com.edugreat.akademiksresource.dao.AssessmentNotificationDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Notification;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;

@Service
public class NotificationService implements NotificationInterface {

	@Autowired
	private AssessmentNotificationDao assessmentNotificationDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private InstitutionDao institutionDao;

	@Transactional
	public AssessmentUploadNotification postAssessmentNotification(NotificationRequestDTO dto,
			Integer receipientInstitutitonId) {

		AssessmentUploadNotification newNotification = mapToNotification(dto);

		// check if notification has been associated with the metadata
		var tempNotification = assessmentNotificationDao.findByMetadata(dto.getMetadata());
		if (tempNotification != null)
			throw new IllegalArgumentException("Suspected duplicate notifications");

		// Persist to the database;
		assessmentNotificationDao.save(newNotification);

		// Get creation time for this notification
		final LocalDateTime createdAt = newNotification.getCreatedAt();

		final List<String> audience = dto.getAudience();

		// if audience is null, then the notification is targeted for all students
		if (audience == null || audience.size() == 0) {

			// Associate each notification to each student in the database
			List<Student> students = studentDao.findAll();
			// Updating student's records using batch updates

			final int batchSize = 10;
			final int capacity = students.size();

			try {
				for (int i = 0; i < capacity; i += batchSize) {

					int end = Math.min(i + batchSize, capacity);

					var studentList = students.subList(i, end);

					studentList.forEach(student -> student.AddNotification(newNotification));
					studentDao.saveAllAndFlush(studentList);

				}
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}

			// Get the just added notification
			AssessmentUploadNotification currentNotification = assessmentNotificationDao.findByCreatedAt(createdAt);

			return currentNotification;

		} else if (audience.size() > 0) {

			// get information about the student the notification is targeted at
			List<Integer> idList = new ArrayList<>();

			audience.forEach(x -> idList.add(Integer.parseInt(x)));

			// get the students for whom the notification is sent

			List<Student> targetStudents = studentDao.findAllById(idList);
			final int total = targetStudents.size();

			int batchSize = 5;
			// Add notification to each of the students in batches
			for (int start = 0; start < total; start += batchSize) {

				try {

					int end = Math.min(start + batchSize, total);
					var studentList = targetStudents.subList(start, end);
					studentList.forEach(student -> student.AddNotification(newNotification));

					// Perform batch update
					studentDao.saveAllAndFlush(studentList);

				} catch (Exception e) {

					throw new IllegalArgumentException("Something went wrong!");
				}

			}

			// get the just added notification
			AssessmentUploadNotification currentNotification = assessmentNotificationDao.findByCreatedAt(createdAt);

			// sets the audience the notification targets at
			currentNotification.setReceipientIds(
					targetStudents.stream().map(student -> student.getId()).collect(Collectors.toList()));

			return currentNotification;
		} else {

//			the notification targets a particular institution

//			get the institution
			final Institution targetInstitution = institutionDao.findById(receipientInstitutitonId)
					.orElseThrow(() -> new IllegalArgumentException("Targetted institution not found"));

			List<Student> targetStudents = targetInstitution.getStudentList();

			if (targetStudents.size() > 0) {

				final int total = targetStudents.size();

				int batchSize = 5;

				for (int start = 0; start < total; start += batchSize) {

					try {

						int end = Math.min(start + batchSize, total);
						var studentList = targetStudents.subList(start, end);
						studentList.forEach(student -> student.AddNotification(newNotification));

						// Perform batch update
						studentDao.saveAllAndFlush(studentList);

					} catch (Exception e) {

						throw new IllegalArgumentException("Something went wrong!");
					}

				}

			}

			// get the just added notification
			AssessmentUploadNotification currentNotification = assessmentNotificationDao.findByCreatedAt(createdAt);

			// sets the audience the notification targets at
			currentNotification.setReceipientIds(
					targetStudents.stream().map(student -> student.getId()).collect(Collectors.toList()));

			return currentNotification;

		}

	}

	private AssessmentUploadNotification mapToNotification(NotificationRequestDTO dto) {

		return new AssessmentUploadNotification(dto.getType(), dto.getMetadata(), dto.getMessage());

	}

	@Override
	public List<AssessmentUploadNotification> unreadNotificationsFor(Integer studentId) {

		return assessmentNotificationDao.getUnreadNotificationsFor(studentId);
	}

	// delete all notifications that have been read by all
	@Override
	public void deleteReadNotifications() {

		List<AssessmentUploadNotification> notifications = assessmentNotificationDao.findAll();

		removeAllReadNotifications(notifications);

	}

	// Removes all read notifications
	@Transactional
	private <T extends Notification> void removeAllReadNotifications(List<T> notifications) {

		// List of notifications that have been read by every student, they should be
		// deleted from the database
		List<AssessmentUploadNotification> staleNotifications = new ArrayList<>();

		notifications.forEach(notification -> {

			// get count of students yet to read the notification
			int unreadStudentCount = studentDao.getUnreadNotificationCountForStudents(notification.getId());

			// delete notification from the database if every student has read the
			// notification
			if (unreadStudentCount == 0) {

				// mark for deletion
				staleNotifications.add((AssessmentUploadNotification) notification);

			}

		});

		if (staleNotifications.size() > 0) {

			// delete all stale notifications
			assessmentNotificationDao.deleteAll(staleNotifications);
		}

	}

}
package com.edugreat.akademiksresource.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.controller.NotificationManagementController;
import com.edugreat.akademiksresource.dao.AssessmentNotificationDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {

	@Autowired
	private AssessmentNotificationDao assessmentNotificationDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private NotificationManagementController notificationManager;

	@Transactional
	public void postAssessmentNotification(NotificationRequestDTO dto) {

		AssessmentUploadNotification newNotification = mapToNotification(dto);

//		check if notification has been associated with the metadata
		var tempNotification = assessmentNotificationDao.findByMetadata(dto.getMetadata());
		if (tempNotification != null)
			throw new IllegalArgumentException("Suspected duplicate notifications");

//		Persist to the database;
		assessmentNotificationDao.save(newNotification);

//		Get creation time for this notification
		final LocalDateTime createdAt = newNotification.getCreatedAt();

		final List<String> audience = dto.getAudience();

//		if audience is null, then the notification is targeted for all students
		if (audience == null || audience.size() == 0) {

//			Associate each notification to each student in the database
			List<Student> students = studentDao.findAll();
//			Updating student's records using batch updates

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

//			Get the just added notification
			AssessmentUploadNotification currentNotification = assessmentNotificationDao.findByCreatedAt(createdAt);

//			Send instant notification to all logged in students

			students.forEach(student -> notificationManager.sendNotification(currentNotification, student.getId()));

		} else {

//			get information about the student the notification is targeted at
			List<Integer> idList = new ArrayList<>();

			audience.forEach(x -> idList.add(Integer.parseInt(x)));

//			get the students for whom the notification is sent

			List<Student> targetedStudents = studentDao.findAllById(idList);
			final int total = targetedStudents.size();

			int batchSize = 5;
//			Add notification to each of the students in batches
			for (int start = 0; start < total; start += batchSize) {

				try {

					int end = Math.min(start + batchSize, total);
					var studentList = targetedStudents.subList(start, end);
					studentList.forEach(student -> student.AddNotification(newNotification));

//					Perform batch update
					studentDao.saveAllAndFlush(studentList);

				} catch (Exception e) {

					throw new IllegalArgumentException("Something went wrong!");
				}

			}

//			get the just added notification
			AssessmentUploadNotification currentNotification = assessmentNotificationDao.findByCreatedAt(createdAt);
//			Instantly notify the intended students who are currently logged in
			targetedStudents
					.forEach(student -> notificationManager.sendNotification(currentNotification, student.getId()));

		}

	}

	private AssessmentUploadNotification mapToNotification(NotificationRequestDTO dto) {

		return new AssessmentUploadNotification(dto.getType(), dto.getMetadata(), dto.getMessage());

	}

}

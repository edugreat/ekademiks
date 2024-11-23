package com.edugreat.akademiksresource.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

@Repository
public interface AssessmentNotificationDao extends JpaRepository<AssessmentUploadNotification, Integer> {

//	Searches notification by type property
	List<AssessmentUploadNotification> findByType(String type);

//	Select all notifications for the given student

	@Query("SELECT n FROM Student s JOIN s.assessmentNotifications n ON s.id =:studentId")
	public List<AssessmentUploadNotification> getUnreadNotificationsFor(Integer studentId);

// Get notification by their creation time information
	AssessmentUploadNotification findByCreatedAt(LocalDateTime createdAt);

//	check if a there is a notification for a given metadata
	AssessmentUploadNotification findByMetadata(Integer metadata);

}

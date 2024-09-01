package com.edugreat.akademiksresource.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.Notification;

public interface NotificationDao extends JpaRepository<Notification, Integer> {

//	Searches notification by type property
	List<Notification> findByType(String type);
	
//	Select all notifications that have not been read
//	for a given student
	@Query("SELECT n FROM Student s JOIN s.notifications n ON s.id =:studentId "
			+ "AND n.readAt IS NULL")
	public List<Notification> getUnreadNotificationsFor(Integer studentId);

// Get notification by their creation time information
	Notification findByCreatedAt(LocalDateTime createdAt);

}

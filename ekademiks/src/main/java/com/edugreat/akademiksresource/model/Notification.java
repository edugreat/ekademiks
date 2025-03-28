package com.edugreat.akademiksresource.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The parent class for all notification classes
@MappedSuperclass
@NoArgsConstructor
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;

	// AssessmentUploadNotification type used to distinguish between notification
	@Column(nullable = false)
	private String type;

	// property that gives access to the information we are notifying about
	// For instance, in the context of test upload, this metadata holds the id to
	// the particular test being
	// uploaded.Hence it is the access to retrieve that particular test that was uploaded.
    // if notification is about a request to join a group chat, metadata would represent an ID of the user requesting to join the group chat
	@Column(name = " metadata", nullable = false)
	private Integer metadata;

	// When notification was created
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// When the student has read the notification.
	// This is use to calculate when to clear read notification from the database
	@Column(nullable = true)
	private LocalDateTime readAt;

	// AssessmentUploadNotification message briefing about the notification
	@Column(nullable = false)
	private String message;

	public Notification(String type, Integer metadata, String message) {

		this.type = type;
		this.metadata = metadata;
		this.message = message;
		
		createdAt = LocalDateTime.now();
	}

	

	public Notification(String type) {
		this.type = type;

	}

	@Override
	public int hashCode() {

		return Objects.hash(this.createdAt);

	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Notification that = (Notification) o;

		return this.createdAt.equals(that.createdAt);

	}

}

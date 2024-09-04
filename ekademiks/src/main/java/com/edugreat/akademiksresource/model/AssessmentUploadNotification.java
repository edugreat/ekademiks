package com.edugreat.akademiksresource.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class AssessmentUploadNotification extends Notification {

	public AssessmentUploadNotification() {
		super();

	}

	public AssessmentUploadNotification(String type, Integer metadata, String message) {
		super(type, metadata, message);

	}

	public AssessmentUploadNotification(String type) {
		super(type);

	}

}

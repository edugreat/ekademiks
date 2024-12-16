package com.edugreat.akademiksresource.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table
public class AssessmentUploadNotification extends Notification {

	
//	the user this notification is targeted at
	@Transient
	private List<Integer> receipientIds;
	
	
	
	public AssessmentUploadNotification() {
		super();

	}

	public AssessmentUploadNotification(String type, Integer metadata, String message) {
		super(type, metadata, message);

	}

	public AssessmentUploadNotification(String type) {
		super(type);

	}


	public List<Integer> getReceipientIds() {
		return receipientIds;
	}

	public void setReceipientIds(List<Integer> receipientIds) {
		this.receipientIds = receipientIds;
	}

	

}

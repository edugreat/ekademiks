package com.edugreat.akademiksresource.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

//  The miscellaneous notifications object represents other notifications except the assessment notification.
//  This includes notifications about someone that sent request to join academic group chat etc.
// For this case, the instance property 'metadata' is not strictly important if the notification is about join request.
// The most important field to determine the type of this notification is the 'type' field as it should convey the right
// information about the notification (e.g group join request as the case may be)
@Entity
@Table
public class MiscellaneousNotifications extends Notification {

	 //notifier represents the person that sends the notification. For instance, if the notification is about request to join a group chat,
     // notifier then represents the name of the student wanting to join the group chat which can be got from the 'metadata' property of this class.
	@Transient
	private String notifier;

	
	public MiscellaneousNotifications() {
		super();
		
	}

	public MiscellaneousNotifications(String type, Integer metadata, String message) {
		super(type, metadata, message);
		
	}

	public MiscellaneousNotifications(String type) {
		super(type);
		
	}

	public String getNotifier() {
		return notifier;
	}

	public void setNotifier(String notifier) {
		this.notifier = notifier;
	}

	
	

}

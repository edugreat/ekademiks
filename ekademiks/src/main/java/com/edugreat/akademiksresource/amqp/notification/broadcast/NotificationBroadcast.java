package com.edugreat.akademiksresource.amqp.notification.broadcast;

import java.util.List;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

public interface NotificationBroadcast {
	
	
	void getPreviousNotifications(List<AssessmentUploadNotification> notifications);
	
	void sendInstantNotification(AssessmentUploadNotification notification);

}

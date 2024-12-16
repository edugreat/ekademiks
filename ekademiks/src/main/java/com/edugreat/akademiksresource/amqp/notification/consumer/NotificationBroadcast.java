package com.edugreat.akademiksresource.amqp.notification.consumer;

import java.util.List;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

public interface NotificationBroadcast {
	
	
	void previousNotification(List<AssessmentUploadNotification> notifications);
	
	void instantNotification(AssessmentUploadNotification notification);

}

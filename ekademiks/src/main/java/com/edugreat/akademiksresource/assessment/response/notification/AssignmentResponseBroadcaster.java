package com.edugreat.akademiksresource.assessment.response.notification;

import java.util.List;

// interface that provides contract for broadcasting previous and instant notifications for 
// assessments attempts student make
public interface AssignmentResponseBroadcaster {
	
	
	void broadcastPreviousNotifications(List<AssessmentResponseRecord> notifications);
	
	void broadcastInstantNotification(AssessmentResponseRecord notifcation);

}

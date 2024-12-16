package com.edugreat.akademiksresource.contract;

import java.util.List;

import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

//  interface that provides notification contracts
public interface NotificationInterface {
	
//	provides contract to post assessment notification assessments to student who are currently online
	AssessmentUploadNotification postAssessmentNotification(NotificationRequestDTO dto);
	
	List<AssessmentUploadNotification> unreadNotificationsFor(Integer studentId);
	
	
	void deleteReadNotifications();
	

}

package com.edugreat.akademiksresource.contract;

import com.edugreat.akademiksresource.dto.NotificationRequestDTO;

//  interface that provides notification contracts
public interface NotificationInterface {
	
//	provides contract to post assessment notification assessments to student who are currently online
	void postAssessmentNotification(NotificationRequestDTO dto);

}

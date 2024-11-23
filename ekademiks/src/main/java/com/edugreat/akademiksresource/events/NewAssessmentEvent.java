package com.edugreat.akademiksresource.events;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

//  event source that publishes new assessment notification events to subscribers.
// This is used to trigger notifications to students who are currently online the moment new assessments have been uploaded.
public class NewAssessmentEvent extends ApplicationEvent {
	
private static final long serialVersionUID = 7486663701355863655L;
	//	a map of the data to publish where the key is the database identification the notification is meant for
	private final Map<Integer, AssessmentUploadNotification> data;

	public NewAssessmentEvent(Object source, Map<Integer, AssessmentUploadNotification> data) {
		
		super(source);
		
		this.data = data;
		
		
	}

	public Map<Integer, AssessmentUploadNotification> getData() {
		return data;
	}

	
	
	

}

package com.edugreat.akademiksresource.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO that serves as notification object returned from the client.
// From this object, the server determines the type of notification and who to notify
public class NotificationRequestDTO {

//	Type of notification such assessment uploads etc
	private String type;

//	Brief description of what the notification is about
	private String message;

//	AssessmentUploadNotification metadata contains useful links or property through which to access to access the information being 
//	notified, such new assessment uploads etc. Here we're accessing notification details by id of the
//	Information we are being notified about
	private Integer metadata;

//	The audience target for the notification
	private List<String> audience = new ArrayList<>();

}

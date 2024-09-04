package com.edugreat.akademiksresource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Server's notification response to the client's notification request
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsResponseDTO {

//	AssessmentUploadNotification id representing the identity of the notification object in the database from which this object get instantiated
	private int id;

//	metadata represents the id of the of what is being notified about(eg new assessment upload if the notification is about assessment upload)
	private int metadata;
//	The notification title
	private String type;
//	Brief information about the notification
	private String message;
//	When this notification was made
	private String createdAt;

}

package com.edugreat.akademiksresource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Server's notification response to the client's notification request
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsResponseDTO {
	
//	Notification id representing the identity of the notification object in the database from which this object get instantiated
	private int id;
//	The notification title
	private String type;
//	Brief information about the notification
	private String message;
//	When this notification was made
	private String createdAt;

}

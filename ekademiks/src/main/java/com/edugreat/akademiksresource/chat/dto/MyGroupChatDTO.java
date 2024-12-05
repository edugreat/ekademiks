package com.edugreat.akademiksresource.chat.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyGroupChatDTO{
	
//	the number of chats yet to be read by the student. This can be zero or more chats
	private int unreadChats;
	
//	the group name the student belongs to
	private String groupName;
	
//	the group icon url
	private String groupIconUrl;
	
//	Group admin ID
	private Integer groupAdminId;
	
//	the group description
	private String groupDescription;
	
	private LocalDateTime createdAt;
	
//	shows if the group chat has had previous posts or not. This is used to control the display of spinner at the backend while waiting for network response.
	private boolean hasPreviousPosts;
	public MyGroupChatDTO(int unreadChats, Integer groupAdminId, String groupName, LocalDateTime createdAt, String groupIconUrl, String groupDescription, boolean posted) {
		
		
		this.unreadChats = unreadChats;
		this.groupAdminId = groupAdminId;
		this.groupName = groupName;
		this.createdAt = createdAt;
		this.groupIconUrl = groupIconUrl;
		this.groupDescription = groupDescription;
		this.hasPreviousPosts = posted;
		
	}
	
}
package com.edugreat.akademiksresource.chat.dto;

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
	public MyGroupChatDTO(int unreadChats, Integer groupAdminId, String groupName, String groupIconUrl, String groupDescription) {
		
		
		this.unreadChats = unreadChats;
		this.groupAdminId = groupAdminId;
		this.groupName = groupName;
		this.groupIconUrl = groupIconUrl;
		this.groupDescription = groupDescription;
	}
	
}
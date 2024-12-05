package com.edugreat.akademiksresource.chat.projection;

import java.time.LocalDateTime;

// projection that retrieves just some basic information (group name, group icon url, group description) of the group chat a student belongs to
public interface GroupChatInfo {


	Integer getId();
	
	Integer getGroupAdminId();
	
	String getGroupName();
	
	LocalDateTime getCreatedAt();
	
	String getGroupIconUrl();
	
	String getDescription();
}

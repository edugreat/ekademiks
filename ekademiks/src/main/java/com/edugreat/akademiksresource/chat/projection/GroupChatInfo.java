package com.edugreat.akademiksresource.chat.projection;

// projection that retrieves just some basic information (group name, group icon url, group description) of the group chat a student belongs to
public interface GroupChatInfo {


	Integer getId();
	
	Integer getgroupAdminId();
	
	String getGroupName();
	
	String getGroupIconUrl();
	
	String getDescription();
}

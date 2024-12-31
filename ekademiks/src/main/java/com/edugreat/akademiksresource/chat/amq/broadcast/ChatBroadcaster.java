package com.edugreat.akademiksresource.chat.amq.broadcast;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

public interface ChatBroadcaster {

	void previousChatMessages(List<ChatDTO> chats);

//provides end point for creating and sending chat messages
	void sendInstantChat(ChatDTO chat);

//provides functionality for users to request to join an existing group chat.
	void sendJoinRequestNotification(MiscellaneousNotifications requestNotification);

//provides functionality to notify members about a new member who has joined the group
	public void notifyOnNewMember(MiscellaneousNotifications newJoinNotification);
	
	
	void broadcastPreviousChatNotifications(Set<MiscellaneousNotifications> chatNotifications, Integer groupId);
	
	

}

package com.edugreat.akademiksresource.chat._interface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import com.edugreat.akademiksresource.chat.dto.MyGroupChatDTO;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

// interface that provides contract methods for the chat functionality
public interface ChatInterface {
	
	void createGroupChat(GroupChatDTO dto);


//  get a key-value pair of basic group chat information for  the given student referenced by studentId
  SortedMap<Integer, MyGroupChatDTO> myGroupChatInfo(Integer studentId);
	
//  provides contract method that checks if the student referenced by the studentId belongs in any group chat
  boolean isGroupMember(Integer studentId);
  

  
//  provides end point for creating and sending chat messages
  ChatDTO instantChat(ChatDTO chat);
  
//  provides functionality that returns all group chat that have been created in a map object whose key is the group id
  Map<Integer, GroupChatDTO> allGroupChats();
  
//  provides functionality that returns the ids of all the groups the student already belongs to
  List<Integer> myGroupIds(Integer studentId);
  
//  provides functionality for users to request to join an existing group chat.
  MiscellaneousNotifications newGroupChatJoinRequest(GroupJoinRequest request);
  
//  provides functionality for admin to approve request to join the group chat referenced by the groupId
  MiscellaneousNotifications approveJoinRequest(Integer groupId, Integer requesterId,Integer requestId);

  
//  provides functionality to delete all chat notifications referenced by their IDs
  void deleteChatNotifications(Integer studentId, List<Integer> notificationIds);
  
//  provide functionality to fetch group IDs the user has requested to join but have yet to be attended to
  Set<Integer>  getPendingGroupChatRequestsFor(Integer studentId);
  
//  declines the student's request to join the group chat
  void declineJoinRequest(Integer groupId, Integer studentId, Integer notificationId);


//  provides functionality for editing group chat's name. The key of data map is the student's ID(who wants to rename group chat name, and should be the group admin).
//  The value of the data map is the group chat ID 
boolean editGroupName(Map<Integer, Integer> data, String currentGroupName);

// the key data map is the ID of the student who wants to delete the group chat. It is used to verify that it's actually being deleted by
// the group admin
boolean deleteGroupChat(Map<Integer, Integer> data);

//  provides functionality that allows a group member to exit or leave the group. The map's key is the group ID while the value is the ID of the member intending to leave
void leaveGroup(Map<Integer, Integer> map);


// provides functionality to retrieve all group IDs and the joined date for the student referenced by studentId

 Map<Integer, LocalDateTime> groupAndJoinedAt(Integer studentId);
 
// provides functionality that checks if the currently logged in user has received chat messages from the given group chat (map's value).
 boolean hadPreviousPosts(Map<Integer, Integer> map);
 
// gets the logged in student's previous chats
  List<ChatDTO> getPreviousChat(Integer studentId, Integer groupId);
 
 
 Set<MiscellaneousNotifications> streamChatNotifications(Integer studentId);
}

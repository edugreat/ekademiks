package com.edugreat.akademiksresource.chat._interface;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import com.edugreat.akademiksresource.chat.dto.MyGroupChatDTO;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;

// interface that provides contract methods for the chat functionality
public interface ChatInterface {
	
	void createGroupChat(GroupChatDTO dto);


//  get a key-value pair of basic group chat information for  the given student referenced by studentId
  SortedMap<Integer, MyGroupChatDTO> myGroupChatInfo(Integer studentId);
	
//  provides contract method that checks if the student referenced by the studentId belongs in any group chat
  boolean isGroupMember(Integer studentId);
  

  
//  provides end point for creating and sending chat messages
  void sendChat(ChatDTO chat, Map<Integer, SseEmitter> emitters);
  
//  provides functionality that returns all group chat that have been created in a map object whose key is the group id
  Map<Integer, GroupChatDTO> allGroupChats();
  
//  provides functionality that returns the ids of all the groups the student already belongs to
  List<Integer> myGroupIds(Integer studentId);
  
//  provides functionality for users to request to join an existing group chat.
  void newGroupChatJoinRequest(GroupJoinRequest request, SseEmitter notificationEmitter );
  
//  provides functionality for admin to approve request to join the group chat referenced by the groupId
  void approveJoinRequest(Integer groupId, Integer requesterId,Integer requestId, Map< Integer, SseEmitter> emitters);

  
//  provides functionality to delete all chat notifications referenced by their IDs
  void deleteChatNotifications(Integer studentId, List<Integer> notificationIds);
  
//  provide functionality to fetch group IDs the user has requested to join but have yet to be attended to
  Set<Integer>  getPendingGroupChatRequestsFor(Integer studentId);
  
//  declines the student's request to join the group chat
  void declineJoinRequest(Integer groupId, Integer studentId, Integer notificationId);
}

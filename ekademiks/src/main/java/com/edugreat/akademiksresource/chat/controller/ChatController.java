package com.edugreat.akademiksresource.chat.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dao.ChatDao;
import com.edugreat.akademiksresource.chat.dao.GroupMembersDao;
import com.edugreat.akademiksresource.chat.dao.MiscellaneousNotificationsDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import com.edugreat.akademiksresource.chat.model.Chat;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import com.edugreat.akademiksresource.model.Student;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/chats")
public class ChatController {
	
	@Autowired
	private ChatInterface chatInterface;
	
	// List of connected clients awaiting notifications
		private Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
		
		@Autowired
		private ChatDao chatDao;
		
		
		@Autowired
		private StudentDao studentDao;
		
		@Autowired
		private GroupMembersDao groupMembersDao;
		
		@Autowired
		private MiscellaneousNotificationsDao miscellaneousNotificationsDao;
		
	
	
	@PostMapping("/group")
	public ResponseEntity<Object> createGroupChat(@RequestBody @Valid GroupChatDTO dto, @RequestParam("new")boolean newGroup) {
		
		if(newGroup) {
			
			try {
				
				
				chatInterface.createGroupChat(dto);
				
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
			
				return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
			}
		}
		
		
		return null;
		
	}
	
	@GetMapping("/unread")
	public ResponseEntity<Object> unreadChats(@RequestParam Integer studentId) {
		
		System.out.println("unread called");
		
		
		
		try {
			
			return new ResponseEntity<>(chatInterface.myGroupChatInfo(studentId), HttpStatus.OK);
		} catch (Exception e) {
			
			
			
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@GetMapping("/inGroup")
	public ResponseEntity<Object> isGroupMember(@RequestParam ("id")String studentId) {
		
		System.out.println("in group called");	
		try {
			return new ResponseEntity<Object>(chatInterface.isGroupMember(Integer.parseInt(studentId)), HttpStatus.OK);
		} catch (Exception e) {
			
			
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/new_chat")
	public ResponseEntity<Object> postChat(@RequestBody @Valid ChatDTO dto) {
		
		
	
		try {
			chatInterface.sendChat(dto, emitters);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
		
			return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	
//	get all the group chats 
	@GetMapping("/groups")
	public ResponseEntity<Object> allGroupChats() {
		
		try {
			return new ResponseEntity<>(chatInterface.allGroupChats(), HttpStatus.OK);
		} catch (Exception e) {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
//	end point that returns all the group chat id the user
	@GetMapping("/ids")
	public ResponseEntity<Object> getMyGroupIds(@RequestParam String studentId) {
		
		try {
			return new ResponseEntity<>(chatInterface.myGroupIds(Integer.parseInt(studentId)), HttpStatus.OK);
		} catch (Exception e) {
		
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	// send previous chat messages to group members each time they enter the chat
	// forum
	@GetMapping("/messages")
	public SseEmitter broadcastChatMessages(@RequestParam("group") String groupId,
			@RequestParam("student") String studentId) {
		

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// ensure the user is authenticated
		if (authentication != null) {

			final Integer receipientId = Integer.parseInt(studentId);
			final Integer _groupId = Integer.parseInt(groupId);

			SseEmitter emitter = establishConnection(receipientId);
			

			

			// begin to emit previous chat messages
			emitChats(_groupId, receipientId, emitter);
			
			streamJoinRequestNotifications(receipientId);

			return emitter;

		}

		return null;

	}

//	end point for sending request to join group chat
	@PostMapping("/join_req")
	public ResponseEntity<Object> sendGroupJoinRequest(@RequestBody GroupJoinRequest request) {
		
		
		
//		get the group admin's notification emitter, if they online
		
		SseEmitter notificationEmitter = emitters.get(request.getGroupAdminId());
		
		chatInterface.newGroupChatJoinRequest(request, notificationEmitter);
		
		return new ResponseEntity<>(HttpStatus.OK);
//		
		
		
		
	}
	
	
	@PostMapping("/approve")
	public ResponseEntity<Object> approveRequest(@RequestBody Map<Integer, Integer> request, @RequestParam("id") String requestId) {
		
		
		request.forEach((groupId, requesterId) -> {
			
			chatInterface.approveJoinRequest(groupId, requesterId, Integer.parseInt(requestId), emitters);
			
		});
		
		
		
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	
//	clears from the student records, all notifications that have been viewed
	@DeleteMapping("/delete")
	public ResponseEntity<Object> clearChatNotifications(@RequestParam("owner_id") String studentId, @RequestBody List<Integer> ids) {
		
		chatInterface.deleteChatNotifications(Integer.parseInt(studentId), ids);
		
		return new ResponseEntity<>(HttpStatus.OK);
		
		
		
		
	}
	
	
//	return all the groupChat the user has impending join requests
	@GetMapping("/pending")
	public ResponseEntity<Object> getPendingJoinRequests(@RequestParam String studentId) {
		
		
		
		return new ResponseEntity<Object>(chatInterface.getPendingGroupChatRequestsFor(Integer.parseInt(studentId)), HttpStatus.OK);
	}
	
	
	@GetMapping("/decline")
	public ResponseEntity<Object> declineJoinRequest(@RequestParam ("grp") String groupId, @RequestParam("stu")String studentId, @RequestParam("notice_id")String notificationId ) {
		
		
		chatInterface.declineJoinRequest(Integer.parseInt(groupId), Integer.parseInt(studentId), Integer.parseInt(notificationId));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	private void emitChats(Integer groupId, Integer receipientId, SseEmitter emitter) {
		
//		get the date the user joined the group chat, so as to not allow them view chats histories prior to when they joined
		LocalDateTime joinedAt = groupMembersDao.findJoinedDate(receipientId, groupId);

//		get chats histories from the time they joined the group chat
		List<Chat> chats = chatDao.findByGroupIdOrderBySentAt(groupId).stream().filter(chat -> chat.getSentAt().isAfter(joinedAt) ).collect(Collectors.toList());

		if (chats != null && chats.size() > 0) {
			List<ChatDTO> dtos = chats.stream().map(this::mapToChatDTO).collect(Collectors.toList());
			dtos.forEach(dto -> {
				
//				get the number of members who are currently online
				final int onlineMembers = emitters.size();
				
				dto.setOnlineMembers(onlineMembers);
				try {
					emitter.send(SseEmitter.event().data(dto).name("chats"));
				} catch (IOException e) {

					// unsubscribes the student on error
					emitters.remove(receipientId);
				}
			});
		}

	}

	
//	check if the group admin has some unattended requests to join the group chat
	private void streamJoinRequestNotifications(Integer loggedInStudentId) {
		
		

			
			
//			fetch miscellaneous notification for the logged in student for notification whose type is either 'join request' or 'new member'
         Set<MiscellaneousNotifications> joinRequests = studentDao.findById(loggedInStudentId).get()
        		                                        .getMiscellaneousNotices().stream().filter(n -> n.getType().equals("join type") || n.getType().equals("new member")).collect(Collectors.toSet());  
       
         
        for(MiscellaneousNotifications  requestNotification: joinRequests) {
        
//        	set the name of the user requesting to join the group chat
        	final String requester = studentDao.getFirstName(requestNotification.getMetadata());
        	
        	if(requester != null) {
        	
        		requestNotification.setNotifier(requester);
        		
//        		check if the group admin is still connected to receive notifications
        		
        		final SseEmitter emitter = emitters.get(loggedInStudentId);
        		
        		if(emitter != null) {
        			
        			try {
						emitter.send(SseEmitter.event().data(requestNotification).name("chats"));
					} catch (IOException e) {
						emitters.remove(loggedInStudentId);
						e.printStackTrace();
					}
        		}
        	}
        }
         
         
			
//        get all the miscellaneous notifications to check if any needs deletion from the database
        List<MiscellaneousNotifications> notifications = miscellaneousNotificationsDao.findAll();
        
        List<Integer> staleNotificationsId = new ArrayList<>();
        
        notifications.forEach(n -> {
        	
//        	check if there are students yet to read this notification
        	int unreadCount = studentDao.getUnreadNotificationCount(n.getId());
        	
        	if(unreadCount == 0) {
        		
        		staleNotificationsId.add(n.getId());
        		
        	}
        });
			
//		delete all stale notifications
        if(! staleNotificationsId.isEmpty()) {
        	miscellaneousNotificationsDao.deleteAllById(staleNotificationsId);
        }
		
		
	}
	// send new chat messages in real-time to group members each time members of the
	// group post new chats
	public void sendInstantChatMessage(ChatDTO newChat, Integer receipientId) {

		// checks if the student is still available to receive the chat message
		SseEmitter emitter = emitters.get(receipientId);
		
//		reconnect if connection was lost
		if(emitter == null) emitter = establishConnection(receipientId);

//		get the number of group members currently online
		newChat.setOnlineMembers(emitters.size());
		
			emitInstantChat(newChat, receipientId, emitter);
		

	}

	private void emitInstantChat(ChatDTO newChat, Integer receipientId, SseEmitter emitter) {
		try {

			emitter.send(SseEmitter.event().data(newChat).name("chats"));
		} catch (IOException e) {
			emitters.remove(receipientId);

		}

	}

	private ChatDTO mapToChatDTO(Chat chat) {

		Student sender = chat.getSender();

		ChatDTO dto = new ChatDTO(chat.getId(), chat.getGroupChat().getId(), chat.getSender().getId(),
				chat.getContent(), chat.getSentAt());

		
		dto.setSenderName(sender.getFirstName());

		return dto;
	}

	private SseEmitter establishConnection(Integer receipientId) {
		
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.put(receipientId, emitter);
		
		
		emitter.onTimeout(() -> {
			
			emitters.remove(receipientId);
		});
		
		emitter.onCompletion(() -> {
			
			 emitters.remove(receipientId);
		});;
		
		return emitter;
	}


}

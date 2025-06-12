package com.edugreat.akademiksresource.chat.messages;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.amq.broadcast.ChatBroadcaster;
import com.edugreat.akademiksresource.chat.amq.consumer.ChatConsumer;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chats")
@Slf4j
public class MessageController {

	@Autowired
	private ChatBroadcaster broadcaster;

	@Autowired
	private ChatInterface chatInterface;
	
	@Autowired
	private ChatConsumer chatConsumer;
	
	
	private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

	@GetMapping("/messages")
	public SseEmitter previousMessages(@RequestParam Integer studentId) {
		
		System.out.println("messages controller called");
			
		
		SseEmitter emitter = chatConsumer.establishConnection(studentId);
		
		
		
		if(emitter != null) {

			try {
				
				
//		send previous chat messages
		
		Map<Integer, List<ChatDTO>> previousChatsPerGroup = chatInterface.getPreviousChats(studentId);
		
		for(Map.Entry<Integer, List<ChatDTO>> chatEntry : previousChatsPerGroup.entrySet()) {
			
					
			
			broadcaster.previousChatMessages(chatEntry.getValue());
		}
	
//		send previous chat notifications
		
		Map<Integer, List<MiscellaneousNotifications>> notificationsPerGroup = chatInterface.streamChatNotifications(studentId);
		
		for(Map.Entry<Integer, List<MiscellaneousNotifications>> notificationSet : notificationsPerGroup.entrySet()) {
			
			broadcaster.broadcastPreviousChatNotifications(notificationSet.getValue());
			
		}
		
		
				
			} catch (Exception e) {
				
				System.out.println(e);
				
				return null;
			}
		
			
			
		}	
		

		
		return emitter;
		
	}

//	end point for sending request to join group chat
	@PostMapping("/join_req")
	public ResponseEntity<Object> joinRequestNotification(@RequestBody GroupJoinRequest request) {

		MiscellaneousNotifications notification = chatInterface.newGroupChatJoinRequest(request);

		if (notification != null) {

			broadcaster.sendJoinRequestNotification(notification);

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/approve")
	public ResponseEntity<Object> approveJoinRequest(@RequestBody Map<Integer, Integer> request,
			@RequestParam("id") String requestId) {

		final Integer groupId = request.keySet().stream().toList().get(0);

		final Integer studentId = request.get(groupId);

		MiscellaneousNotifications notification = chatInterface.approveJoinRequest(groupId, studentId,
				Integer.parseInt(requestId));

		if (notification != null) {

			broadcaster.notifyOnNewMember(notification);

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

	}

	@PostMapping("/new_chat")
	public ResponseEntity<Object> postChat(@RequestBody @Valid ChatDTO dto) {
		
		
		

		try {
			final ChatDTO instantChat = chatInterface.instantChat(dto);

			broadcaster.sendInstantChat(instantChat);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {

			System.out.println(e) ;

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	

	@PutMapping("/modify/msg")
	public ResponseEntity<Object> editChat(@RequestBody ChatDTO chatDTO) {
		
		
		try {
			
			final ChatDTO editedChat = chatInterface.updateChat(chatDTO);
			
			broadcaster.sendInstantChat(editedChat);
				
				return new ResponseEntity<>(HttpStatus.OK);
			
			
		} catch (Exception e) {
			
			LOGGER.info("ERROR :", e);
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		
		
	}
	
	
	
	@DeleteMapping("/del_msg")
	public ResponseEntity<Object> deleteChat(@RequestBody Map.Entry<Integer, Integer> map, @RequestParam("del_id") Integer deleterId){
		
		
		try {
			
			broadcaster.sendInstantChat(chatInterface.deleteChat(map, deleterId));
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			
            LOGGER.info("ERROR :", e);
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}

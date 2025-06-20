package com.edugreat.akademiksresource.chat.messages;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
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
import reactor.core.publisher.Flux;

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
	public Flux<ServerSentEvent<?>> previousMessages(@RequestParam Integer studentId) {
		  return chatConsumer.establishConnection(studentId)
			        .mergeWith(
			            Flux.defer(() -> {
			                Map<Integer, List<ChatDTO>> previousChatsPerGroup = chatInterface.getPreviousChats(studentId);
			                return Flux.fromIterable(previousChatsPerGroup.entrySet())
			                    .map(chatEntry -> ServerSentEvent.builder()
			                        .data(chatEntry.getValue())  // Send entire list for this group
			                        .event("chats")
			                        .id(String.valueOf(chatEntry.getKey()))  // Group ID as event ID
			                        .build());
			            })
			        )
			        .mergeWith(
			            Flux.defer(() -> {
			                Map<Integer, List<MiscellaneousNotifications>> notificationsPerGroup = 
			                    chatInterface.streamChatNotifications(studentId);
			                return Flux.fromIterable(notificationsPerGroup.entrySet())
			                    .map(notificationSet -> ServerSentEvent.builder()
			                        .data(notificationSet.getValue())  // Send entire notification list
			                        .event("notifications")
			                        .id(String.valueOf(notificationSet.getKey()))  // Group ID as event ID
			                        .build());
			            })
			        );
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

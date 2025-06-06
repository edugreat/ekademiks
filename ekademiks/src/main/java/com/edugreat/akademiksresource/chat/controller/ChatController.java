package com.edugreat.akademiksresource.chat.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import com.edugreat.akademiksresource.chat.livepresence.LivePresenceMonitorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/chats")
@Tag(name = "Student's Chat Management", description = "Manages collaborative group chats, sending and receiving instant and previous chat messages, (requires student ROLE)")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

	
	private final ChatInterface chatInterface;
	private final LivePresenceMonitorService livePresenceMonitorService;
	public ChatController(ChatInterface chatInterface, 
			LivePresenceMonitorService livePresenceMonitorService, ObjectMapper objectMapper) {
		
		this.chatInterface = chatInterface;
		this.livePresenceMonitorService = livePresenceMonitorService;
		
		
	}

	@PostMapping("/group")
	@Operation(summary = "Create group chat", description = "Creates new interactive group chat")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Group created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid data format")
	})
	public ResponseEntity<Object> createGroupChat(@RequestBody @Valid GroupChatDTO dto,
			@RequestParam("new") boolean newGroup) {

		if (newGroup) {

			try {

				chatInterface.createGroupChat(dto);

				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {

				return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
			}
		}

		return null;

	}

	@GetMapping("/group_info")
	@Operation(summary = "Group information", description = "Retrieve group chat information by student ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Information retrieved successfully"),
			@ApiResponse(responseCode = "400", description ="Invalid ID or student not found")
	})
	public ResponseEntity<Object> groupInfo(@RequestParam Integer studentId) {

		try {
			
			

			return new ResponseEntity<>(chatInterface.myGroupChatInfo(studentId), HttpStatus.OK);
		} catch (Exception e) {

			System.out.println(e);			
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/inGroup")
	@Operation(summary = "Is group member", description = "Checks by ID if the logged in user belongs in any group")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "Invalid ID or student not found")
	})
	public ResponseEntity<Object> isGroupMember(@RequestParam("id") String studentId) {
		
		

		try {
			return new ResponseEntity<Object>(chatInterface.isGroupMember(Integer.parseInt(studentId)), HttpStatus.OK);
		} catch (Exception e) {
			
			

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

//	get all the group chats 
	@GetMapping("/groups")
	@Operation(summary  = "All groups", description = "Get all group chats")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "Request not successful")
	})
	public ResponseEntity<Object> allGroupChats() {

		try {
			return new ResponseEntity<>(chatInterface.allGroupChats(), HttpStatus.OK);
		} catch (Exception e) {

			

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

//	end point that returns all the group chat id the user
	@GetMapping("/ids")
	@Operation(summary  = "Group IDs", description = "Get all group IDs a student belongs in")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "student not found")
	})
	public ResponseEntity<Object> getMyGroupIds(@RequestParam String studentId) {

		try {
			return new ResponseEntity<>(chatInterface.myGroupIds(Integer.parseInt(studentId)), HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

//	clears from the student records, all notifications that have been viewed
	@DeleteMapping("/delete")
	@Operation(summary  = "clear notifications", description = "Delete chat notifications by student ID and a list of notification IDs")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Notifications deleted successfully"),
			@ApiResponse(responseCode = "400", description = "student or notification not found")
	})
	public ResponseEntity<Object> clearChatNotifications(@RequestParam("owner_id") String studentId,
			@RequestBody List<Integer> ids) {

		chatInterface.deleteChatNotifications(Integer.parseInt(studentId), ids);

		return new ResponseEntity<>(HttpStatus.OK);

	}

//	return all the groupChat the user has pending join requests
	@GetMapping("/pending")
	@Operation(summary  = "Pending request", description = "Get all pending requests to join group chats. Uses group admin ID")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "Group admin  not found")
	})
	public ResponseEntity<Object> getPendingJoinRequests(@RequestParam String studentId) {

		return new ResponseEntity<Object>(chatInterface.getPendingGroupChatRequestsFor(Integer.parseInt(studentId)),
				HttpStatus.OK);
	}

	@GetMapping("/decline")
	@Operation(summary  = "Decline request", description = "Decline someone's request to join the group chat")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Decline request successful"),
			@ApiResponse(responseCode = "400", description = "Any of the provided parameters does not exist")
	})
	public ResponseEntity<Object> declineJoinRequest(@RequestParam("grp") String groupId,
			@RequestParam("stu") String studentId, @RequestParam("notice_id") String notificationId) {

		chatInterface.declineJoinRequest(Integer.parseInt(groupId), Integer.parseInt(studentId),
				Integer.parseInt(notificationId));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/editGroup")
	@Operation(summary  = "Edit group name", description = "Update details of a group chat")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Update successful"),
			@ApiResponse(responseCode = "400", description = "Group not found")
	})
	public ResponseEntity<Object> editGroupName(@RequestBody Map.Entry<Integer, Integer> data,
			@RequestParam("_new") String currentGroupName) {

		try {
			final boolean renamed = chatInterface.editGroupName(data, currentGroupName);

			if (renamed)
				new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(null);

	}

	@DeleteMapping("/deleteGroup")
	@Operation(summary  = "Delete group", description = "Group a group chat - only group admin can do this")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "Admin or group chat not found")
	})
	public ResponseEntity<Object> deleteGroupChat(@RequestBody Map.Entry<Integer, Integer> data) {

		try {
			final boolean deleted = chatInterface.deleteGroupChat(data);

			if (deleted) {

				return new ResponseEntity<>(HttpStatus.OK);
			}
		} catch (Exception e) {

			

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(null);

	}

	@DeleteMapping("/exit")
	@Operation(summary  = "Exit a group", description = "Leave a particular chat")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "Either user or group chat not found")
	})
	public ResponseEntity<Object> leaveGroup(@RequestBody Map.Entry<Integer, Integer> map) {

		try {
			chatInterface.leaveGroup(map);
		} catch (Exception e) {
			

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/grp_joined_at")
	@Operation(summary  = "group joined dates", description = "Get dates a user joined group chats")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "User or group chat not found")
	})
	public ResponseEntity<Object> groupAndJoinedAt(@RequestParam("id") Integer studentId) {

		try {
			return new ResponseEntity<Object>(chatInterface.groupAndJoinedAt(studentId), HttpStatus.OK);
		} catch (Exception e) {
			

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/recent/post")
	@Operation(summary  = "Recent post", description = "Check if there have been recent posts since the user joined")
	@ApiResponses(value  = {
			@ApiResponse(responseCode = "200", description = "Request successful"),
			@ApiResponse(responseCode = "400", description = "User and or group chat not found")
	})
	public ResponseEntity<Object> anyPostsSinceJoined(@RequestBody Map<Integer, Integer> map) {

		if (!map.isEmpty()) {

			try {
				return new ResponseEntity<Object>(chatInterface.hadPreviousPosts(map), HttpStatus.OK);
			} catch (Exception e) {

				

				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

		return null;
	}
	
	@GetMapping("/live-presence")
	public SseEmitter updateLivePresence (@RequestHeader("user-group-chat-ids") String csvIds, @RequestHeader("studentId")String id) {
		
		try {
			
			
			List<Integer> groupChatIds = Arrays.stream(csvIds.split(","))
					                     .map(String::trim) 
					                     .map(Integer::parseInt)
					                     .collect(Collectors.toList());
					                     
			
			
			return livePresenceMonitorService.updateLivePresence(groupChatIds, Integer.parseInt(id));
		} catch (Exception e) {
			
			System.out.println(e);
			
			return null;
		}
	}
	
	


}

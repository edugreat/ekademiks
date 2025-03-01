package com.edugreat.akademiksresource.chat.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/chats")
public class ChatController {

	@Autowired
	private ChatInterface chatInterface;

	@PostMapping("/group")
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
	public ResponseEntity<Object> groupInfo(@RequestParam Integer studentId) {

		try {

			return new ResponseEntity<>(chatInterface.myGroupChatInfo(studentId), HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/inGroup")
	public ResponseEntity<Object> isGroupMember(@RequestParam("id") String studentId) {
		
		System.out.println("is group member called");

		try {
			return new ResponseEntity<Object>(chatInterface.isGroupMember(Integer.parseInt(studentId)), HttpStatus.OK);
		} catch (Exception e) {
			
			System.out.println(e.getLocalizedMessage());

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

//	get all the group chats 
	@GetMapping("/groups")
	public ResponseEntity<Object> allGroupChats() {

		try {
			return new ResponseEntity<>(chatInterface.allGroupChats(), HttpStatus.OK);
		} catch (Exception e) {

			System.out.println(e);

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

//	clears from the student records, all notifications that have been viewed
	@DeleteMapping("/delete")
	public ResponseEntity<Object> clearChatNotifications(@RequestParam("owner_id") String studentId,
			@RequestBody List<Integer> ids) {

		chatInterface.deleteChatNotifications(Integer.parseInt(studentId), ids);

		return new ResponseEntity<>(HttpStatus.OK);

	}

//	return all the groupChat the user has pending join requests
	@GetMapping("/pending")
	public ResponseEntity<Object> getPendingJoinRequests(@RequestParam String studentId) {

		return new ResponseEntity<Object>(chatInterface.getPendingGroupChatRequestsFor(Integer.parseInt(studentId)),
				HttpStatus.OK);
	}

	@GetMapping("/decline")
	public ResponseEntity<Object> declineJoinRequest(@RequestParam("grp") String groupId,
			@RequestParam("stu") String studentId, @RequestParam("notice_id") String notificationId) {

		chatInterface.declineJoinRequest(Integer.parseInt(groupId), Integer.parseInt(studentId),
				Integer.parseInt(notificationId));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/editGroup")
	public ResponseEntity<Object> editGroupName(@RequestBody Map<Integer, Integer> data,
			@RequestParam("_new") String currentGroupName) {

		try {
			final boolean renamed = chatInterface.editGroupName(data, currentGroupName);

			if (renamed)
				new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e);

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(null);

	}

	@DeleteMapping("/deleteGroup")
	public ResponseEntity<Object> deleteGroupChat(@RequestBody Map<Integer, Integer> data) {

		try {
			final boolean deleted = chatInterface.deleteGroupChat(data);

			if (deleted) {

				return new ResponseEntity<>(HttpStatus.OK);
			}
		} catch (Exception e) {

			System.out.println(e);

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(null);

	}

	@DeleteMapping("/exit")
	public ResponseEntity<Object> leaveGroup(@RequestBody Map<Integer, Integer> map) {

		try {
			chatInterface.leaveGroup(map);
		} catch (Exception e) {
			System.out.println(e);

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/grp_joined_at")
	public ResponseEntity<Object> groupAndJoinedAt(@RequestParam("id") Integer studentId) {

		try {
			return new ResponseEntity<Object>(chatInterface.groupAndJoinedAt(studentId), HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/recent/post")
	public ResponseEntity<Object> anyPostsSinceJoined(@RequestBody Map<Integer, Integer> map) {

		if (!map.isEmpty()) {

			try {
				return new ResponseEntity<Object>(chatInterface.hadPreviousPosts(map), HttpStatus.OK);
			} catch (Exception e) {

				System.out.println(e);

				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

		return null;
	}


}

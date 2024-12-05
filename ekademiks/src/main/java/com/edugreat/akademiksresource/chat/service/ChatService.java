/**
 * 
 */
package com.edugreat.akademiksresource.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dao.ChatDao;
import com.edugreat.akademiksresource.chat.dao.GroupChatDao;
import com.edugreat.akademiksresource.chat.dao.GroupMembersDao;
import com.edugreat.akademiksresource.chat.dao.MiscellaneousNotificationsDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.chat.dto.GroupChatDTO;
import com.edugreat.akademiksresource.chat.dto.MyGroupChatDTO;
import com.edugreat.akademiksresource.chat.model.Chat;
import com.edugreat.akademiksresource.chat.model.GroupChat;
import com.edugreat.akademiksresource.chat.model.GroupMember;
import com.edugreat.akademiksresource.chat.projection.GroupChatInfo;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import com.edugreat.akademiksresource.model.Student;

import jakarta.transaction.Transactional;

/**
 * Class that provides implementation for ChatInterface
 */

@Service
public class ChatService implements ChatInterface {

	@Autowired
	private GroupChatDao groupChatDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private GroupMembersDao groupMembersDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private MiscellaneousNotificationsDao miscellaneousNoticeDao;

	@Transactional
	@Override
	public void createGroupChat(GroupChatDTO dto) {

		// check and retrieve the student trying to create the new group chat
		Student admin = studentDao.findById(dto.getGroupAdminId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid admin details"));

		GroupChat newGroupChat = groupChatDao.save(mapToGroupChat(dto));

		// add a new member for the group chat, the admin who just created the group
		// chat.
		GroupMember groupMember = new GroupMember(newGroupChat, admin);

		groupMembersDao.saveAndFlush(groupMember);

	}

	private GroupChat mapToGroupChat(GroupChatDTO dto) {

		return modelMapper.map(dto, GroupChat.class);
	}

	@Override
	public SortedMap<Integer, MyGroupChatDTO> myGroupChatInfo(Integer studentId) {
		SortedMap<Integer, MyGroupChatDTO> myGroupChatDTO = new TreeMap<>();

		// Fetch group names and icons for the student
		List<GroupChatInfo> groupChatInfos = groupChatDao.getGroupInfo(studentId);

	
		
		// Fetch unread chats for the student
		SortedMap<Integer, Integer> unreadChats = studentDao.unreadChats(studentId);
		
		
		
		
		
		// Check if unreadMessages is null or empty
		if (unreadChats == null || unreadChats.isEmpty()) {
			
			// There are no unread chats; populate unreadChatsDTO with zero unread chats for
			// each group
			for (GroupChatInfo group : groupChatInfos) {
				
				
				myGroupChatDTO.put(group.getId(), new MyGroupChatDTO(0, group.getGroupAdminId(), group.getGroupName(), group.getCreatedAt(),
						group.getGroupIconUrl(), group.getDescription(), chatDao.hasPreviousPosts(group.getId())));
				
				
				
				
				
			}
		} else {
			
			
			// Handle the case where unreadChats is not null and has entries
			for (GroupChatInfo group : groupChatInfos) {
				
				
				// get the number of unread chats
				Integer unreadCount = unreadChats.get(group.getId());
				myGroupChatDTO.put(group.getId(),
						new MyGroupChatDTO(unreadCount != null ? unreadCount : 0, group.getGroupAdminId(),
								group.getGroupName(),group.getCreatedAt(), group.getGroupIconUrl(), group.getDescription(), chatDao.hasPreviousPosts(group.getId())));
			}
			
			
		}
		
		
		return myGroupChatDTO;
	}

	@Override
	public boolean isGroupMember(Integer studentId) {

				
		return groupMembersDao.isGroupMember(studentId);
	}

	@Override
	@Transactional
	public void sendChat(ChatDTO dto, Map<Integer, SseEmitter> emitters) {

		// confirm the user is authenticated
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {

			// confirm that the group chat for which the chat is intended actually exists
			if (groupChatDao.findById(dto.getGroupId()).isPresent()) {

				// get the group for which the chat is intended
				GroupChat groupChat = groupChatDao.findById(dto.getGroupId()).get();

				// map to Chat object
				Chat currentChat = mapToChat(dto);

				// associate group chat to chat for bidirectional relationship
				groupChat.AddMessage(currentChat);

				// persist the chat to the database and return it
				Chat currentlySavedChat = chatDao.save(currentChat);

				// map the currently saved chat object to chat dto
				ChatDTO chatDTO = mapToChatDTO(currentlySavedChat);
				chatDTO.setSenderName(currentChat.getSender().getFirstName());

				// get the number of members who are currently online
				chatDTO.setOnlineMembers(emitters.size());

				// get all the group member's id so they can receive new chat update
				List<Integer> groupMemberId = groupMembersDao.getMemberIds(chatDTO.getGroupId());

				groupMemberId.forEach(memberId -> {

					// send to members who are currently online.
					// online members have their notification emitter saved in the emitters map
					final SseEmitter chatEmitter = emitters.get(memberId);

					if (chatEmitter != null) {

						try {
							chatEmitter.send(SseEmitter.event().data(chatDTO).name("chats"));
						} catch (IOException e) {
							
							System.out.println("error sending message: "+chatDTO);
							emitters.remove(memberId);
							
							try {
								chatEmitter.complete();
							} catch (Exception e2) {
								System.out.println("error completing emitter: "+e2.getMessage());
							}
							e.printStackTrace();
						}

					}
				});

			}

		}

	}

	private Chat mapToChat(ChatDTO chatDTO) {

		final GroupChat groupChat = groupChatDao.findById(chatDTO.getGroupId()).get();

		final Student sender = studentDao.findById(chatDTO.getSenderId()).get();

		Chat chat = new Chat(groupChat, sender, chatDTO.getContent());

		return chat;
	}

	private ChatDTO mapToChatDTO(Chat chat) {

		ChatDTO dto = new ChatDTO(chat.getId(), chat.getGroupChat().getId(), chat.getSender().getId(),
				chat.getContent(), chat.getSentAt());

		return dto;
	}

	@Override
	public Map<Integer, GroupChatDTO> allGroupChats() {

		List<GroupChat> allGroupChats = groupChatDao.findAll();

		Map<Integer, GroupChatDTO> groupChatDTOs = new HashMap<>();

		if (!allGroupChats.isEmpty()) {
			// sort group chats by their creation date
			allGroupChats.sort(Comparator.comparing(GroupChat::getCreatedAt));

			allGroupChats.forEach(g -> {

				groupChatDTOs.put(g.getId(), mapToGroupChatDTO(g));
			});
		}

		return groupChatDTOs;
	}

	private GroupChatDTO mapToGroupChatDTO(GroupChat groupChat) {

		return new GroupChatDTO(groupChat.getDescription(), groupChat.getGroupIconUrl(), groupChat.getGroupName(),
				groupChat.getGroupAdminId(), groupChat.getId(), groupChat.getCreatedAt());
	}

	@Override
	public List<Integer> myGroupIds(Integer studentId) {

		return groupMembersDao.groupIdsFor(studentId);
	}

	@Override
	@Transactional
	public void newGroupChatJoinRequest(GroupJoinRequest request, SseEmitter notificationEmitter) {

		// check if the group the user wants to join actually exists.
		final boolean exists = groupChatDao.existsById(request.getGroupId());
		
        // ensure the user requesting to join the group chat actually exists
		Student requester = studentDao.findById(request.getRequesterId()).orElseThrow(() -> new IllegalArgumentException("User does not exist"));

		if (exists) {

			final String groupToJoin = groupChatDao.findById(request.getGroupId()).get().getGroupName();
			final StringBuilder notificationMsg = new StringBuilder();
			notificationMsg.append("Request to join ").append(groupToJoin).append(" group");

			MiscellaneousNotifications joinRequestNotice = new MiscellaneousNotifications("join group",
					request.getRequesterId(), notificationMsg.toString());

			// fetch the admin of the group
			Student groupAdmin = studentDao.findById(request.getGroupAdminId())
					.orElseThrow(() -> new IllegalArgumentException("Something went wrong!"));

			// save the notification
			joinRequestNotice = miscellaneousNoticeDao.save(joinRequestNotice);

			// add new notification for the student(group admin)
			groupAdmin.addMiscellaneousNotices(joinRequestNotice);

			studentDao.saveAndFlush(groupAdmin);
			
//			add a pending request to the student to indicate the have pending request to join a group chat
			requester.getPendingGroupChatRequests().add(request.getGroupId());
			
			studentDao.saveAndFlush(requester);

			// send instant join request notification to the group admin if they're online
			if (notificationEmitter != null) {

				// set the requester's name
				joinRequestNotice.setNotifier(request.getRequester());

				try {
					notificationEmitter.send(SseEmitter.event().data(joinRequestNotice).name("chats"));
				} catch (IOException e) {

					e.printStackTrace();
				}
			}

		}

	}

	@Transactional
	@Override
	public void approveJoinRequest(Integer groupId, Integer requesterId,Integer requestId, Map<Integer, SseEmitter> emitters) {

		// check if the user is already a member of the group they want to join
		final boolean isMember = groupMembersDao.isGroupMember(groupId, requesterId);

		if (!isMember) {

			// get the group chat they want to join
			final GroupChat groupChat = groupChatDao.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("Sorry the group chat does not exist"));

			// get the student intending to join the group chat
			final Student student = studentDao.findById(requesterId)
					.orElseThrow(() -> new IllegalArgumentException("Sorry account does not exist"));

//			create a new group member
			GroupMember newMember = new GroupMember(groupChat, student);

			final var newlyAddedMember = groupMembersDao.save(newMember);

			// create a new notification to let group members know a new member has been
			// added
			MiscellaneousNotifications notification = new MiscellaneousNotifications("new member", requesterId,
					student.getFirstName() + " has joined the group");

			// save the notification
			final MiscellaneousNotifications _notification = miscellaneousNoticeDao.save(notification);

			// remove this group id from the collection of group chats the user has
			//pending join requests
			student.getPendingGroupChatRequests().remove(groupChat.getId());

			
			
//			get the group's admin
			Student groupAdmin = studentDao.findById(groupChat.getGroupAdminId()).orElseThrow(() -> new IllegalArgumentException("Admin's records not found"));
			
//			get the request information from the database
			 MiscellaneousNotifications staleNotification = miscellaneousNoticeDao.findById(requestId).orElseThrow(() -> new IllegalArgumentException("error processing request"));
			
//			 remove the request from the admin's collection of join group requests yet to attend to
			 groupAdmin.getMiscellaneousNotices().remove(staleNotification);
			 
			 studentDao.saveAllAndFlush(List.of(groupAdmin, student));
			 
			// get all group members except the  currently added member
			List<Student> members = groupMembersDao.findAll().stream().map(x -> x.getMember()).filter(m -> m.getId() != newlyAddedMember.getId())
					.collect(Collectors.toList());
			
			// save notification to each of the group members 	
			members.forEach(m -> {

				m.addMiscellaneousNotices(_notification);
				studentDao.saveAndFlush(m);

				// notify members who are currently online about a new member that just joined the group chat
				if (emitters.containsKey(m.getId())) {

					final SseEmitter notificationEmitter = emitters.get(m.getId());
					try {
						notificationEmitter.send(SseEmitter.event().data(_notification).name("chats"));
					} catch (IOException e) {
						emitters.remove(m.getId());
					}
				}
			});

		}

	}

	@Override
	@Transactional
	public void deleteChatNotifications(Integer studentId, List<Integer> notificationIds) {

		// check if the student exists
		if (studentDao.existsById(studentId)) {

			Student student = studentDao.findById(studentId).get();

			List<MiscellaneousNotifications> staleNotifications = new ArrayList<>();

			// get all the chat notifications for the student and extract into an array list,
			//  the ones to delete.
			student.getMiscellaneousNotices().forEach(n -> {

				if (notificationIds.contains(n.getId())) {
					

					staleNotifications.add(n);
				}
			});

		
			if (!staleNotifications.isEmpty()) {

			
				// removes all stale notifications at once
				student.getMiscellaneousNotices().removeAll(staleNotifications);

				studentDao.saveAndFlush(student);


			}
		}

	}

	@Override
	@Transactional
	public void declineJoinRequest(Integer groupId, Integer studentId, Integer notificationId) {

		// confirm the records exits
		final boolean studentExists = studentDao.existsById(studentId);

		if (studentExists) {

			// confirm the group chat exists
			final boolean groupExists = groupChatDao.existsById(groupId);

			if (groupExists) {

				// remove this request from the student's collection of requests awaiting
				// approval
				Student student = studentDao.findById(studentId).get();
				student.getPendingGroupChatRequests().remove(groupId);

				studentDao.saveAndFlush(student);

				// get the group admin's ID
				final Integer adminId = groupChatDao.getAdminId(groupId);

				// get the admin
				Student groupAdmin = studentDao.findById(adminId).get();

				//

				// deletes the admin's notification about this join request
				groupAdmin.getMiscellaneousNotices().removeIf(notice -> notice.getId() == notificationId);

				studentDao.saveAndFlush(groupAdmin);

				// delete the notification from the database
				miscellaneousNoticeDao.deleteById(notificationId);

			}
		}

	}

	@Override
	public Set<Integer> getPendingGroupChatRequestsFor(Integer studentId) {

		// confirm that the student exists
		if (studentDao.existsById(studentId)) {

			// get the all the group they have pending requests
			Set<Integer> pendingGroupRequest = studentDao.pendingGroupRequestsFor(studentId);

			return pendingGroupRequest;
		}

		return null;
	}

	@Transactional
	@Override
	public boolean editGroupName(Map<Integer, Integer> data, String currentGroupName) {
	
		final Integer studentId = data.keySet().stream().collect(Collectors.toList()).get(0);
		
		final Integer groupId = data.get(studentId);
		
//		confirm the group chat actually exists
		final boolean groupExists = groupChatDao.existsById(groupId);
		
		
//		confirm the member trying to rename the group is the group admin
		if(groupExists && groupChatDao.getAdminId(groupId) == studentId ) {
			
//			get the group chat
			GroupChat groupChat = groupChatDao.findById(groupId).orElseThrow(() -> new IllegalArgumentException("could not rename group"));
			
			groupChat.setGroupName(currentGroupName);
			
			groupChatDao.saveAndFlush(groupChat);
			
			return true;
			
			
			
			
		}
		
		return false;
	}

	@Transactional
	@Override
	public boolean deleteGroupChat(Map<Integer, Integer> data) {
		
		System.out.println("data: "+data.toString());
		
		
		Integer studentId = data.keySet().stream().collect(Collectors.toList()).get(0);
		
//		confirm group admin is the on trying to delete the group chat
		if(studentId != null &&  studentId.equals(groupChatDao.getAdminId(data.get(studentId)))) {
			
			
			
			groupChatDao.deleteById(data.get(studentId));
			
		}else return false;
		

		
		
		
		return true;
	}

	@Transactional
	@Override
	public void leaveGroup(Map<Integer, Integer> map) {
		
		System.out.println("inside service");
		
		System.out.println(map.toString());
		
		Integer groupId = map.keySet().stream().collect(Collectors.toList()).get(0);
		
//		get the group chat from which they intend to leave
		GroupChat groupChat = groupChatDao.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group chat could not be fetched"));
		
		Integer memberId = map.get(groupId);
		
//		get the group member object
		final GroupMember groupMember = groupMembersDao.findByGroupChatAndMember(groupId, memberId);
		
		if(groupMember != null) {
			
			System.out.println("not null: "+groupMember.toString());
			
//			disconnect the groupMember object from the group chat
			groupChat.getGroupMembers().remove(groupMember);
			
//			delete the group member entity
			groupMembersDao.delete(groupMember);
		}else throw new IllegalArgumentException("could not process request");
		
	}

	@Override
	public Map<Integer, LocalDateTime> groupAndJoinedAt(Integer studentId) {
		
		
		
		return groupMembersDao.findGroupAndJoinedAt(studentId);
	}
	
	
	@Override
	public boolean hadPreviousPosts(Map<Integer, Integer> map) {
		
		final Integer studentId = map.keySet().stream().collect(Collectors.toList()).get(0);
//		get the time the user joined the group chat
		final LocalDateTime joinedAt = groupMembersDao.findJoinedDate(studentId, map.get(studentId));

		return groupMembersDao.anyPostsSinceJoined(studentId, map.get(studentId), joinedAt);
		
	}

}

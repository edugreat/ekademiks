/**
 * 
 */
package com.edugreat.akademiksresource.chat.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.GroupJoinRequest;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import com.edugreat.akademiksresource.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


/**
 * Class that provides implementation for ChatInterface
 */

@Service
@Slf4j
public class ChatService implements ChatInterface {

	
	private final GroupChatDao groupChatDao;

	
	private final StudentDao studentDao;

	
	private final ModelMapper modelMapper;

	
	private final GroupMembersDao groupMembersDao;

	
	private final ChatDao chatDao;
	
	private final AppAuthInterface appAuthInterface;

	
	private final MiscellaneousNotificationsDao miscellaneousNoticeDao;

	private final CacheManager cacheManager;
	
	@Autowired
	private  RedisTemplate<String,Object> redisTemplate;
	
	private final ObjectMapper objectMapper;
	
	
	
	
	public ChatService(GroupChatDao groupChatDao, StudentDao studentDao, ModelMapper modelMapper,
			GroupMembersDao groupMembersDao, ChatDao chatDao, MiscellaneousNotificationsDao miscellaneousNoticeDao,
			CacheManager cacheManager,ObjectMapper objectMapper, AppAuthInterface appAuthInterface) {
		this.groupChatDao = groupChatDao;
		this.studentDao = studentDao;
		this.modelMapper = modelMapper;
		this.groupMembersDao = groupMembersDao;
		this.chatDao = chatDao;
		this.miscellaneousNoticeDao = miscellaneousNoticeDao;
		this.cacheManager = cacheManager;
		this.objectMapper = objectMapper;	
		this.appAuthInterface = appAuthInterface;
				
	}

	//	configured content meant for deleted chat
	@Value("${chat.deleted.content}")
	private String DELETED_CHAT_CONTENT;
	
	
	

	@Transactional
	@Override
	@Caching(evict = {
			
			@CacheEvict(cacheNames = RedisValues.MY_GROUP, key = "#dto.groupAdminId"),
			@CacheEvict(cacheNames = RedisValues.MY_GROUP_IDs, key =  "#dto.groupAdminId")
	})
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

	@Transactional(readOnly = true)
	@Override
	public SortedMap<Integer, MyGroupChatDTO> myGroupChatInfo(Integer studentId) {
		
		
		Cache cache = cacheManager.getCache(RedisValues.MY_GROUP);
		
		if(cache !=  null && cache.get(studentId) != null) {
			
			Map<Integer, MyGroupChatDTO> cachedMap = objectMapper.convertValue(cache.get(studentId).get(),
					
					new TypeReference<Map<Integer, MyGroupChatDTO>>() {});
			
							
				return new TreeMap<>(cachedMap);
			
		}
		
	
		
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

				myGroupChatDTO.put(group.getId(),
						new MyGroupChatDTO(0, group.getGroupAdminId(), group.getGroupName(), group.getCreatedAt(),
								group.getGroupIconUrl(), group.getDescription(),
								chatDao.hasPreviousPosts(group.getId())));

			}
		} else {

			// Handle the case where unreadChats is not null and has entries
			for (GroupChatInfo group : groupChatInfos) {

				// get the number of unread chats
				Integer unreadCount = unreadChats.get(group.getId());
				myGroupChatDTO.put(group.getId(),
						new MyGroupChatDTO(unreadCount != null ? unreadCount : 0, group.getGroupAdminId(),
								group.getGroupName(), group.getCreatedAt(), group.getGroupIconUrl(),
								group.getDescription(), chatDao.hasPreviousPosts(group.getId())));
			}

		}
		
		cacheManager.getCache(RedisValues.MY_GROUP).put(studentId, myGroupChatDTO);
		
		List<Integer> myGroupIds = new ArrayList<>();
		
		myGroupChatDTO.keySet().forEach(groupId -> myGroupIds.add(groupId));
		
		redisTemplate.opsForValue().set(String.valueOf(studentId), myGroupIds);
		

		return myGroupChatDTO;
	}


	@Override
	@Transactional
	public ChatDTO instantChat(ChatDTO dto) {

		
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
			
//			if this was a replied message, set the message that got the reply
			if (dto.getRepliedToChat() != null) {

				chatDTO.setRepliedTo(currentlySavedChat.getRepliedTo());
				chatDTO.setRepliedToChat(dto.getRepliedToChat());
			}
			
//			key = user's ID, value = boolean flag that shows if they belong to the given group
			final Map.Entry<Integer, Boolean> userEntry =  currentUserEntry(chatDTO.getGroupId());
			
//			ensure the user was logged before processing this code
	
			if(userEntry.getKey() != null && userEntry.getValue()) {
				
				final Integer userId = userEntry.getKey();
				
				
//				get and update previous chats
				Map<Integer, List<ChatDTO>> previousChatsPerGroup = getPreviousChats(userId);
				
			
//				update stored chats
				if(previousChatsPerGroup.get(dto.getGroupId()) != null) {
					
					
					previousChatsPerGroup.get(dto.getGroupId()).add(chatDTO);
				}else {
					
					
					
					List<ChatDTO> l = new ArrayList<>();
					l.add(chatDTO);
					previousChatsPerGroup.put(dto.getGroupId(),l );
					
				}
				
				cacheManager.getCache(RedisValues.PREVIOUS_CHATS).put(userId, previousChatsPerGroup);
				
				chatDTO.setChatReceipient(userId);
			
				return chatDTO;
			
			}
			
			
			
			
		}
		
		

		return null;
	}

	private Chat mapToChat(ChatDTO chatDTO) {

		final GroupChat groupChat = groupChatDao.findById(chatDTO.getGroupId()).get();

		final Student sender = studentDao.findById(chatDTO.getSenderId()).get();

		Chat chat = new Chat(groupChat, sender, chatDTO.getContent());

		if (chatDTO.getRepliedTo() != null) {

			chat.setRepliedTo(chatDTO.getRepliedTo());
			chat.setRepliedToChat(chatDTO.getRepliedToChat());

		}

		return chat;
	}

	private ChatDTO mapToChatDTO(Chat chat) {

		ChatDTO dto = new ChatDTO(chat.getId(), chat.getGroupChat().getId(), chat.getSender().getId(),
				chat.getContent(), chat.getSentAt());

		return dto;
	}
	
//	method that checks if the current user is a logged user that belongs to the given group ID
	private Map.Entry<Integer, Boolean> currentUserEntry(Integer groupId) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String username = authentication.getName();
		
		
		
		

//		ensure the user was logged before processing this code
		if(!username.toLowerCase().contains("anonymous")) {
			
			
//			get ID of the user
			Integer userId = studentDao.getIdByUsername(username);
			
			if(userId != null) {
				
				
//				check if the user is a group member
			final boolean isMember =	groupMembersDao.isGroupMember(userId);
			
			
			
			if(isMember) {
				
				
				
				return new AbstractMap.SimpleEntry<>(userId, isMember);
				
			}
				
				
				
			}
			
			}
		
		return new AbstractMap.SimpleEntry<>(null, false);
	}

	private ChatDTO _mapToChatDTO(Chat chat, Integer studentId) {

		Student sender = chat.getSender();

		ChatDTO dto = new ChatDTO(chat.getId(), chat.getGroupChat().getId(), chat.getSender().getId(),
				chat.getContent(), chat.getSentAt());

		
		
		dto.setEditedChat(chat.getIsEditedChat());

		dto.setSenderName(sender.getFirstName());
		dto.setChatReceipient(studentId);

		if (chat.getRepliedTo() != null) {

//			set the chat ID that got the reply
			dto.setRepliedTo(chat.getRepliedTo());

//			set the chat that was replied to if it hasn't been deleted
			dto.setRepliedToChat(chat.getRepliedToChat());

		}
		
		if(chat.getDeletedBy() != null) {
			
			dto.setDeleter(studentDao.getFirstName(chat.getDeletedBy()));
			dto.setDeleterId(chat.getDeletedBy());		
			
		}

		

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = RedisValues.ALL_GROUPS)
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
	@Transactional(readOnly = true)
	@Cacheable(value = RedisValues.MY_GROUP_IDs, key = "#studentId")
	public List<Integer> myGroupIds(Integer studentId) {
		
	
		return groupMembersDao.groupIdsFor(studentId);
	}

	@Override
	@Transactional
	@Caching(evict = {
			
			@CacheEvict(cacheNames = RedisValues.MISCELLANEOUS, allEntries = true),
			@CacheEvict(cacheNames = RedisValues.PENDING_REQUEST, allEntries = true)
	})
	public MiscellaneousNotifications newGroupChatJoinRequest(GroupJoinRequest request) {

		// check if the group the user wants to join actually exists.
		final boolean exists = groupChatDao.existsById(request.getGroupId());

		// ensure the user requesting to join the group chat actually exists
		Student requester = studentDao.findById(request.getRequesterId())
				.orElseThrow(() -> new IllegalArgumentException("User does not exist"));

		if (exists) {

			final String groupToJoin = groupChatDao.findById(request.getGroupId()).get().getGroupName();
			final StringBuilder notificationMsg = new StringBuilder();
			notificationMsg.append("Request to join ").append(groupToJoin).append(" group");

			MiscellaneousNotifications joinRequestNotification = new MiscellaneousNotifications("join group",
					request.getRequesterId(), notificationMsg.toString());

			// fetch the admin of the group
			Student groupAdmin = studentDao.findById(request.getGroupAdminId())
					.orElseThrow(() -> new IllegalArgumentException("Something went wrong!"));

			joinRequestNotification.setTargetGroupChat(request.getGroupId());
			
			// save the notification
			joinRequestNotification = miscellaneousNoticeDao.save(joinRequestNotification);

//			set the group chat this request notification targets at
			joinRequestNotification.setTargetGroupChat(request.getGroupId());

			// add new notification for the student(group admin)
			groupAdmin.addMiscellaneousNotices(joinRequestNotification);

			studentDao.saveAndFlush(groupAdmin);

//			add a pending request to the student to indicate the have pending request to join a group chat
			requester.getPendingGroupChatRequests().add(request.getGroupId());

			studentDao.saveAndFlush(requester);

			return joinRequestNotification;
		}

		return null;
	}

	@Transactional
	@Override
    @Caching(evict = {
			@CacheEvict(cacheNames = RedisValues.MISCELLANEOUS, allEntries = true),
			@CacheEvict(cacheNames = RedisValues.PENDING_REQUEST, allEntries = true)
	})
	public MiscellaneousNotifications approveJoinRequest(Integer groupId, Integer requesterId, Integer requestId) {

		// check if the user is already a member of the group they wanted to join
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

			notification.setTargetGroupChat(groupId);		// save the notification
			MiscellaneousNotifications _notification = miscellaneousNoticeDao.save(notification);



			// remove this group id from the collection of group chats the user has
			// pending join requests
			student.getPendingGroupChatRequests().remove(groupChat.getId());

//			get the group's admin
			Student groupAdmin = studentDao.findById(groupChat.getGroupAdminId())
					.orElseThrow(() -> new IllegalArgumentException("Admin's records not found"));

//			get the request information from the database
			MiscellaneousNotifications staleNotification = miscellaneousNoticeDao.findById(requestId)
					.orElseThrow(() -> new IllegalArgumentException("error processing request"));

//			 remove the request from the admin's collection of join group requests yet to attend to
			groupAdmin.getMiscellaneousNotices().remove(staleNotification);

			studentDao.saveAllAndFlush(List.of(groupAdmin, student));

			// save notification to each of the group members except the newly added member
			groupMembersDao.getMemberIds(groupId).stream().filter(id -> id != newlyAddedMember.getId()).forEach(id -> {

				studentDao.findById(id).get().addMiscellaneousNotices(_notification);
			});

			return _notification;
		}

		return null;
	}

	@Override
	@Transactional
   @Caching(evict = {
			
			@CacheEvict(cacheNames = RedisValues.MISCELLANEOUS, allEntries = true),
			@CacheEvict(cacheNames = RedisValues.PENDING_REQUEST, allEntries = true)
	})
	public void deleteChatNotifications(Integer studentId, List<Integer> notificationIds) {

		// check if the student exists
		if (studentDao.existsById(studentId)) {

			Student student = studentDao.findById(studentId).get();

			List<MiscellaneousNotifications> staleNotifications = new ArrayList<>();

			// get all the chat notifications for the student and extract into an array
			// list,
			// the ones to delete.
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
		}else throw new IllegalArgumentException("Unauthorized attempt");

	}

	@Override
	@Transactional
	@Caching(evict = {
			
			@CacheEvict(cacheNames = RedisValues.MISCELLANEOUS, allEntries = true),
			@CacheEvict(cacheNames = RedisValues.PENDING_REQUEST, allEntries = true)
	})
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

			}else throw new IllegalArgumentException("Group not found");
		}else throw new IllegalArgumentException("Unauthorized attempt");

	}

	@Override
	@Cacheable(cacheNames = RedisValues.PENDING_REQUEST, unless = "#result ==  null")
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
	@Caching(evict = {
			@CacheEvict(cacheNames = RedisValues.MY_GROUP, key = "#data.key"),
			@CacheEvict(cacheNames = RedisValues.MY_GROUP_IDs, key = "#data.key")
	})
	public boolean editGroupName(Map.Entry<Integer, Integer> data, String currentGroupName) {

		final Integer studentId = data.getKey();

		final Integer groupId = data.getValue();

//		confirm the group chat actually exists
		final boolean groupExists = groupChatDao.existsById(groupId);

//		confirm the member trying to rename the group is the group admin
		if (groupExists && groupChatDao.getAdminId(groupId) == studentId) {

//			get the group chat
			GroupChat groupChat = groupChatDao.findById(groupId)
					.orElseThrow(() -> new IllegalArgumentException("could not rename group"));

			groupChat.setGroupName(currentGroupName);

			groupChatDao.saveAndFlush(groupChat);

			return true;

		}

		throw new IllegalArgumentException("Operation not allowed");
	}

	@Transactional
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = RedisValues.MY_GROUP, key = "#data.key"),
			@CacheEvict(cacheNames = RedisValues.MY_GROUP_IDs, key = "#data.key"),
			@CacheEvict(cacheNames = RedisValues.PREVIOUS_CHATS, key = "#data.value")
	})
	public boolean deleteGroupChat(Map.Entry<Integer, Integer> data) {

		Integer studentId = data.getKey();

//		confirm group admin is the one trying to delete the group chat
		if (studentId != null && studentId.equals(groupChatDao.getAdminId(data.getValue()))) {

			groupChatDao.deleteById(data.getValue());

		} else throw new IllegalArgumentException("Operation not allowed");

		return true;
	}

	@Transactional
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = RedisValues.MY_GROUP, key = "#data.key"),
			@CacheEvict(cacheNames = RedisValues.MY_GROUP_IDs, key = "#data.key"),
			@CacheEvict(cacheNames = RedisValues.PREVIOUS_CHATS, key = "#data.value")
	})
	public void leaveGroup(Map.Entry<Integer, Integer> map, String cachingKey) {

		Integer groupId = map.getKey();

//		get the group chat from which they intend to leave
		GroupChat groupChat = groupChatDao.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Group chat could not be fetched"));

		Integer memberId = map.getValue();

//		get the group member object
		final GroupMember groupMember = groupMembersDao.findByGroupChatAndMember(groupId, memberId);

		if (groupMember != null) {

//			disconnect the groupMember object from the group chat
			groupChat.getGroupMembers().remove(groupMember);

//			delete the group member entity
			groupMembersDao.delete(groupMember);
			
			
			var cachedUser = getCachedUser(cachingKey);
			
			if(cachedUser instanceof StudentDTO) {
				
				System.out.println("user is an instance of student dto");
				
				cachedUser.setIsGroupMember(groupMembersDao.isGroupMember(memberId));
				
				appAuthInterface.resetCachedUser(cachedUser, cachingKey);
			}else {
				
				System.out.println("cached user not an instance of student dto");
			}
			
		} else
			throw new IllegalArgumentException("could not process request");

	}

	@Override
	@Cacheable(value = RedisValues.JOIN_DATE, key = "#studentId")
	public Map<Integer, String> groupAndJoinedAt(Integer studentId) {
		
		final var joinedAt = groupMembersDao.findGroupAndJoinedAt(studentId);
		
	
		return joinedAt;
	}
		
		

	@Override
	public boolean hadPreviousPosts(Map<Integer, Integer> map) {

		final Integer studentId = map.keySet().stream().collect(Collectors.toList()).get(0);
//		get the time the user joined the group chat
		final LocalDateTime joinedAt = groupMembersDao.findJoinedDate(studentId, map.get(studentId));

		return groupMembersDao.anyPostsSinceJoined(studentId, map.get(studentId), joinedAt);

	}

	@Override
	@Transactional(readOnly = true)
	public Map<Integer, List<ChatDTO>> getPreviousChats(Integer studentId) {
		
		
		
//		check if the information is already cached
		Object prevChatsObj = redisTemplate.opsForValue().get(RedisValues.PREVIOUS_CHATS+"::"+studentId);
		
		
		if(prevChatsObj != null) {
			
			
			
			return objectMapper.convertValue(prevChatsObj,
					
					new TypeReference<Map<Integer, List<ChatDTO>>>(){}
					);
		}
		
		
		Object obj = redisTemplate.opsForValue().get(RedisValues.JOIN_DATE+"::"+studentId);
		
		Map<Integer, String> joinedDates;
		
		if(obj != null) {
						 joinedDates =  objectMapper.convertValue(obj, new TypeReference<Map<Integer, String>>() {});
			
			
		}else {
						joinedDates = groupMembersDao.findGroupAndJoinedAt(studentId);
				
		
		}
		
		
		return processPreviousChats(joinedDates, studentId);

		
	}

	private Map<Integer, List<ChatDTO>> processPreviousChats(Map<Integer, String> joinedDatesMap, Integer studentId) {
	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		
		Map<Integer, List<ChatDTO>> messagesPerGroup = new HashMap<>();
		
//		fetch previous chat messages per group, filtering those messages sent after the given date the user joined the group chat
		for(Map.Entry<Integer, String> map : joinedDatesMap.entrySet()) {
			
			LocalDate date = LocalDate.parse(map.getValue(), formatter);
			
//			start the date at midnight
			LocalDateTime parsedDate = date.atStartOfDay();
			List<ChatDTO> chats = chatDao.findByGroupChatIdOrderBySentAt(map.getKey())
					           .stream().filter(chat -> chat.getSentAt().isAfter(parsedDate))
					           .map(c -> _mapToChatDTO(c, studentId))
					           .collect(Collectors.toList());
			
			messagesPerGroup.put(map.getKey(), chats);
			
		}
		
		
		redisTemplate.opsForValue().set(RedisValues.PREVIOUS_CHATS+"::"+studentId, messagesPerGroup);
		
		
		
		return messagesPerGroup;
		
		
		
		
		
	}

	@Override
	@Transactional(readOnly =  true)
	@Cacheable(cacheNames = RedisValues.MISCELLANEOUS, key = "#studentId")
	public Map<Integer, List<MiscellaneousNotifications>> streamChatNotifications(Integer studentId) {

//		fetch miscellaneous notification for the logged in student for notification whose type is either 'join request' or 'new member'
		List<MiscellaneousNotifications> requestNotifications = studentDao.findById(studentId).get()
				.getMiscellaneousNotices().stream()
				.filter(n -> n.getType().equals("join group") || n.getType().equals("new member"))
				.collect(Collectors.toList());
				
		
//		get a list of group chat IDs contained in each of the notifications
		List<Integer> groupList = requestNotifications.stream()
				                     .map(r -> r.getTargetGroupChat())
				                     .collect(Collectors.toList());

		requestNotifications.stream().forEach(request -> {

			final String requester = studentDao.getFirstName(request.getMetadata());

			if (requester != null) {

				request.setNotifier(requester);
				request.setReceipientId(studentId);
			}
		});

//	    get all the miscellaneous notifications to check if any needs deletion from the database
		List<MiscellaneousNotifications> notifications = miscellaneousNoticeDao.findAll();

		List<Integer> staleNotificationsId = new ArrayList<>();

		notifications.forEach(n -> {

//	    	check if there are students yet to read this notification
			int unreadCount = studentDao.getUnreadNotificationCount(n.getId());

			if (unreadCount == 0) {

				staleNotificationsId.add(n.getId());

			}
		});

//		delete all stale notifications
		if (!staleNotificationsId.isEmpty()) {
			miscellaneousNoticeDao.deleteAllById(staleNotificationsId);
		}
	
		Map<Integer, List<MiscellaneousNotifications>> notificationsPerGroup = new HashMap<>();
		
//		associate notifications to their respective group
		for(Integer id : groupList) {
			
			var groupNotifications = requestNotifications.stream()
					                 .filter(n -> n.getTargetGroupChat() == id)
					                 .collect(Collectors.toList());
			
			notificationsPerGroup.put(id, groupNotifications);
			
		}
		
		return notificationsPerGroup;
	}

	@Override
	@Transactional
	public ChatDTO updateChat(ChatDTO chatDTO) {

//		check if the chat exists
		if (chatDao.existsById(chatDTO.getId())) {

//				get the groupChat object where this chat belongs
			Optional<GroupChat> optional = groupChatDao.findById(chatDTO.getGroupId());

			if (optional.isPresent()) {

				

//				get the chat object
				Chat updatableChat = chatDao.findById(chatDTO.getId()).get();

//				update the chat with the current chat content (chat message)
				updatableChat.setContent(chatDTO.getContent());
				updatableChat.setIsEditedChat(true);

				Chat updatedChat = chatDao.saveAndFlush(updatableChat);				
				
				ChatDTO dto = mapToChatDTO(updatedChat);

				dto.setSenderName(chatDTO.getSenderName());

				dto.setEditedChat(true);
				
				final Map.Entry<Integer, Boolean> userEntry = currentUserEntry(dto.getGroupId());
				
				
				if(userEntry.getKey() != null && userEntry.getValue()) {
					
					final Integer userId = userEntry.getKey();
					
					
					Map<Integer, List<ChatDTO>> previousChatsPerGroup = getPreviousChats(userId);
					
//					replace all matching chats in the list with the updated chat
					previousChatsPerGroup.get(chatDTO.getGroupId())
					.replaceAll(c -> c.getId().equals(chatDTO.getId()) ? dto : c );
					
//					update the cache manager
					cacheManager.getCache(RedisValues.PREVIOUS_CHATS).put(userId, previousChatsPerGroup);
		
					dto.setChatReceipient(userId);
					
					return dto;
				}

				
			}

		}

		throw new IllegalArgumentException("error processing request");
	}

	@Override
	@Transactional
	public ChatDTO deleteChat(Map.Entry<Integer, Integer> map, Integer deleterId) {

		final Integer groupId = map.getKey();

		final Integer chatId = map.getValue();

//		get the GroupChat the chat belongs in
		GroupChat grpChat = groupChatDao.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Couldn't delete chat from non-existent group"));

//		get the chat to delete  
		final Chat toBeDeleted = chatDao.findById(chatId)
				.orElseThrow(() -> new IllegalArgumentException("Couldn't delete non-existent chat: " + chatId));

		grpChat.getChats().remove(toBeDeleted);

		groupChatDao.save(grpChat);

//		delete the chat from the database
		chatDao.delete(toBeDeleted);

//		get all the chats that have replied the deleted chat and update the 'deletedBy' property
		List<Chat> repliedChats = repliedChats(chatId, groupId);

		if(repliedChats.size() > 0) {
			
			repliedChats.forEach(reply -> {

				reply.setDeletedBy(deleterId);
			});

			chatDao.saveAllAndFlush(repliedChats);
		}

		ChatDTO deletedChat = mapToChatDTO(toBeDeleted);
		deletedChat.setDeleterId(deleterId);
		String deleterFirstName = studentDao.getFirstName(deleterId);
		deletedChat.setDeleter(deleterFirstName);	

		deletedChat.setContent(DELETED_CHAT_CONTENT);
		
		
		final Map.Entry<Integer, Boolean> loggeInUserEntry = currentUserEntry(groupId);
		
		if(loggeInUserEntry.getKey() != null && loggeInUserEntry.getValue()) {
			
			final Integer userId = loggeInUserEntry.getKey();
			
			Map<Integer, List<ChatDTO>> previousChatsPerGroup = getPreviousChats(userId);
			
			
//			replace replied chats with the updated values of replied chats
			repliedChats.forEach(replied -> {
				
				previousChatsPerGroup.get(groupId).replaceAll(p -> p.getId().equals(replied.getId()) ?
						
						processRepliedChatsAfterDeletion(p, deleterFirstName, deleterId) :
							
							p
							);
			});
			
			
//			operation below searches the cached version of the deleted chat, replacing it with a new object(deletedChat)
//			that has the information of the user that deleted it
			int indexOfDeletedChat  = -1;
		
			List<ChatDTO> chatsPerGroup = previousChatsPerGroup.get(groupId);			
			
			for(int i = 0; i < chatsPerGroup.size(); i++) {
				
				if(chatsPerGroup.get(i).getId().equals(deletedChat.getId())) {
					
					indexOfDeletedChat = i;
					
					break;
				}
			}
			
			if(indexOfDeletedChat != -1) {
				
				chatsPerGroup.set(indexOfDeletedChat, deletedChat);	
				
//				makes a replacement
				previousChatsPerGroup.put(groupId, chatsPerGroup);
				
			
			}
	
//			update cached data
			cacheManager.getCache(RedisValues.PREVIOUS_CHATS).put(groupId, previousChatsPerGroup);
		}
		
	

		return deletedChat;
	
	}
	
//	update information of the user that deleted the chat
	private ChatDTO processRepliedChatsAfterDeletion(ChatDTO replier, String deleter, Integer deleterId) {
		
		replier.setDeleter(deleter);
		replier.setDeleterId(deleterId);		
		
		return replier;
		
	}

// get a list of chats that have replied a given chat(referenced by chatId)
	private List<Chat> repliedChats(Integer chatId, Integer groupId) {

		return chatDao.findRepliedChats(chatId, groupId);

	}

	@Transactional
	@Override
	public boolean lockGroup(Map.Entry<Integer, Integer> mapObj) {

		final Integer groupId = mapObj.getKey();

		final Integer groupAdmin = mapObj.getValue();

//		get the group to be locked
		GroupChat grpChat = groupChatDao.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("group chat does not exist"));

//		confirm the action is to be perform by the group admin
		final Integer actualGroupAdmin = groupChatDao.getAdminId(groupId);

		if (!actualGroupAdmin.equals(groupAdmin))
			return false;

//		lock the group if the user meets the criteria to lock the chat
		grpChat.setIsGroupLocked(true);

//	persists operation to the database
		groupChatDao.saveAndFlush(grpChat);

		return true;
	}
	public <T extends AppUserDTO> T getCachedUser(String cachingKey) {
		
		
		T cachedUser = appAuthInterface.getCachedUser(cachingKey);
		
		if(cachedUser!= null) {
			
			return cachedUser;
		}
		throw new IllegalArgumentException("cached user is null");
		
	}
}

package com.edugreat.akademiksresource.chat.amq.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat.dao.GroupChatDao;
import com.edugreat.akademiksresource.chat.dao.GroupMembersDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

@Service
public class ChatConsumerService implements ChatConsumer {

	@Autowired
	private GroupMembersDao groupMembersDao;

	@Autowired
	private GroupChatDao groupChatDao;

	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatConsumerService.class);

	private static final long HEARTBEAT_INTERVAL = 30000; // 30 seconds

	@RabbitListener(queues = { "${instant.chat.queue}" })
	synchronized void consumeInstantChatMessage(ChatDTO chat) {
//
		LOGGER.info(String.format("received JSON  message -> %s", chat));

//		get IDs of group members
		List<Integer> memberIds = groupMembersDao.getMemberIds(chat.getGroupId());

		final Integer groupId = chat.getGroupId();

		final long currentlyOnline = currentlyOnlineMembers(groupId, memberIds);

//		collect the IDs to be removed once there is exception
		List<String> toBeRemoved = new ArrayList<>();

//		send instant message to members currently online
		emitters.forEach((key, emitter) -> {

			try {

				for (Integer studentId : memberIds) {

					if (key.equals(groupId + "_" + studentId)) {
						

						chat.setOnlineMembers(currentlyOnline);
						emitter.send(SseEmitter.event().data(chat).name("chats"));

					}
				}

			} catch (IOException e) {

//				mark the ID as fit for removal
				toBeRemoved.add(key);

				LOGGER.error("error sending message to ", key, e.getMessage());
			}

		});

//		remove all the IDs marked for removal
		toBeRemoved.forEach(emitters::remove);

	}

//	receives and publishes notifications to the group admin about a user intending to join the group chat
	@RabbitListener(queues = { "${chat.notifications.queue}" })
	void sendJoinRequestNotification(MiscellaneousNotifications requestNotification) {

// 	   get the groupAdmin ID
		final Integer groupAdminId = groupChatDao.getAdminId(requestNotification.getTargetGroup());
		
		final Integer targetGroupId = requestNotification.getTargetGroup();
//		check if the group admin is currently connected to receive notification
		if (groupAdminId != null && emitters.containsKey(targetGroupId+"_"+groupAdminId)) {
			
			final String key = targetGroupId+"_"+groupAdminId;

			try {
				emitters.get(key).send(SseEmitter.event().data(requestNotification).name("chats"));
			} catch (IOException e) {

				emitters.remove(key);

				LOGGER.info(String.format("Error notifying group admin %s", groupAdminId+" -> group: "+targetGroupId));
			}

		}

	}

	@RabbitListener(queues = { "${previous.chat.queue}" })
	void publichPreviousChatsMessages(ChatDTO chat) {

		Integer studentId = chat.getChatReceipient();

		final Integer groupId = chat.getGroupId();

//		get IDs of group members
		List<Integer> memberIds = groupMembersDao.getMemberIds(groupId);

		final long currentlyOnline = currentlyOnlineMembers(groupId, memberIds);

		final String key = groupId + "_" + studentId;

//		check if the user is online
		if (emitters.containsKey(key)) {
			
			
			chat.setOnlineMembers(currentlyOnline);
			try {
				emitters.get(key).send(SseEmitter.event().data(chat).name("chats"));
			} catch (IOException e) {

				emitters.remove(key);
				LOGGER.info(String.format("unable to send previous chat to %s", studentId + " -> group: " + groupId));
			}
		}else System.out.println("does not contain "+key);
	}

//	notifies group members about a new member that just joined the group
	@RabbitListener(queues = { "${chat.notifications.queue}" })
	void notifyForNewMember(MiscellaneousNotifications notification) {

//		get the group members id
		List<String> groupMembersId = groupMembersDao.getMemberIds(notification.getTargetGroup())
				                      .stream().map(x -> x.toString()).collect(Collectors.toList());

		List<String> toBeRemoved = new ArrayList<>();

		emitters.keySet().stream().
		filter(key -> groupMembersId.contains(key.substring(0, key.indexOf("_"))))
		.forEach(_key -> {

			try {
				emitters.get(_key).send(SseEmitter.event().data(notification).name("chats"));
			} catch (IOException e) {

				LOGGER.info("error notifying member: ", _key, e.getMessage());
				toBeRemoved.add(_key);

				System.out.println("Error notifying logged in group members: " + e.getMessage());
			}

		});

		toBeRemoved.forEach(emitters::remove);

	}

	@RabbitListener(queues = { "${chat.notifications.queue}" })
	void sendPreviousChatNotifications(MiscellaneousNotifications previousNotification) {

//    	get the student id the notifications targets at
		final Integer studentId = previousNotification.getReceipientId();

		if (studentId != null && emitters.containsKey(previousNotification.getTargetGroup()+"_"+studentId)) {

			
			final String key = previousNotification.getTargetGroup()+"_"+studentId;
			
			final SseEmitter emitter = emitters.get(key);

			try {
				emitter.send(SseEmitter.event().data(previousNotification).name("chats"));
			} catch (IOException e) {

				emitters.remove(key);

				LOGGER.info(String.format("Error emitting previous notifications to", studentId+" -> groupId: "+previousNotification.getTargetGroup()));

				return; // stop further notifications

			}

		}

	}

	@Override
	public SseEmitter establishConnection(Integer studentId, Integer groupId) {
		
	

		SseEmitter emitter = new SseEmitter(1000L * 20 * 60);
		
		if(emitters.get(groupId + "_" + studentId) != null) {
			System.out.println("reconnecting");
			var oldEmitter = emitters.get(groupId + "_" + studentId);
			
			emitters.replace(groupId + "_" + studentId, emitter);
			oldEmitter.complete();
			
			
			
		}else {
			emitters.put(groupId + "_" + studentId, emitter);
		}

		

		emitter.onCompletion(() -> {

			emitters.remove(groupId + "_" + studentId);
		});

		emitter.onTimeout(() -> emitter.complete());

		emitter.onError(e -> {

			emitters.remove(groupId + "_" + studentId);

			LOGGER.info(String.format("just removed %s", groupId));
		});

		// startHeartbeat(emitter, studentId);

		return emitter;

	}

	private long currentlyOnlineMembers(Integer groupId, List<Integer> memberIds) {

		long count = 0l;

		for (Integer id : memberIds) {

			if (emitters.containsKey(groupId + "_" + id))
				count++;
		}

		return count;
	}

//	private void startHeartbeat(SseEmitter emitter, Integer studentId) {
//		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//		executorService.scheduleAtFixedRate(() -> {
//			try {
//				emitter.send(SseEmitter.event().data("heartbeat").name("heartbeat"));
//			} catch (IOException e) {
//				LOGGER.error("Error sending heartbeat: " + e.getMessage());
//				emitters.remove(studentId); // Remove the emitter if it fails
//				executorService.shutdown(); // Stop the heartbeat task if emitter is no longer valid
//			}
//		}, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
//	}

	@Override
	public void disconnectGroup(Map<Integer, Integer> data) {
		
		List<String> tobeRemoved = new ArrayList<>();
		
		emitters.forEach((key, val) -> {
			
			for (Integer groupId: data.keySet()) {
				
				String s = groupId+"_"+data.get(groupId);
				if(s.equals(key)) tobeRemoved.add(s);
				
			}
			
			
		});


//		Removes and completes each of the SSEs. 
		tobeRemoved.forEach(key -> {
			
			final SseEmitter staleEmitter = emitters.remove(key);
			
			staleEmitter.complete();
		});
		
		

	}

}

package com.edugreat.akademiksresource.chat.amq.consumer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
public class ChatConsumer implements ChatConsumerInterface {

	@Autowired
	private GroupMembersDao groupMembersDao;

	@Autowired
	private GroupChatDao groupChatDao;

	private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatConsumer.class);

	@RabbitListener(queues = {"${instant.chat.queue}"})
	void consumeInstantChatMessage(ChatDTO chat) {

		LOGGER.info(String.format("received JSON  message -> %s", chat));

//		get IDs of group members
		List<Integer> memberIds = groupMembersDao.getMemberIds(chat.getGroupId());

		final long currentlyOnline = currentlyOnlineMembers(memberIds);

//		send instant message to members currently online
		emitters.forEach((studentId, emitter) -> {

			try {
				if (memberIds.contains(studentId)) {

					chat.setOnlineMembers(currentlyOnline);
					emitter.send(SseEmitter.event().data(chat).name("chats"));
				}
			} catch (IOException e) {

//				removes this emitter in case of error
				emitters.remove(studentId);

				System.out.println("error removing emitter: " + e);
			}

		});

	}

//	receives and publishes notifications to the group admin about a user intending to join the group chat
	@RabbitListener(queues = {"${chat.notifications.queue}"})
	void sendJoinRequestNotification(MiscellaneousNotifications requestNotification) {

// 	   get the groupAdmin ID
		final Integer groupAdminId = groupChatDao.getAdminId(requestNotification.getTargetGroup());

//		check if the group admin is currently connected to receive notification
		if (groupAdminId != null && emitters.containsKey(groupAdminId)) {

			try {
				emitters.get(groupAdminId).send(SseEmitter.event().data(requestNotification).name("chats"));
			} catch (IOException e) {

				emitters.remove(groupAdminId);

				System.out.println("Error notifying group admin " + e);
			}

		}

	}

	@RabbitListener(queues = {"${previous.chat.queue}"})
	void publichPreviousChatsMessages(ChatDTO chat) {

		Integer studentId = chat.getChatReceipient();

//		get IDs of group members
		List<Integer> memberIds = groupMembersDao.getMemberIds(chat.getGroupId());

		final long currentlyOnline = currentlyOnlineMembers(memberIds);

//		check if the user is online
		if (emitters.containsKey(studentId)) {

			chat.setOnlineMembers(currentlyOnline);
			try {
				emitters.get(studentId).send(SseEmitter.event().data(chat).name("chats"));
			} catch (IOException e) {

				emitters.remove(studentId);
				System.out.println("unable to send previous chat: " + e);
			}
		}

	}

//	notifies group members about a new member that just joined the group
	@RabbitListener(queues = {"${chat.notifications.queue}"})
	void notifyForNewMember(MiscellaneousNotifications notification) {

//		get the group members id
		List<Integer> groupMembersId = groupMembersDao.getMemberIds(notification.getTargetGroup());

		emitters.keySet().stream().filter(userId -> groupMembersId.contains(userId)).forEach(loggedInUser -> {

			try {
				emitters.get(loggedInUser).send(SseEmitter.event().data(notification).name("chats"));
			} catch (IOException e) {

				emitters.remove(loggedInUser);

				System.out.println("Error notifying logged in group members: " + e.getMessage());
			}

		});

	}


	@RabbitListener(queues = {"${chat.notifications.queue}"})
	void sendPreviousChatNotifications(Set<MiscellaneousNotifications> previousNotifications) {

//    	get the student id the notifications targets at
		final Integer receipientId = previousNotifications.stream().toList().get(0).getReceipientId();

		if (receipientId != null && emitters.containsKey(receipientId)) {

			final SseEmitter emitter = emitters.get(receipientId);

			for (MiscellaneousNotifications notification : previousNotifications) {

				try {
					emitter.send(SseEmitter.event().data(notification).name("chats"));
				} catch (IOException e) {

					emitters.remove(receipientId);

					System.out.println("Error emitting previous notifications: " + e.getMessage());

					return; // stop further notification

				}
			}
		}

	}
	

	@Override
	public SseEmitter establishConnection(Integer studentId) {

		SseEmitter emitter = new SseEmitter(1000L * 10 * 60);
		emitters.putIfAbsent(studentId, emitter);

		

		emitter.onTimeout(() -> {
			System.out.println("timed out from chat");
			emitter.complete();
		});

		emitter.onCompletion(() -> {
			System.out.println("completed chat");
			emitters.remove(studentId);
		});
		;

		return emitter;

	}

	private long currentlyOnlineMembers(List<Integer> memberIds) {

		return emitters.keySet().stream().filter(key -> memberIds.contains(key)).count();
	}

}

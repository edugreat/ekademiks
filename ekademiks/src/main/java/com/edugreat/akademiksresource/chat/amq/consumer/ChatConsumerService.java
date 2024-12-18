package com.edugreat.akademiksresource.chat.amq.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ChatConsumerService.class);
	
	private static final long HEARTBEAT_INTERVAL = 30000; // 30 seconds 

	@RabbitListener(queues = {"${instant.chat.queue}"})
 synchronized	void consumeInstantChatMessage(ChatDTO chat) {

		LOGGER.info(String.format("received JSON  message -> %s", chat));

//		get IDs of group members
		List<Integer> memberIds = groupMembersDao.getMemberIds(chat.getGroupId());

		final long currentlyOnline = currentlyOnlineMembers(memberIds);

//		collect the IDs to be removed once there is exception
		List<Integer> toBeRemoved = new ArrayList<>();
		
//		send instant message to members currently online
		emitters.forEach((studentId, emitter) -> {

			try {
				if (memberIds.contains(studentId)) {

					chat.setOnlineMembers(currentlyOnline);
					
					System.out.println("current emitters size: "+emitters.size());
					emitter.send(SseEmitter.event().data(chat).name("chats"));
				}
			} catch (IOException e) {

//				mark the ID as fit for removal
				toBeRemoved.add(studentId);

				LOGGER.error("error sending message to ",studentId, e.getMessage());
			}

		});
		
//		remove all the IDs marked for removal
		toBeRemoved.forEach(emitters::remove);

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

				LOGGER.info(String.format("Error notifying group admin %s", groupAdminId));
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
		if (studentId != null && emitters.containsKey(studentId)) {

			chat.setOnlineMembers(currentlyOnline);
			try {
				emitters.get(studentId).send(SseEmitter.event().data(chat).name("chats"));
			} catch (IOException e) {

				emitters.remove(studentId);
				LOGGER.info(String.format("unable to send previous chat to %s", studentId));
			}
		}
	}

//	notifies group members about a new member that just joined the group
	@RabbitListener(queues = {"${chat.notifications.queue}"})
	void notifyForNewMember(MiscellaneousNotifications notification) {

//		get the group members id
		List<Integer> groupMembersId = groupMembersDao.getMemberIds(notification.getTargetGroup());

		List<Integer> toBeRemoved = new ArrayList<>();
		
		emitters.keySet().stream().filter(userId -> groupMembersId.contains(userId)).forEach(loggedInUser -> {

			try {
				emitters.get(loggedInUser).send(SseEmitter.event().data(notification).name("chats"));
			} catch (IOException e) {

				LOGGER.info("error notifying member: ", loggedInUser, e.getMessage());
				toBeRemoved.add(loggedInUser);
				

				System.out.println("Error notifying logged in group members: " + e.getMessage());
			}

		});
		
		toBeRemoved.forEach(emitters::remove);

	}


	@RabbitListener(queues = {"${chat.notifications.queue}"})
	void sendPreviousChatNotifications(Set<MiscellaneousNotifications> previousNotifications) {

//    	get the student id the notifications targets at
		final Integer studentId = previousNotifications.stream().toList().get(0).getReceipientId();

		if (studentId != null && emitters.containsKey(studentId)) {

			final SseEmitter emitter = emitters.get(studentId);

			for (MiscellaneousNotifications notification : previousNotifications) {

				try {
					emitter.send(SseEmitter.event().data(notification).name("chats"));
				} catch (IOException e) {

					emitters.remove(studentId);

					LOGGER.info(String.format("Error emitting previous notifications to", studentId));

					return; // stop further notification

				}
			}
		}

	}
	

	@Override
	public SseEmitter establishConnection(Integer studentId) {

		SseEmitter emitter = new SseEmitter(1000L * 20 * 60);

		emitters.putIfAbsent(studentId, emitter);

		emitter.onCompletion(() -> {

			emitters.remove(studentId);
		});

		emitter.onTimeout(() -> emitter.complete());

		emitter.onError(e -> {

			emitters.remove(studentId);
			
			LOGGER.info(String.format("Establishing connection %s", e.getMessage()));
		});
		
		startHeartbeat(emitter, studentId);

		return emitter;

	}

	private long currentlyOnlineMembers(List<Integer> memberIds) {

		return emitters.keySet().stream().filter(key -> memberIds.contains(key)).count();
	}
	
	 private void startHeartbeat(SseEmitter emitter, Integer studentId) {  
	        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();  
	        executorService.scheduleAtFixedRate(() -> {  
	            try {  
	                emitter.send(SseEmitter.event().data("heartbeat").name("heartbeat"));  
	            } catch (IOException e) {  
	                LOGGER.error("Error sending heartbeat: " + e.getMessage());  
	                emitters.remove(studentId); // Remove the emitter if it fails  
	                executorService.shutdown(); // Stop the heartbeat task if emitter is no longer valid  
	            }  
	        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);  
	    }

	@Override
	public SseEmitter disconnectFromSSE(Integer studentId) {
		
		
		if(emitters.containsKey(studentId)) {
			
			return emitters.remove(studentId);
		}
		
		return null;
		
	}  

}

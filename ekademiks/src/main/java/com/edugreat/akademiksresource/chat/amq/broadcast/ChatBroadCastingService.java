package com.edugreat.akademiksresource.chat.amq.broadcast;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.edugreat.akademiksresource.chat.dao.MiscellaneousNotificationsDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

// service that broadcasts previous and instant chat messages to online group members
@Service
public class ChatBroadCastingService implements ChatBroadcaster {
	
	@Autowired
	MiscellaneousNotificationsDao miscellaneousNotifications;
	
//	rabbitmq exchange 
	@Value("${ekademiks.exchange.name}")
	private String exchange;
		
	@Value("${previous.chat.routing.key}")
	private String previousChatRoutingKey;
	
	@Value("${instant.chat.routing.key}")
	private String instantChatRoutingKey;
	
	@Value("${chat.notifications.routing.key}")
	private String chatNotificationRoutingKey;
	
//	rabbit template for sending messages to rabbit exchange
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBroadCastingService.class);
	

	
//	key is the student ID
	@Override
	public void previousChatMessages(List<ChatDTO> chats) {
		
		
		
		
		if(chats != null && chats.size() > 0) {
			
			for(ChatDTO chat :chats) {
				rabbitTemplate.convertAndSend(exchange, previousChatRoutingKey, chat);
			}
		}
		
		
	}

	
	@Override
	public void sendInstantChat(ChatDTO chat) {
	
//		get the number of group members currently online.
		rabbitTemplate.convertAndSend(exchange, instantChatRoutingKey, chat);
		
		
		

	}

	@Override
	public void sendJoinRequestNotification(MiscellaneousNotifications requestNotification) {
		
		if(requestNotification != null) {
			LOGGER.info(String.format("exchanging request notification -> %s", requestNotification));
			
			
			rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, requestNotification);
			
		}
		
		

	}

//	broadcasts about a new member joining  the group
	@Override
	public void notifyOnNewMember(MiscellaneousNotifications newMemberNotification) {
	
		if(newMemberNotification != null) {
			
			LOGGER.info(String.format("new member just joined -> %s", newMemberNotification));
			
			rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, newMemberNotification);
			
		}

	}



//get chat notifications such as some new members and or request to join the group
@Override
 public void broadcastPreviousChatNotifications(Set<MiscellaneousNotifications> chatNotifications, Integer targetGroupId) {

	if(chatNotifications != null && chatNotifications.size() > 0) {
		
		for(MiscellaneousNotifications notification : chatNotifications) {
			
			
			notification.setTargetGroup(targetGroupId);
		
		rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, notification);
		}
		
	}
	
	
}




}

package com.edugreat.akademiksresource.chat.amq.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dao.GroupChatDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j

public class ChatConsumerService implements ChatConsumer {

  @Autowired
  private GroupChatDao groupChatDao;
  
  @Autowired
  private ChatInterface chatInterface;

  private final Map<Integer, Sinks.Many<ServerSentEvent<?>>> userSinks = new ConcurrentHashMap<>();
  
//  maintains a concurrent map of connected users where key is the user ID and value, a list of the group IDs they belong to
  private Map<Integer, List<Integer>> connectedUsers = new ConcurrentHashMap<>();
  
  @RabbitListener(queues = { "${instant.chat.queue}" })
  public void consumeInstantChatMessage(ChatDTO chat) {
	  
	  log.info("received instant chat:{}",chat);
	  
	  final Integer groupId = chat.getGroupId();
	
	  connectedUsers.entrySet().stream()
	  .filter(entry -> entry.getValue().contains(groupId))
	  .map(Map.Entry::getKey)
	  .forEach(userId -> {
		  
		  Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(userId);
		  
		  if(sink != null) {
			  
			  sink.tryEmitNext(ServerSentEvent.builder(chat).event("chats").build());
		  }
	  });

  

  }

//  receives and publishes notifications to the group admin about a user intending to join the group chat
  @RabbitListener(queues = { "${chat.notifications.queue}" })
   void sendJoinRequestNotification(MiscellaneousNotifications requestNotification) {

//     get the groupAdmin ID
    final Integer groupAdminId = groupChatDao.getAdminId(requestNotification.getTargetGroupChat());
 
    if(groupAdminId != null && connectedUsers.containsKey(groupAdminId)) {
    	
    	Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(groupAdminId);
    	
    	if(sink != null) {
    		
    		sink.tryEmitNext(ServerSentEvent.builder(requestNotification).event("notifications").build());
    	}
    }

  }

  @RabbitListener(queues = { "${previous.chat.queue}" })
 void publishPreviousChatsMessages(List<ChatDTO> chats) {
    
	  final Integer destination = chats.get(0).getChatReceipient();
	  
	  if(connectedUsers.containsKey(destination)) {
		  
		  Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(destination);
		  
		  if(sink != null) {
			  
			  sink.tryEmitNext(ServerSentEvent.builder(chats).event("chats").build());
		  }
	  }
    
 
  }

//  notifies group members about a new member that just joined the group
  @RabbitListener(queues = { "${chat.notifications.queue}" })
  void notifyForNewMember(MiscellaneousNotifications notification) {

//    get the group members id
    final List<Integer> members = groupMembersOnline(notification.getTargetGroupChat());
   
    members.forEach(userId -> {
    	
    	Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(userId);
    	
    	if(sink != null) {
    		
    		sink.tryEmitNext(ServerSentEvent.builder(notification).event("notifications").build());
    	}
    });
  
    

  }

  @RabbitListener(queues = { "${chat.notifications.queue}" })
   void sendPreviousChatNotifications(List<MiscellaneousNotifications> previousNotifications) {

//      get the student id the notifications targets at
    final Integer userId = previousNotifications.get(0).getReceipientId();

    if(userId != null && connectedUsers.containsKey(userId)) {
    	
    	Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(userId);
    	
    	if(sink != null) {
    		
    		sink.tryEmitNext(ServerSentEvent.builder(previousNotifications).event("notifications").build());
    		
    	}
    }
   

  }

  @Override
  public Flux<ServerSentEvent<?>> establishConnection(Integer studentId) {
      Sinks.Many<ServerSentEvent<?>> sink = Sinks.many().unicast().onBackpressureBuffer();
      userSinks.put(studentId, sink);
      connectedUsers.put(studentId, userGroupIds(studentId));
      
      // Add heartbeat
      Flux<ServerSentEvent<Object>> heartbeat = Flux.interval(Duration.ofSeconds(30))
          .map(i -> ServerSentEvent.builder().comment("heartbeat").event("heartbeat").build())
          .doFinally(signal -> cleanup(studentId));
      
      return sink.asFlux()
          .mergeWith(heartbeat)
          .doOnCancel(() -> cleanup(studentId))
          .doOnError(e -> cleanup(studentId))
          .doOnTerminate(() -> cleanup(studentId));
  }
  
  private List<Integer> userGroupIds(Integer studentId) {
      return chatInterface.myGroupIds(studentId);
  }

 
  
//get a list of users of a group currently online
private List<Integer> groupMembersOnline(Integer groupId) {
  
  return connectedUsers.entrySet()
      .stream()
      .filter(x -> x.getValue().contains(groupId))
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  
}

  private void cleanup(Integer connectionId) {
	  
	  
	  Sinks.Many<ServerSentEvent<?>> sink = userSinks.remove(connectionId);
	  if(sink != null) {
		  
		  sink.tryEmitComplete();
	  }
	  
	  connectedUsers.remove(connectionId);
	
  }

 
 
}

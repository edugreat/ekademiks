package com.edugreat.akademiksresource.chat.amq.consumer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.edugreat.akademiksresource.chat._interface.ChatInterface;
import com.edugreat.akademiksresource.chat.dao.GroupChatDao;
import com.edugreat.akademiksresource.chat.dto.ChatDTO;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatConsumerService implements ChatConsumer {

  @Autowired
  private GroupChatDao groupChatDao;
  
  @Autowired
  private ChatInterface chatInterface;

  private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
  
//  maintains a concurrent map of connected users where key is the user ID and value, a list of the group IDs they belong to
  private Map<Integer, List<Integer>> connectedUsers = new ConcurrentHashMap<>();
  
  private final Map<Integer, ScheduledExecutorService> heartbeatExecutors = new ConcurrentHashMap<>();

  private static final long HEARTBEAT_INTERVAL = 30000;
  
  @RabbitListener(queues = { "${instant.chat.queue}" })
  public void consumeInstantChatMessage(ChatDTO chat) {
	  
	  System.out.println("sending instant chat to");

    
    final Integer groupId = chat.getGroupId();
    
    
    
    for(Map.Entry<Integer, List<Integer>> entry  : connectedUsers.entrySet()) {
    	
    	final Integer userId = entry.getKey();
    	
    	
//    	checks if the connection is still alive
    	if(emitters.get(userId) != null) {
    		
    		final boolean connected = isConnectionAlive(emitters.get(userId));
    		
    		if(!connected) {
    			
    			cleanup(userId);
    			
    			continue;
    		}
    		
    		if(!entry.getValue().contains(groupId))  continue; //not a group member
    		
    		 
            try {
              emitters.get(userId).send(SseEmitter.event().data(chat).name("chats"));
            } catch (IOException e) {
            	
            	 log.info("unable to send instant message to: {}", userId);
            	continue;
              
             
            }
    		
    	} else {
    		
    		cleanup(userId);
    		continue;
    	}
    	

    	
    }
    

  

  }

//  receives and publishes notifications to the group admin about a user intending to join the group chat
  @RabbitListener(queues = { "${chat.notifications.queue}" })
   void sendJoinRequestNotification(MiscellaneousNotifications requestNotification) {

//     get the groupAdmin ID
    final Integer groupAdminId = groupChatDao.getAdminId(requestNotification.getTargetGroupChat());
    
  
//    check if the group admin is currently connected to receive notification
    if (groupAdminId != null && connectedUsers.containsKey(groupAdminId) && emitters.containsKey(groupAdminId)) {
    	
    	final SseEmitter emitter = emitters.get(groupAdminId);
    	if(!isConnectionAlive(emitter)) {
    		
    		cleanup(groupAdminId);
    		
    		return;
    	}
      
      

      try {
    	  emitter.send(SseEmitter.event().data(requestNotification).name("notifications"));
      } catch (IOException e) {

   
        log.info(String.format("Error notifying group admin: {}", groupAdminId));
      }

    }

  }

  @RabbitListener(queues = { "${previous.chat.queue}" })
 void publishPreviousChatsMessages(List<ChatDTO> chats) {
    
    
    final Integer destination = chats.get(0).getChatReceipient();

//    check if the user is online
    if (connectedUsers.containsKey(destination) && emitters.get(destination) != null) {
    	
    	final SseEmitter emitter = emitters.get(destination);
    	
    
      try {
    	  emitter.send(SseEmitter.event().data(chats).name("chats"));
      } catch (IOException e) {

        
        log.info(String.format("unable to send previous chat to: {}", destination));
        cleanup(destination);
      }
    }
  }

//  notifies group members about a new member that just joined the group
  @RabbitListener(queues = { "${chat.notifications.queue}" })
  void notifyForNewMember(MiscellaneousNotifications notification) {

//    get the group members id
    final List<Integer> members = groupMembersOnline(notification.getTargetGroupChat());
    
    for(Integer userId : members) {
    	
    	final SseEmitter emitter = emitters.get(userId);
    	
    	if(emitter != null && isConnectionAlive(emitter)) {
    		
    		try {
				
    			emitter.send(SseEmitter.event().data(notification).name("notifications"));
    			
			} catch (Exception e) {
				
				continue;
			}
    		
    	}else {
    		
    		cleanup(userId);
    		continue;
    	}
    	
    
    }
  
  
    

  }

  @RabbitListener(queues = { "${chat.notifications.queue}" })
   void sendPreviousChatNotifications(List<MiscellaneousNotifications> previousNotifications) {

//      get the student id the notifications targets at
    final Integer studentId = previousNotifications.get(0).getReceipientId();

    if (studentId != null && connectedUsers.containsKey(studentId) && emitters.get(studentId) != null) {
       
    	 final SseEmitter emitter = emitters.get(studentId);
    	
    if(!isConnectionAlive(emitter)) {
    	
    	cleanup(studentId);
    	return;
    	
    }
      
     

      try {
        
        emitter.send(SseEmitter.event().data(previousNotifications).name("notifications"));
      } catch (IOException e) {

      

        log.info(String.format("Error emitting previous notifications to : {}", studentId+" -> groupId: "+previousNotifications.get(0).getTargetGroupChat()));


      }

    }

  }

  @Override
  public SseEmitter establishConnection(Integer studentId) {
    
  
	  if(emitters.containsKey(studentId)) return emitters.get(studentId);
	  
	  


	    SseEmitter emitter = new SseEmitter(0l);
	    
	    
	    emitters.put(studentId, emitter);
	   
	    connectedUsers.put(studentId, userGroupIds(studentId));
	    
	    
	    ScheduledExecutorService heartbeatExecutor = startHeartbeat(studentId);
	   
	    heartbeatExecutors.put(studentId, heartbeatExecutor);
	    
	    
	    
	    
	    emitter.onCompletion(() -> cleanup(studentId));
	    
	    emitter.onError(e -> {
	    	
	    	System.out.println("connection error:");
	    	
	    	
	    	cleanup(studentId);
	    });
	    emitter.onTimeout(() -> cleanup(studentId));
	   
	   

	      return emitter;
	      
      
    }
  
  private List<Integer> userGroupIds(Integer studentId){
    
   return chatInterface.myGroupIds(studentId);
    
  }

    

 
  
//get a list of users of a group currently online
private List<Integer> groupMembersOnline(Integer groupId) {
  
  return connectedUsers.entrySet()
      .stream()
      .filter(x -> x.getValue().contains(groupId))
      .map(x -> x.getKey())
      .collect(Collectors.toList());
  
}

  private ScheduledExecutorService startHeartbeat(Integer connectionId) {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(() -> {
      
      if(emitters.containsKey(connectionId)) {
    	  
    	  if(! isConnectionAlive(emitters.get(connectionId))) {
    		  
    		  log.info("Connection is inactive. Cleaning up: {}", connectionId);
    		  
    		  cleanup(connectionId);
    		  
    		  return;
    	  }
        try {
          emitters.get(connectionId).send(SseEmitter.event().comment("heartbeat").name("heartbeat"));
        } catch (IOException e) {
          log.info("error sending hearbeat:{}", connectionId);
          
          cleanup(connectionId);
        }
      }
    }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
  
    return executorService;
  }

 
  
//  cleanup method in case of sse errors
  private void cleanup(Integer connectionId) {
    
    
    emitters.remove(connectionId);
    connectedUsers.remove(connectionId);
    
    ScheduledExecutorService executorService = heartbeatExecutors.get(connectionId);
    
    if(executorService != null) {
      
      heartbeatExecutors.remove(connectionId);
      
      executorService.shutdownNow();
      
    }
}
  
//  periodic checks for active connections to avoid error resulting in sending to stale connection
  private boolean isConnectionAlive(SseEmitter emitter) {
	  
	  try {
		  
		emitter.send(SseEmitter.event().comment("ping"));
		
		return true;
	} catch (Exception e) {
		
		return false;
	}
  }

}

package com.edugreat.akademiksresource.chat.livepresence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/*
  * Service class keeps atomic track of user presence for each group chat maintained in its map instance
  */
@Service
public class LivePresenceMonitorService {
	
	private Map<Integer, AtomicInteger> activeUserPerGroup = new ConcurrentHashMap<>();
	
	private Map<ConnectionId, SseEmitter> connectors = new ConcurrentHashMap<>();
	
	public SseEmitter updateLivePresence(List<Integer> userGroupIds, Integer userId) {
		
		System.out.println("--------------");
		
		
		
		if(!isConnected(userId)) {
			
			System.out.println("is not connected");
			
			ConnectionId newConnection = new ConnectionId(userId, userGroupIds);
			
			 return createNewConnection(newConnection);
			
			
		}
	
		return getUserConnector(userId);
	
	}
	
	private SseEmitter createNewConnection(ConnectionId connection) {
		
		
			
			SseEmitter emitter = new SseEmitter(0L);
			
			connectors.put(connection, emitter);
			emitter.onError(error -> {
				
				System.out.println("error creating live presence notifier");
				 cleanup(connection);
			});
			
			emitter.onCompletion(() -> {
				
				System.out.println("live presence completed");
				cleanup(connection);
			});
			
			emitter.onTimeout(() -> {
				
				System.out.println("live presence timedout");
				cleanup(connection);
			});
			
			updateActiveGroups(connection.getGroupIds());
		
			
		
		return emitter;
	}
	
//	tracks if a user has already been connected to the connectors map
	private boolean isConnected(Integer userId) {
		
		return connectors.keySet().stream().filter(key -> key.getUserId().equals(userId)).toList().size() > 0;
	}
	
	private void updateActiveGroups(List<Integer> groupIds) {
		
		System.out.println("updating active groups");
		System.out.println(groupIds);
		
		for(Integer id: groupIds) {
			
			if(activeUserPerGroup.containsKey(id)) {
				
				activeUserPerGroup.get(id).incrementAndGet();
				
				continue;
			}
			
				activeUserPerGroup.put(id, new AtomicInteger(0));
				activeUserPerGroup.get(id).incrementAndGet();
			
		}
		
		
	}
	
//	method that returns SSE connector given a student ID
	private SseEmitter getUserConnector(Integer studentId) {
		
		for(Map.Entry<ConnectionId, SseEmitter> connectionMap : connectors.entrySet()) {
			
			if(connectionMap.getKey().getGroupIds().contains(studentId)) {
				
				return connectionMap.getValue();
			}
		}
		
		return null;
	}
	
//	cleanup implementation due to error on SSE 
	private void cleanup(ConnectionId connectionId) {
		
		connectors.remove(connectionId);
		
		for(Integer groupId : connectionId.getGroupIds()) {
			
			if(activeUserPerGroup.containsKey(groupId)) {
				
				if(activeUserPerGroup.get(groupId).get() == 0) {
					activeUserPerGroup.remove(groupId);
					continue;
				}
				
				activeUserPerGroup.get(groupId).decrementAndGet();
			}
		}
		
		
		
	}
	
	@Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
	public void sendLivePresence() {
		
		System.out.println("executing scheduled task");
		System.out.println(">>>>>>>>>>>>");
		
		connectors.forEach((connector, emitter) -> {
			
			
			
			for(Integer groupId: connector.getGroupIds()) {
				
				int userCount = activeUserPerGroup.getOrDefault(groupId, new AtomicInteger(0)).get();
				try {
					emitter.send(SseEmitter.event().data(Map.of("key", groupId, "value", userCount)).name("live-presence"));
				} catch (IOException e) {
					
					System.out.println("error notifying");
				}
			}
			
		});
		
		
	}
	
	


}

 // an object representing user connection identification
 class ConnectionId{
	 
	 private Integer userId;
	 
	 private List<Integer> groupIds = Collections.synchronizedList(new ArrayList<>());
	 
	 public ConnectionId(Integer userId, List<Integer> groupIds) {
		 
		 this.userId = userId;
		 addUserGroupIds(groupIds);
		 
	 }
	 
	 

	public Integer getUserId() {
		return userId;
	}



	public List<Integer> getGroupIds() {
		return groupIds;
	}

	


	private void addUserGroupIds(List<Integer> groupIds) {
		
		
		for(Integer id : groupIds) {
			System.out.println(id);
			if(!this.groupIds.contains(id)) {
				this.groupIds.add(id);
			}
		}
	}



	@Override
	public int hashCode() {
		
		return Objects.hash(userId);
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj != null && this == obj) return true;
		if(getClass() == obj.getClass()) return true;
		
		ConnectionId that = (ConnectionId)obj;
		
		return Objects.equals(userId, that.userId);
		
		
	}
	 
	 
	
}

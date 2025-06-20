package com.edugreat.akademiksresource.chat.livepresence;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/*
  * Service class keeps atomic track of user presence for each group chat maintained in its map instance
  */
@Service
@Slf4j
@RequiredArgsConstructor
public class LivePresenceMonitorService {

    // Track active users per group
    private final Map<Integer, AtomicInteger> activeUserPerGroup = new ConcurrentHashMap<>();
    
    // Reactive event sinks for each user
    private final Map<Integer, Sinks.Many<ServerSentEvent<?>>> userSinks = new ConcurrentHashMap<>();
    
    // Rich connection metadata
    private final Map<Integer, ConnectionId> connections = new ConcurrentHashMap<>();

    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(30);


	public Flux<ServerSentEvent<?>> updateLivePresence(List<Integer> userGroupIds, Integer userId) {

		if (!connections.containsKey(userId)) {

			ConnectionId newConnection = new ConnectionId(userId, userGroupIds);

			
			connections.put(userId, newConnection);
			
			Sinks.Many<ServerSentEvent<?>> sink = userSinks.computeIfAbsent(userId,
		id -> Sinks.many().unicast().onBackpressureBuffer());
		
			updateActiveGroups(userGroupIds, true);
			userGroupIds.forEach(this::broadcastPresenceUpdate);
			
			
//			heartbeat stream
			Flux<ServerSentEvent<?>> heartbeat = startHeartbeat();
		
			
//			initial presence update
			Flux<ServerSentEvent<?>> initialPresence = computeInitialPresence(userGroupIds);
			
//			combine all streams
			return sink.asFlux()
					.mergeWith(initialPresence)
					.mergeWith(heartbeat)
					.doOnCancel(() -> cleanup(userId))
					.doOnTerminate(() -> cleanup(userId))
					.doOnError(e -> cleanup(userId));
		
		}

		return userSinks.get(userId).asFlux()
				.mergeWith(computeInitialPresence(userGroupIds))
				.mergeWith(startHeartbeat())
				.doOnCancel(() -> cleanup(userId))
				.doOnTerminate(() -> cleanup(userId))
				.doOnError(e -> cleanup(userId));
	

	}

	private Flux<ServerSentEvent<?>> computeInitialPresence(List<Integer> userGroupIds) {
		Flux<ServerSentEvent<?>> initialPresence = Flux.fromIterable(userGroupIds)
				.map(groupId -> createPresenceEvent(groupId));
		return initialPresence;
	}

	private Flux<ServerSentEvent<?>> startHeartbeat() {
		Flux<ServerSentEvent<?>> heartbeat = Flux.interval(HEARTBEAT_INTERVAL)
				.map(i -> ServerSentEvent.builder().comment("heartbeat")
				.event("heartbeat")
				.build());
		return heartbeat;
	}

	private ServerSentEvent<?> createPresenceEvent(Integer groupId) {
		
		return ServerSentEvent.builder()
				.data(Map.of("key", groupId,
						"value", activeUserPerGroup.getOrDefault(groupId, new AtomicInteger(0)).get()))
				.event("live-presence")
				.build();

		
	}

	
	private void updateActiveGroups(List<Integer> groupIds, boolean increment) {

		groupIds.forEach(groupId -> {
			
			AtomicInteger count = activeUserPerGroup.computeIfAbsent(groupId,
				k -> new AtomicInteger(0));
			
			if(increment) {
				
				count.incrementAndGet();
				
			}else {
				count.decrementAndGet();
			}
			if(count.get() <= 0) {
				
				activeUserPerGroup.remove(groupId);
			}
		});
		}

		
	

//	method that returns SSE connector given a student ID
	private void broadcastPresenceUpdate(Integer groupId) {
		
		int count = activeUserPerGroup.getOrDefault(groupId, new AtomicInteger(0)).get();
		
		ServerSentEvent<?> event = ServerSentEvent.builder()
				.data(Map.of("key", groupId, "value", count))
				.event("live-presence")
				.build();
		
		connections.values().stream()
		.filter(conn -> conn.getGroupIds().contains(groupId))
		.map(ConnectionId::getUserId)
		.forEach(userId -> {
			
			Sinks.Many<ServerSentEvent<?>> sink = userSinks.get(userId);
			
			if(sink != null) {
				sink.tryEmitNext(event);
			}
		});
				

	}

//	cleanup implementation due to error on SSE 
	private void cleanup(Integer userId) {

		ConnectionId conn = connections.remove(userId);
		
		if(conn != null) {
			
			updateActiveGroups(conn.getGroupIds(), false);
			conn.getGroupIds().forEach(this::broadcastPresenceUpdate);
		}

	}

	

	@Scheduled(fixedRate = 30000) // Every 30 seconds
	public void checkActiveConnections() {
		
		log.info("schedule presence checks");
	    connections.values().stream()
	        .flatMap(conn -> conn.getGroupIds().stream())
	        .distinct()
	        .forEach(groupId -> {
	            int currentCount = activeUserPerGroup.getOrDefault(groupId, new AtomicInteger(0)).get();
	            if (currentCount > 0) {
	                broadcastPresenceUpdate(groupId); // Sync all groups periodically
	            }
	        });
	}
}

// an object representing user connection identification

    class ConnectionId {
    private final Integer userId;
    private final List<Integer> groupIds;
    private final Instant connectedAt;

    public ConnectionId(Integer userId, List<Integer> groupIds) {
        this.userId = Objects.requireNonNull(userId);
        this.groupIds = Collections.unmodifiableList(groupIds);
        this.connectedAt = Instant.now();
    }

    public Integer getUserId() {
        return userId;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public Instant getConnectedAt() {
        return connectedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionId that = (ConnectionId) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "ConnectionId{" +
            "userId=" + userId +
            ", groupIds=" + groupIds +
            ", connectedAt=" + connectedAt +
            '}';
    }
}
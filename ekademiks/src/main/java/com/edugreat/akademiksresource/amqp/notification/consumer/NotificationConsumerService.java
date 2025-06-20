package com.edugreat.akademiksresource.amqp.notification.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationConsumerService implements NotificationConsumer {

    private final Map<Integer, Sinks.Many<ServerSentEvent<?>>> clientSinks = new ConcurrentHashMap<>();
    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(30);

    @RabbitListener(queues = "${previous.notification.queue}")
    public void consumePreviousNotification(AssessmentUploadNotification notification) {
        notification.getReceipientIds().stream()
            .filter(clientSinks::containsKey)
            .forEach(userId -> emitNotification(notification, userId));
    }

    @RabbitListener(queues = "${instant.notification.queue}")
    public void consumeInstantNotification(AssessmentUploadNotification notification) {
        List<Integer> recipientIds = notification.getReceipientIds();
        
        if (recipientIds == null || recipientIds.isEmpty()) {
            // Broadcast to all connected clients
            clientSinks.keySet().forEach(userId -> emitNotification(notification, userId));
        } else {
            // Send to specific recipients
            recipientIds.stream()
                .filter(clientSinks::containsKey)
                .forEach(userId -> emitNotification(notification, userId));
        }
    }

    @Override
    public Flux<ServerSentEvent<?>> establishConnection(Integer studentId) {
        Sinks.Many<ServerSentEvent<?>> sink = Sinks.many().unicast().onBackpressureBuffer();
        clientSinks.put(studentId, sink);

        // Heartbeat stream
        Flux<ServerSentEvent<?>> heartbeat = Flux.interval(HEARTBEAT_INTERVAL)
            .map(i -> ServerSentEvent.builder()
                .comment("heartbeat")
                .event("heartbeat")
                .build());

        return sink.asFlux()
            .mergeWith(heartbeat)
            .doOnCancel(() -> cleanup(studentId))
            .doOnTerminate(() -> cleanup(studentId));
    }

    @Override
    public void disconnectFromSSE(Integer studentId) {
        cleanup(studentId);
    }

    private void emitNotification(AssessmentUploadNotification notification, Integer userId) {
        Sinks.Many<ServerSentEvent<?>> sink = clientSinks.get(userId);
        if (sink != null) {
            sink.tryEmitNext(
                ServerSentEvent.builder(notification)
                    .event("notifications")
                    .build()
            );
        }
    }

    private void cleanup(Integer studentId) {
        Sinks.Many<ServerSentEvent<?>> sink = clientSinks.remove(studentId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }
}
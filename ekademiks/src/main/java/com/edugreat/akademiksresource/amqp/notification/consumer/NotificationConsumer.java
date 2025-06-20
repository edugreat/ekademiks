package com.edugreat.akademiksresource.amqp.notification.consumer;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationConsumer {
	
	Flux<ServerSentEvent<?>> establishConnection(Integer studentId);
	
//	disconnects the user from SSE notification once they logout from the client
	public void disconnectFromSSE(Integer studentId);

}

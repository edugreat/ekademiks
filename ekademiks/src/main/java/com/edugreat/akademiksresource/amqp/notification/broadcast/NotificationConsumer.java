package com.edugreat.akademiksresource.amqp.notification.broadcast;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationConsumer {
	
	SseEmitter establishConnection(Integer studentId);
	
//	disconnects the user from SSE notification once they logout from the client
	public SseEmitter disconnectFromSSE(Integer studentId);

}

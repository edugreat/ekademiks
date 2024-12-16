package com.edugreat.akademiksresource.chat.amq.consumer;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatConsumer {
	
//	establishes connection so student can receive both previous and instant chat messages
	SseEmitter establishConnection(Integer studentId);
	
//	disconnects the user from SSE once they log out from the client 
	public SseEmitter disconnectFromSSE(Integer studentId);
}
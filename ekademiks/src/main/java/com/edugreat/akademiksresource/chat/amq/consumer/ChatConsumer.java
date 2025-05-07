package com.edugreat.akademiksresource.chat.amq.consumer;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatConsumer {
	
//	establishes connection so student can receive both previous and instant chat messages for the given group
	SseEmitter establishConnection(Integer studentId, Integer groupId);
	
//	disconnects the user from SSE once they log out from the client or gets disconnected due to max-size restriction
//	The key of client map is groupId while the value is the studentId
	public void disconnectGroup(Map.Entry<Integer, Integer> client);
}
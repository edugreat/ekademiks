package com.edugreat.akademiksresource.chat.amq.consumer;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatConsumer {
	
//	establishes connection so student can receive both previous and instant chat messages for the groups they belong in
	SseEmitter establishConnection(Integer studentId);
	
	

}
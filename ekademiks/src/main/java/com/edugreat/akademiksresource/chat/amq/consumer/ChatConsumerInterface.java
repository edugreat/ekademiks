package com.edugreat.akademiksresource.chat.amq.consumer;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatConsumerInterface {
	
//	establishes connection so student can receive both previous and instant chat messages
	SseEmitter establishConnection(Integer studentId);
}
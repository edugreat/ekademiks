package com.edugreat.akademiksresource.chat.amq.consumer;

import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.Flux;

public interface ChatConsumer {
	
//	establishes connection so student can receive both previous and instant chat messages for the groups they belong in
	 Flux<ServerSentEvent<?>> establishConnection(Integer studentId);
	
	

}
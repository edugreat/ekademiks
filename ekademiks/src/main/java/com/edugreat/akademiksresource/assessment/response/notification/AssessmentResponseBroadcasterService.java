package com.edugreat.akademiksresource.assessment.response.notification;

import java.util.Collection;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// service that provides implementations to the underlying interface
@Service
public class AssessmentResponseBroadcasterService implements AssignmentResponseBroadcaster {

	@Value("${ekademiks.exchange.name}")
	private String exchange;
	
	@Value("${previous.assessment.response.notification.routing.key}")
	private String previousAssessmentResponseRoutingKey;
	
    @Value("${instant.assessment.response.notification.routing.key}")
	private String instantAssessmentResponseRoutingKey;
    

//	rabbit template for sending messages to rabbit exchange
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override
	public void broadcastPreviousNotifications(Collection<AssessmentResponseRecord> notifications) {
		
		
		for(var notification: notifications) {
			
			
			
			rabbitTemplate.convertAndSend(exchange, previousAssessmentResponseRoutingKey, notification);
		}
	}

	@Override
	public void broadcastInstantNotification(AssessmentResponseRecord notification) {
		
		rabbitTemplate.convertAndSend(exchange, instantAssessmentResponseRoutingKey, notification);

	}

}

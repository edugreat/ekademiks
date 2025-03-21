package com.edugreat.akademiksresource.amqp.notification.broadcast;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.model.AssessmentUploadNotification;

@Service
public class NotificationBroadcastService implements NotificationBroadcast {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${previous.notification.routing.key}")
	private String previousNotificationRoutingKey;
	

	@Value("${instant.notification.routing.key}")
	private String instantNotificationRoutingKey;

	@Value("${ekademiks.exchange.name}")
	private String exchange;
	

	@Override
	public void getPreviousNotifications(List<AssessmentUploadNotification> notifications) {

		for (AssessmentUploadNotification notification : notifications) {

			rabbitTemplate.convertAndSend(exchange, previousNotificationRoutingKey, notification);
		}

	}

	@Override
	public void sendInstantNotification(AssessmentUploadNotification notification) {
		
		

		rabbitTemplate.convertAndSend(exchange, instantNotificationRoutingKey, notification);

	}

}

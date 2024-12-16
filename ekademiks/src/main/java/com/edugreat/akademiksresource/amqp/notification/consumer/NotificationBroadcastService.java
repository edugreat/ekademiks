package com.edugreat.akademiksresource.amqp.notification.consumer;

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

	@Value("${notification.routing.key}")
	private String notificationRoutingKey;

	@Value("${ekademiks.exchange.name}")
	private String exchange;

	@Override
	public void previousNotification(List<AssessmentUploadNotification> notifications) {

		for (AssessmentUploadNotification notification : notifications) {

			rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, notification);
		}

	}

	@Override
	public void instantNotification(AssessmentUploadNotification notification) {

		rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, notification);

	}

}

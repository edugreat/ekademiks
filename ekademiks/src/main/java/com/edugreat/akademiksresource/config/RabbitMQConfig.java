package com.edugreat.akademiksresource.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	
	@Value("${previous.notification.queue}")
	private String previousNotificationQueue;
	
	@Value("${previous.notification.routing.key}")
	private String previousNotificationRoutingKey;
	
	@Value("${instant.notification.queue}")
	private String instantNotificationQueue;
	
	@Value("${instant.notification.routing.key}")
	private String instantNotificationRoutingKey;
	
	@Value("${instant.chat.queue}")
	private String instantChatQueue;
	
	@Value("${instant.chat.routing.key}")
	private String instantChatRoutinKey;
	
	@Value("${previous.chat.queue}")
	private String previousChatQueue;
	
	@Value("${previous.chat.routing.key}")
	private String previousChatRoutinKey;
	
	
	@Value("${ekademiks.exchange.name}")
	private String exchange; 
	
	@Value("${previous.assessment.response.notification.queue}")
	private String previousAssessmentResponseNotificationQueue;
	
	@Value("${previous.assessment.response.notification.routing.key}")
	private String previousAssessmentResponseNotificationRoutingKey;
	
	
	@Value("${instant.assessment.response.notification.queue}")
	private String instantAssessmentResponseNotificationQueue;
	
	@Value("${instant.assessment.response.notification.routing.key}")
	private String instantAssessmentResponseNotificationRoutingKey;
	
	
//	for miscellaneous notifications like new member joining the group chat
	@Value("${chat.notifications.queue}")
	private String chatNotificationsQueue;
	
//	for miscellaneous notifications like new member joining the group chat
	@Value("${chat.notifications.routing.key}")
	private String chatNotificationsRoutingKey;
	
	

    @Bean
    Queue previousNotificationQueue() {
		
		return new Queue(previousNotificationQueue);
	}
    
    
    @Bean
    Queue instantNotificationQueue() {
    	
    	return new Queue(instantNotificationQueue);
    }
    @Bean
    Queue instantChatQueue() {
		
		return new Queue(instantChatQueue);
	}
	
    @Bean
    Queue previousChatQueue() {
    	
    	return new Queue(previousChatQueue);
    }

    @Bean
    Queue chatNotificationsQueue() {
		
		return new Queue(chatNotificationsQueue);
	}
    
   @Bean
   Queue previousAssessmentResponseNotificationQueue() {
	   
	   return new Queue(previousAssessmentResponseNotificationQueue);
   }
    
   
   
   @Bean
   Queue instantAssessmentResponseNotificationQueue() {
	   
	   return new Queue(instantAssessmentResponseNotificationQueue);
   }
    
    @Bean
	 TopicExchange exchange() {
		
		return new TopicExchange(exchange);
	}
    
    @Bean
    Binding instantChatBinding() {
    	
    	return BindingBuilder.bind(instantChatQueue()).to(exchange()).with(instantChatRoutinKey);
    }

    @Bean
    Binding chatNotificationsBinding() {
    	
    	return BindingBuilder.bind(chatNotificationsQueue()).to(exchange()).with(chatNotificationsRoutingKey);
    }
    
    
    @Bean
    Binding previousChatBinding() {
    	
    	return BindingBuilder.bind(previousChatQueue()).to(exchange()).with(previousChatRoutinKey);
    }
   
    
    @Bean
    Binding previousNotificationBinding() {
    	
    	return BindingBuilder.bind(previousNotificationQueue()).to(exchange()).with(previousNotificationRoutingKey);
    }
    
    @Bean
    Binding instantNotificationBinding() {
    	
    	return BindingBuilder.bind(instantNotificationQueue()).to(exchange()).with(instantNotificationRoutingKey);
    }
    
    @Bean
    Binding previousAssessmentResponseNotificationBinding() {
    	
    	return BindingBuilder.bind(previousAssessmentResponseNotificationQueue()).to(exchange()).with(previousAssessmentResponseNotificationRoutingKey);
    }
    
    
    @Bean
    Binding instantAssessmentResponseNotificationBinding() {
    	
    	return BindingBuilder.bind(instantAssessmentResponseNotificationQueue()).to(exchange()).with(instantAssessmentResponseNotificationRoutingKey);
    }
    
    @Bean
    MessageConverter converter() {
    	
    	return new Jackson2JsonMessageConverter();
    }
    
   
    @Bean
    AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
    	
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    	
    	rabbitTemplate.setMessageConverter(converter());
    	
    	return rabbitTemplate;
    }
}

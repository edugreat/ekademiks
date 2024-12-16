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
	
	
	@Value("${notification.queue}")
	private String notificationQueue;
	
	@Value("${notification.routing.key}")
	private String notificationRoutingKey;
	
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
	
	@Value("${chat.notifications.queue}")
	private String chatNotificationsQueue;
	
	@Value("${chat.notifications.routing.key}")
	private String chatNotificationsRoutingKey;
	
	
//	spring bean for rabbitmq queue
    @Bean
    Queue notificationQueue() {
		
		return new Queue(notificationQueue);
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
    Binding notificationBinding() {
    	
    	return BindingBuilder.bind(notificationQueue()).to(exchange()).with(notificationRoutingKey);
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

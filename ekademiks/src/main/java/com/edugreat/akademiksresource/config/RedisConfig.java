package com.edugreat.akademiksresource.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {

	@Primary
    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		
    	
    	RedisCacheConfiguration cacheConfiguration = 
    			RedisCacheConfiguration.defaultCacheConfig()
    			.entryTtl(Duration.ofMinutes(10))
    			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
    					new GenericJackson2JsonRedisSerializer()
    					));
    	
    	return  RedisCacheManager.builder(connectionFactory)
    			.cacheDefaults(cacheConfiguration).build();
    	
		
    	
	}

}

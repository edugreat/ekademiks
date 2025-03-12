package com.edugreat.akademiksresource.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	
	@Bean
	RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
	    // Default cache configuration with a TTL of 10 minutes
	    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
	            .entryTtl(Duration.ofMinutes(10)) // Set TTL to 10 minutes
	            .serializeValuesWith(RedisSerializationContext.SerializationPair
	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()));

	    // Custom cache configuration for specific cache names
	    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

	    // Example: Set TTL to 30 minutes for the "USER_CACHE"
	    cacheConfigurations.put(RedisValues.USER_CACHE, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofMinutes(Integer.MAX_VALUE))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 30 minutes
	                    .disableCachingNullValues());

//	    configures caching for subject names
	    cacheConfigurations.put(RedisValues.SUBJECT_NAMES, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(24))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 30 minutes
	                    .disableCachingNullValues());
	    
//	    configures caching for student's recent performance
//	    configures caching for subject names
	    cacheConfigurations.put(RedisValues.RECENT_PERFORMANCE, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(1))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 1 hour
	                    .disableCachingNullValues());
	    
//	    configures caching for assessment topics and durations
	    cacheConfigurations.put(RedisValues.TOPICS_AND_DURATIONS, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(1))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 24 hours
	                    .disableCachingNullValues());
//	    configures caching for assessment topic and duration(a particular assessment and its duration
	    cacheConfigurations.put(RedisValues.TOPIC_AND_DURATION, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(1))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 30 minutes
	                    .disableCachingNullValues());
	    
//	    configures caching for assessment test(an object of TestWrapper)
	    cacheConfigurations.put(RedisValues.ASSESSMENT_TEST, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(1))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 30 minutes
	                    .disableCachingNullValues());
	    
//	    configures caching for when a user joins a group chat
	    cacheConfigurations.put(RedisValues.JOIN_DATE, 
	            RedisCacheConfiguration.defaultCacheConfig()
	                    .entryTtl(Duration.ofHours(24))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair
	    	                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))// Set TTL to 30 minutes
	                    .disableCachingNullValues());
	    
	    
	    


	    // Create and return the RedisCacheManager
	    return RedisCacheManager.builder(connectionFactory)
	            .cacheDefaults(defaultCacheConfig) // Apply default configuration
	            .withInitialCacheConfigurations(cacheConfigurations) // Apply custom configurations
	            .build();
	}
	
	@Bean
     RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Use GenericJackson2JsonRedisSerializer for values
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

}

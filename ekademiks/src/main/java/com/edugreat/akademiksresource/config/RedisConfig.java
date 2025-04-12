package com.edugreat.akademiksresource.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {
	
	
	@Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());

        var valueSerializer = RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer);
        return (builder) -> builder
                .withCacheConfiguration(RedisValues.ASSESSMENT_TEST,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(200)))
                .withCacheConfiguration(RedisValues.ASSESSMENT_TOPICS,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration(RedisValues.ASSIGNMENT_DETAILS,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60 * 24)))
                
                .withCacheConfiguration(RedisValues.JOIN_DATE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(200)))
                .withCacheConfiguration(RedisValues.MY_INSTITUTIONS,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration(RedisValues.RECENT_PERFORMANCE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60 * 24)))
                
                .withCacheConfiguration(RedisValues.SUBJECT_NAMES,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(200)))
                .withCacheConfiguration(RedisValues.TOPIC_AND_DURATION,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration(RedisValues.TOPICS_AND_DURATIONS,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60 * 24)))
                
                .withCacheConfiguration(RedisValues.USER_CACHE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(200)))
                .withCacheConfiguration(RedisValues.WELCOME_MSG,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION, 
                		RedisCacheConfiguration.defaultCacheConfig()
                		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMillis(60 * 2)));
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

	@Bean
	ObjectMapper objectMapper() {
		
		return JsonMapper.builder()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
				.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
				.addModule(new JavaTimeModule())
				.findAndAddModules()
				.build();
	}
}

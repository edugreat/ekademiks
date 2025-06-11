package com.edugreat.akademiksresource.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
                		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
                
                .withCacheConfiguration(RedisValues.MY_GROUP, 
                		RedisCacheConfiguration.defaultCacheConfig()
                		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.IS_GROUPMEMBER, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.PREVIOUS_CHATS, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.ALL_GROUPS, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.MY_GROUP_IDs, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.MISCELLANEOUS, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        
        .withCacheConfiguration(RedisValues.PENDING_REQUEST, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
         .withCacheConfiguration(RedisValues.GROUP_CHAT_INFO, 
        		RedisCacheConfiguration.defaultCacheConfig()
        		.serializeValuesWith(valueSerializer).entryTtl(Duration.ofMinutes(60 * 2)))
        ;

        

  
  
	}
	
	@Bean(name = "genericRedisTemplate")
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
	    RedisTemplate<String, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);
	    
	    
	    GenericJackson2JsonRedisSerializer serializer = 
	        new GenericJackson2JsonRedisSerializer(objectMapper());
	    
	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(serializer);
	    template.setHashKeySerializer(new StringRedisSerializer());
	    template.setHashValueSerializer(serializer);
	    
	    return template;
	}
	
	
	@Bean
	ObjectMapper objectMapper() {
	    return JsonMapper.builder()
	        .addModule(new JavaTimeModule())
	        .addModule(new Jdk8Module())
	        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
	        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	        .build();
	}
	
	@Bean(name = "appUserRedisTemplate")
     RedisTemplate<String, AppUserDTO> studentRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, AppUserDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<AppUserDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), AppUserDTO.class);
       
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
	
	@Bean(destroyMethod = "shutdown")
	RedissonClient redissonClient() {
		
		Config config = new Config();
		config.useSingleServer()
		      .setAddress("redis://localhost:6379")
		      .setConnectionPoolSize(5)
		      .setConnectionMinimumIdleSize(5);
		
		return Redisson.create(config);
		
		
	}
	
	@Bean
	GenericJackson2JsonRedisSerializer jacksonRedisSerializer(ObjectMapper mapper) {
	    ObjectMapper redisMapper = mapper.copy();
	    // Enable polymorphic type handling safely
	    redisMapper.activateDefaultTyping(
	        redisMapper.getPolymorphicTypeValidator(),
	        ObjectMapper.DefaultTyping.NON_FINAL,
	        JsonTypeInfo.As.PROPERTY
	    );
	    return new GenericJackson2JsonRedisSerializer(redisMapper);
	}
}

package com.edugreat.akademiksresource.util;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Utility class that generates and encrypts new redis caching keys
@Component
public class CachingKeysUtil {
	
	@Autowired
	private  PasswordEncoder passwordEncoder;
	
	@Autowired
	private  RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private  CacheManager cacheManager;
	
	
     public  String generateCachingKey(String cacheName) {
		
		SecureRandom rand = new SecureRandom();
//		get the existing cache keys
		Cache cache = cacheManager.getCache(cacheName);
		
		if(cache != null) {
			
			
//			get all the caching keys from the redis stored
			Set<String> cacheKeys = getAllCacheKeys(cache.getName());
			
//			randomly generate and encrypt new key
			 String encryptedKey = passwordEncoder.encode(String.valueOf(rand.nextInt()));
			
//			safe approach to ensure key uniqueness
			while(cacheKeys.contains(encryptedKey)) encryptedKey = passwordEncoder.encode(String.valueOf(rand.nextInt()));
			
//			return uniquely generated and encrypted key
			return encryptedKey;
			
		}
		
		throw new RuntimeException("Something went wrong");
	}
	 private  Set<String> getAllCacheKeys(String cacheName) {
	        // Default pattern for keys in the cache(example: "cacheName::*1237594)
	        final String pattern = cacheName + "::*";

	        Set<String> keys = new HashSet<>();

	        redisTemplate.keys(pattern)
	        .parallelStream().forEach((key) -> {
	        	
	        	String cachingKey = key.substring(cacheName.length()+2);
	        
	        	keys.add(cachingKey);
	        	
	        	
	        });
	        
	        return keys;
	    }


}

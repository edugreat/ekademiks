package com.edugreat.akademiksresource.util;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
	 public  Set<String> getAllCacheKeys(String cacheName) {
	        // matches all keys stored via @Cacheable(eg cacheName::123) and those stored via cacheManager.put(cacheName123)
	        final Set<String> redisKeys = redisTemplate.keys("*");
	        
	        System.out.println(redisKeys.toString());
	        
	        
	        return redisKeys.stream()
	        		        .filter(key -> key.startsWith(cacheName+"::"))
	        		        .map(k -> k.substring(2+cacheName.length()))
	        		        .collect(Collectors.toSet());

	       
	        
	        
	    }


}

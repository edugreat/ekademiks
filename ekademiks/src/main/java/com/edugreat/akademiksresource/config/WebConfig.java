package com.edugreat.akademiksresource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	 @Bean
	     ThreadPoolTaskExecutor taskExecutor() {
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        executor.setCorePoolSize(10);  // Minimum threads available
	        executor.setMaxPoolSize(50);   // Maximum threads
	        executor.setQueueCapacity(100); // Queue for tasks
	        executor.setThreadNamePrefix("AsyncExecutor-");
	        executor.initialize();
	        return executor;
	    }
	
	 @Override
	    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	        configurer.setDefaultTimeout(5000);
	        configurer.setTaskExecutor(taskExecutor());
	    }
	 
	 


}

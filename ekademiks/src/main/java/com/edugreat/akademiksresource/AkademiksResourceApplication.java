package com.edugreat.akademiksresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AkademiksResourceApplication {

	
	public static void main(String[] args) {
		
		SpringApplication.run(AkademiksResourceApplication.class, args);
		
	
	}

}

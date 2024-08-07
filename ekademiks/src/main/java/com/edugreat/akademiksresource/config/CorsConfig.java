package com.edugreat.akademiksresource.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	
	private static final String[] ALLOWED_HEADERS = 
		{"Origin","Access-Control-Allow-Origin","Content-Type","Accept","Authorization","X-Request-With","Access-Control-Request-Method","Access-Control-Request-Headers"};
	private static final String[] EXPOSED_HEADERS = {"Origin","Content-Type","Accept","Authorization","Access-Control-Allow-Origin","Access-Control-Allow-Credentials"};
	
	private static final String[] ALLOWED_METHODS = {"GET","POST","PUT","DELETE","PATCH"};
	
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Bean
	CorsFilter corsFilter() {
		
		CorsConfiguration corsConfiguration = 
				new CorsConfiguration();
		corsConfiguration.setAllowedHeaders(Arrays.asList(ALLOWED_HEADERS));
		corsConfiguration.addAllowedOrigin("http://localhost:4200");
		corsConfiguration.setExposedHeaders(Arrays.asList(EXPOSED_HEADERS));
		corsConfiguration.setAllowedMethods(Arrays.asList(ALLOWED_METHODS));
		var urlBasedCorsConfiguration = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfiguration.registerCorsConfiguration("/**", corsConfiguration);
		
		return new CorsFilter(urlBasedCorsConfiguration);
		
		
	}

}

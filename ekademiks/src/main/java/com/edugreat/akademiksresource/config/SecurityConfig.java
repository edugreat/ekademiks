package com.edugreat.akademiksresource.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.edugreat.akademiksresource.auth.StudentUserDetailsService;
import com.edugreat.akademiksresource.filter.JwtRequestFilter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private final StudentUserDetailsService userDetailsService;
	private final JwtRequestFilter jwtFilter;
	
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

    http.cors(withDefaults()).csrf(csrf -> csrf.disable())
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests()
            .antMatchers("/**")

            .permitAll();
    
    return http.build();
	
}



}

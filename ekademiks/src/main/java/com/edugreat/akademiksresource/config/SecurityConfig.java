package com.edugreat.akademiksresource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.edugreat.akademiksresource.auth.AppUserDetailsService;
import com.edugreat.akademiksresource.filter.JwtAuthtFilter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private static final String[] PUBLIC_API = {"/sign-in/**","/sign-up/**","/logout/**","/take-test/**"};
	
	private final AppUserDetailsService userDetailsService;
	private final JwtAuthtFilter jwtFilter;
	
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

	return http.csrf(AbstractHttpConfigurer::disable)
.authorizeHttpRequests(request -> request.antMatchers(PUBLIC_API).permitAll()
		.antMatchers("/admin/**").hasAnyAuthority("Admin")
		.antMatchers("/student/**").hasAnyAuthority("Admin","Student")
		
		).sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authenticationProvider(authenticationProvider()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();

   
	
}

@Bean
private AuthenticationProvider authenticationProvider() {
	
	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	daoAuthenticationProvider.setUserDetailsService(userDetailsService);
	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	
	
	return daoAuthenticationProvider;
}

@Bean
private PasswordEncoder passwordEncoder() {
	
	return new BCryptPasswordEncoder();
}

@Bean
private AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	
	return authenticationConfiguration.getAuthenticationManager();
}



}

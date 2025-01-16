package com.edugreat.akademiksresource.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.edugreat.akademiksresource.auth.AppUserDetailsService;
import com.edugreat.akademiksresource.filter.AccountStatusFilter;
import com.edugreat.akademiksresource.filter.JwtAuthtFilter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableGlobalAuthentication
public class SecurityConfig {

	private static final String[] PUBLIC_API = { "/auth/**",  "/students/**", "/tests/**", "/notice/**",
			"/learning/**","/chats/messages" };

	private final AppUserDetailsService userDetailsService;
	private final JwtAuthtFilter jwtFilter;
	private final AccountStatusFilter accountStatusFilter;

	private static final String[] ALLOWED_HEADERS = { "Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
			"Authorization", "X-Request-With", "Access-Control-Request-Method", 
			"Access-Control-Request-Headers","adminid", "institutionId","studentId" };
	private static final String[] EXPOSED_HEADERS = { "Origin", "Content-Type", "Accept", "Authorization",
			"Access-Control-Allow-Origin", "Access-Control-Allow-Credentials","adminid","institutionId","studentId" };

	private static final String[] ALLOWED_METHODS = { "GET", "POST", "PUT", "DELETE", "PATCH" };


	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.addAllowedOrigin("http://localhost:4200");
			configuration.setAllowedMethods(List.of(ALLOWED_METHODS));
			configuration.setAllowedHeaders(List.of(ALLOWED_HEADERS));
			configuration.setExposedHeaders(List.of(EXPOSED_HEADERS));
			return configuration;
		})).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request -> request.requestMatchers(PUBLIC_API)
				.permitAll().requestMatchers("/admins/**").hasAnyAuthority("Admin").requestMatchers("/chats/**").hasAnyAuthority("Student")

		)

				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(accountStatusFilter, JwtAuthtFilter.class).build();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {

		return authenticationConfiguration.getAuthenticationManager();
	}

}

package com.edugreat.akademiksresource.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.edugreat.akademiksresource.filter.AccountStatusFilter;
import com.edugreat.akademiksresource.filter.JwtAuthtFilter;

@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_API = { 
        "/auth/**", "/students/**", "/tests/**", "/public/**",
        "/instructors/signup/**", "/learning/**", "/allow/**",
        "/swagger-ui/**", "/documentation.html", "/v3/api-docs/**",
        "/api-docs/**", "/webjars/**", "/swagger-resources/**" 
    };

    private static final String[] STUDENT_URL = {"/chats/**", "/assignments/details", "/assignments/resource", "/notice/**"};
    private static final String[] ADMINS_URL = {"/admins/**", "/assignments/**","/classrooms/**"};
    private static final String[] INSTRUCTORS_URL = {"/classrooms/**"};
    private static final String[] ANALYTICS_URL = {"/api/analytics/**"}; 

    private static final String[] ALLOWED_METHODS = { "GET", "POST", "PUT", "DELETE", "PATCH" };
    private static final String[] ALLOWED_HEADERS = {"Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept", "cachingKey",
    	      "Authorization", "X-Request-With", "Access-Control-Request-Method", 
    	      "Access-Control-Request-Headers","adminid","studentTestIds", "institutionId","studentId", "Connection","Cache-Control","user-group-chat-ids" };
    private static final String[] EXPOSED_HEADERS = {  "Origin", "Content-Type", "Accept", "Authorization",
      "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials","adminid","institutionId",
      "studentId", "type","detailsId", "Connection","Cache-Control"  };

    // Development/Staging configuration
    @Bean
    @Profile("!prod")
    SecurityFilterChain devFilterChain(
        HttpSecurity http,
        JwtAuthtFilter jwtFilter,
        AccountStatusFilter accountStatusFilter
    ) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.addAllowedOrigin("*");
                config.setAllowedMethods(List.of(ALLOWED_METHODS));
                config.setAllowedHeaders(List.of(ALLOWED_HEADERS));
                config.setExposedHeaders(List.of(EXPOSED_HEADERS));
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_API).permitAll()
                .requestMatchers(STUDENT_URL).hasAnyAuthority("Student")
                .requestMatchers(ADMINS_URL).hasAnyAuthority("Admin", "Instructor")
                .requestMatchers(INSTRUCTORS_URL).hasAnyAuthority("Instructor","Admin")
                .anyRequest().hasAnyAuthority("SuperAdmin") 
            )
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(accountStatusFilter, JwtAuthtFilter.class)
            .build();
    }

    @Bean
    @Profile("prod")
    SecurityFilterChain prodFilterChain(
        HttpSecurity http,
        JwtAuthtFilter jwtFilter,
        AccountStatusFilter accountStatusFilter
    ) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("https://your-production-domain.com")); // Strict CORS
                config.setAllowedMethods(List.of(ALLOWED_METHODS));
                config.setAllowedHeaders(List.of(ALLOWED_HEADERS));
                config.setExposedHeaders(List.of(EXPOSED_HEADERS));
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_API).permitAll()
                .requestMatchers(STUDENT_URL).hasAnyAuthority("Student")
                .requestMatchers(ADMINS_URL).hasAnyAuthority("Admin", "Instructor")
                .requestMatchers(INSTRUCTORS_URL).hasAnyAuthority("Instructor","Admin")
                .requestMatchers(ANALYTICS_URL).hasAnyAuthority("SuperAdmin") // Analytics only
                .anyRequest().denyAll()
            )
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(accountStatusFilter, JwtAuthtFilter.class)
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, ex) -> {
                    response.sendError(403, "Production access restricted");
                })
            )
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    
    
    
}
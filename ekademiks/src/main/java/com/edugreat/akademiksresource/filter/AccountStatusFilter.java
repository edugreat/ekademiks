package com.edugreat.akademiksresource.filter;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.edugreat.akademiksresource.auth.AppUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

// Filter that checks the account status of users
@Component
@AllArgsConstructor
public class AccountStatusFilter extends OncePerRequestFilter {

	private final AppUserDetailsService userDetailsService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
//		Check if the user is already authenticated
		if(SecurityContextHolder.getContext().getAuthentication() != null) {
			
//			get the username
			final String username = SecurityContextHolder.getContext().getAuthentication().getName();
			
			UserDetails user = userDetailsService.loadUserByUsername(username);
			
//			check if account is disabled
			if(! user.isEnabled()) {
				
				throw new DisabledException("Account is Disabled!");
				
				
			}
			
		}
		
//		proceed with the request
		filterChain.doFilter(request, response);

	}

}

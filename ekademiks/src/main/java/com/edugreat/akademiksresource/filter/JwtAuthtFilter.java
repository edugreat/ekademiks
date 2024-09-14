package com.edugreat.akademiksresource.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.edugreat.akademiksresource.auth.AppUserDetailsService;
import com.edugreat.akademiksresource.auth.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtAuthtFilter extends OncePerRequestFilter {

	private final AppUserDetailsService userDetailsService;
	private HandlerExceptionResolver handlerExceptionResolver;

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		;
		final String authHeader = request.getHeader("Authorization");
		final String jwtToken;
		final String userEmail;
		if (authHeader == null || authHeader.isBlank()) {

			filterChain.doFilter(request, response);
			return;
		}
		
//		Allow pass without further jwt checks if the fresh token request is being sent
		String path = request.getRequestURI();
		if(path.contains("/auth/refresh-token")) {
			
			;
			
			filterChain.doFilter(request, response);
			
			return;
		}

		try {

			jwtToken = authHeader.substring(7);
			userEmail = jwtUtil.extractUsername(jwtToken);
			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

				if (jwtUtil.isTokenValid(jwtToken, userDetails)) {

//					Extract user's roles from the token
					List<String> roles = jwtUtil.extractRoles(jwtToken);
					List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

					// SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
							authorities);

					token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//					securityContext.setAuthentication(token);
//						SecurityContextHolder.setContext(securityContext);
					SecurityContextHolder.getContext().setAuthentication(token);
//						
				}

			}

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			
			handlerExceptionResolver.resolveException(request, response, null, e);
			
		}
	}
}

package com.edugreat.akademiksresource.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final long EXPIRATION_TIME = 43200000; // 12 hours converted to milliseconds
	

	public JwtUtil() {

	}

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	public String generateToken(UserDetails userDetails, String selectedRole) {

		
		List<String> authorities = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(String::toUpperCase)
				.toList();
		
		
		if(!authorities.contains(selectedRole.toUpperCase())) {
			
			throw new IllegalArgumentException("You cannot login as "+selectedRole);
		}

				
//		Include the user's roles to the generated token
		Map<String, Object> claims = new HashMap<>();
		//claims.put("roles", userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).toList());
        claims.put("roles", List.of(selectedRole));
		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(getSignedKey()).compact();
	}

	public String extractUsername(String token) {

		return extractClaims(token, Claims::getSubject);
	}
	
	
	private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {

		return claimsFunction
				.apply(Jwts.parser().verifyWith(getSignedKey()).build().parseSignedClaims(token).getPayload());
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {

		final String username = extractUsername(token);

		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {

		return extractClaims(token, Claims::getExpiration).before(new Date());
	}

//	 Extract user's roles from the jwt token
	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {

		return extractClaims(token, claims -> claims.get("roles", List.class));
	}

	private SecretKey getSignedKey() {
		byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

		return Keys.hmacShaKeyFor(keyBytes);
	}
	
//	Methods handles creation of refresh token
	public String createRefreshToken(UserDetails userDetails, String selectedRole) {
		
		Date now = new Date();
		Date expiry = new Date(now.getTime() + 86400000);//24 hours as refresh token should live longer than access tokens
		

//		Include the user's roles to the generated token
		Map<String, Object> claims = new HashMap<>();
	//	claims.put("roles", userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).toList());
		claims.put("roles", selectedRole);
		
		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(now)
				.expiration(expiry).signWith(getSignedKey()).compact();

	}

}

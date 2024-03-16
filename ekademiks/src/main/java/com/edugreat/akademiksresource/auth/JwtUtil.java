package com.edugreat.akademiksresource.auth;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {
	
	private final long EXPIRATION_TIME = 3600000; //1 hour converted to milliseconds
	
	public JwtUtil() {
		
		
	}
	
	@Value("${jwt.secret}")
	private  String SECRET_KEY;
	
	public String generateToken(UserDetails userDetails) {
		
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignedKey())
				.compact();
	}
	
	public String extractUsername(String token) {
		
		return extractClaims(token, Claims::getSubject);
	}
	
	 
	 private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
		
		return claimsFunction.apply(Jwts.parser().verifyWith(getSignedKey()).build().parseSignedClaims(token).getPayload());
	}

	 public boolean isTokenValid(String token, UserDetails userDetails) {
		 
		 final String username = extractUsername(token);
		 
		 return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	 }
	 
	 private boolean isTokenExpired(String token) {
		 
		 return extractClaims(token, Claims::getExpiration).before(new Date());
	 }
	 
	private SecretKey getSignedKey() {
		 byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
		 
		 return Keys.hmacShaKeyFor(keyBytes);
	 }
	 
	 
	}
	



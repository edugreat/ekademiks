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
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtUtilService {
	
	@Value("${jwt.secret}")
	private  String SECRET_KEY;
	
	
	
	 public String createToken(Map<String, Object> claims, String subject) {
		  Calendar calendar = Calendar.getInstance();
		  calendar.add(Calendar.MINUTE, 45);
		 return
	        Jwts.
	        builder()
	        .claims(claims)
	        .subject(subject)
	        .issuedAt(new Date())
	        .expiration(calendar.getTime())
	        .signWith(getSignedKey())
	        .compact();
	        
	        		
	    }
	 
	 public Date extractExpiration(String token) {
		 
		 return extractClaim(token, Claims::getExpiration);
	 }
	 
	 public Boolean isTokenExpired(String token) {
		 
		 return extractExpiration(token).before(new Date());
	 }
	 
	 public String extractUsername(String token) {
		 
		 return extractClaim(token, Claims::getSubject);
	 }
	 
	 private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		 
		 
		final Claims claims = extractAllClaims(token);
		
		return claimResolver.apply(claims);
				
				 
	 }
	 
	 private Claims extractAllClaims(String token) {
		 
		 return
				 Jwts.
				 parser().
				 verifyWith(getSignedKey())
				 .build()
				 .parseSignedClaims(token)
				 .getPayload();
				 
				 
	 }
	 
	 private SecretKey getSignedKey() {
		 byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
		 
		 return Keys.hmacShaKeyFor(keyBytes);
	 }
	 
	 public Boolean validateToken(String token, UserDetails userDetails) {
		 
		 final String email = extractUsername(token);
		 
		 return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
	 }

	public String generateToken(UserDetails userDetails) {
		
		Map<String, Object> claims = new HashMap<>();
		
		return createToken(claims, userDetails.getUsername());
	}
	}
	



package com.edugreat.akademiksresource.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.StudentInterface;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Student;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentUserDetailsService implements UserDetailsService {

	private final StudentInterface service;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		return new UserPrincipal(service.searchByEmail(email));
	}
	
	public Student authenticate(String email, String password) throws NoSuchAlgorithmException {
		
		var student = service.searchByEmail(email);
		if(student == null) {
			throw new AcademicException("unauthorized", Exceptions.BAD_REQUEST.name());
		}
		var verified = verifyPassword(password, student.getStoredHash(), student.getStoredSalt());
		
		if(! verified) throw new BadCredentialsException("Unauthorized");
		
		return student;
	}

	private Boolean verifyPassword(String password, byte[] storedHash, byte[] storedSalt)throws NoSuchAlgorithmException {
		
		if(password.isBlank() || password.isEmpty()) throw new BadCredentialsException("Unauthoried!");
		if(storedHash.length != 64  || storedSalt.length != 128) throw new BadCredentialsException("Unauthoried!");
		var md = MessageDigest.getInstance("SHA-512");
		md.update(storedSalt);
		
		var computedHash= md.digest(password.getBytes(StandardCharsets.UTF_8));
		for(int i = 0; i < computedHash.length; i++) {
			if(computedHash[i] != storedHash[i]) return false;
		}
		
		return MessageDigest.isEqual(computedHash,  storedHash);
		
		
		
	}

}

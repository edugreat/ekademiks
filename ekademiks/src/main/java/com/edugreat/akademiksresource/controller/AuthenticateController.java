package com.edugreat.akademiksresource.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.auth.AuthenticationResponse;
import com.edugreat.akademiksresource.auth.JwtUtilService;
import com.edugreat.akademiksresource.auth.StudentUserDetailsService;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Student;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthenticateController {

	private final JwtUtilService jwtUtilService;
	private final StudentUserDetailsService userDetailsService;
	
	@PostMapping("/authenticate")
	public ResponseEntity<Object> authenticate(@RequestBody @Valid AuthenticationRequest req) throws Exception{
	
		Student student;
		try {
			student = userDetailsService.authenticate(req.getEmail(), req.getPassword());
		} catch (Exception e) {
			throw new AcademicException("Incorrect email and or password", Exceptions.BAD_REQUEST.name());
		}
		
		var userDetails = userDetailsService.loadUserByUsername(student.getEmail());
		var jwt = jwtUtilService.generateToken(userDetails);
		
		return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.CREATED);
	}
	
	
}

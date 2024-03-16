package com.edugreat.akademiksresource.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticateController {
 private final AppAuthInterface appInterface;
	
	@PostMapping("/sign-up")
	public ResponseEntity<Object> signUp(@RequestBody @Valid AppUserDTO userDTO) throws Exception{
	
		return ResponseEntity.ok(appInterface.signUp(userDTO));
	
		
		
	}
	
	@PostMapping("/sign-in")
	public ResponseEntity<Object> signIn(@RequestBody @Valid AppUserDTO userDTO){
		
		return ResponseEntity.ok(appInterface.signIn(userDTO));
	}
	
}

package com.edugreat.akademiksresource.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticateController {
 private final AppAuthInterface appInterface;
	
	@PostMapping("/sign-up")
	@JsonView(UserView.class)
	public int signUp(@RequestBody @Valid AppUserDTO userDTO) throws Exception{
	
		
		return appInterface.signUp(userDTO);
	
		
		
	}
	
	@PostMapping("/sign-in")
	@JsonView(UserView.SigninView.class)
	public ResponseEntity<AppUserDTO> signIn(@RequestBody @Valid AuthenticationRequest request,
			@RequestParam("role") String role){
		
		return ResponseEntity.ok(appInterface.signIn(request, role));
	}
	
}

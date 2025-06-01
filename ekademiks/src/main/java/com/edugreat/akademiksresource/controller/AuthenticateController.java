package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentications", description = "Endpoints for managing user authentications")
public class AuthenticateController {
	private final AppAuthInterface appInterface;

	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.OK)
	@JsonView(UserView.class)
	@Operation(summary = "Signup", description = "Allows user to sign up")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description    = "Successfully signed up"),
			@ApiResponse(responseCode = "400", description = "Duplicate account attempt")
	})
	@SecurityRequirements()
	public int signUp(@RequestBody @Valid AppUserDTO userDTO) throws Exception {

		return appInterface.signUp(userDTO);

	}

	@PostMapping("/sign-in")
	@JsonView(UserView.SigninView.class)
	@Operation(summary = "Signin", description = "Allows user to signin")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description    = "Successfully signed in"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@SecurityRequirements()
	public ResponseEntity<AppUserDTO> signIn(@RequestBody @Valid AuthenticationRequest request,
			@RequestParam String role) {

		return ResponseEntity.ok(appInterface.signIn(request, role));
	}

	// Controller endpoint for requesting new access token upon token expiration
	@PostMapping("/refresh-token")
	@JsonView(UserView.SigninView.class)
	public ResponseEntity<AppUserDTO> refreshToken(@RequestBody Map<String, String> request,
			HttpServletResponse respone) throws IOException {

		;

		final String token = request.get("refreshToken");
		;

		return ResponseEntity.ok(appInterface.generateNewToken(token, respone));

	}

	
	@GetMapping("/cached/user")
	@Operation(summary = "Cached user", description = "Get cached user object")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "request successful"),
			@ApiResponse(responseCode = "400", description = "Request not successful")
	})
	public ResponseEntity<Object> cachedUser(@RequestParam ("cache")String key){
		
	
		
		try {
			
			return  ResponseEntity.ok(appInterface.getCachedUser(key));
		} catch (Exception e) {
			
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}

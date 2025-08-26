package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.edugreat.akademiksresource.registrations.AdminRegistrationRequest;
import com.edugreat.akademiksresource.registrations.StudentRegistrationData;
import com.edugreat.akademiksresource.util.ApiResponseObject;
import com.edugreat.akademiksresource.util.ValidatorService;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Authentications", description = "Endpoints for managing user authentications")

public class AuthenticateController {
	private final AppAuthInterface appInterface;
	
	private final Validator validator;
	private final ValidatorService validatorService;

	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.OK)
	@JsonView(UserView.class)
	@Operation(summary = "Signup", description = "Allows user to sign up")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description    = "Successfully signed up"),
			@ApiResponse(responseCode = "400", description = "Duplicate account attempt")
	})
	@SecurityRequirements()
	public ResponseEntity<Object> signUp(@RequestBody  StudentRegistrationData registrationData) {

		try {
			
			Set<ConstraintViolation<StudentRegistrationData>> violations = validator.validate(registrationData);
			
			if(!violations.isEmpty() ) {
				
				List<String> errors = violations.stream().map(v -> v.getMessage()).toList();
				
				List<String> phoneNumberUnrelatedErrors = errors.stream().filter(e -> !e.contains("Phone number")).toList();
				
				if(!phoneNumberUnrelatedErrors.isEmpty()) {
					return new ResponseEntity<Object>(phoneNumberUnrelatedErrors, HttpStatus.BAD_REQUEST);
				}
				
				if(errors.size() > 1) {
					return new ResponseEntity<Object>("Please check your provided mobile number", HttpStatus.BAD_REQUEST);
				}
				
				if(errors.size() == 1 && !(errors.get(0).equals("Phone number is missing"))) {
					return new ResponseEntity<Object>(errors.get(0), HttpStatus.BAD_REQUEST);
				}
			}
			

			
			return ResponseEntity.ok(appInterface.studentSignup(registrationData));
		} catch (Exception e) {
		
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

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

		try {
			return ResponseEntity.ok(appInterface.signIn(request, role));
		} catch (Exception e) {
			System.out.println(e);
			
			return new ResponseEntity<AppUserDTO>(HttpStatus.NOT_FOUND);
		}
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
	
	@PostMapping("/admins/reg")
	public ResponseEntity<ApiResponseObject<String>> registerSchoolAdmin(@RequestBody AdminRegistrationRequest request) {
	
		try {
			List<String> violations = validatorService.validateObject(request);
			if(!violations.isEmpty()) {
				
				return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, String.join(", ", violations), false));
			}
			
			appInterface.registerSchoolAdmin(request);
			
			return ResponseEntity.ok(new ApiResponseObject<>("Congrats, "+request.firstName()+" "+request.lastName(), null, true));
			
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest().body(new ApiResponseObject<>(e.getLocalizedMessage(), null, false));
		}
		
		
	}
	

}

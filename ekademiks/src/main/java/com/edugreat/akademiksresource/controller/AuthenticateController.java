package com.edugreat.akademiksresource.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.edugreat.akademiksresource.registrations.InstructorRegistrationRequest;
import com.edugreat.akademiksresource.registrations.StudentRegistrationData;
import com.edugreat.akademiksresource.util.ApiResponseObject;
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
	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.OK)
	@JsonView(UserView.class)
	@Operation(summary = "Signup", description = "Allows user to sign up")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description    = "Successfully signed up"),
			@ApiResponse(responseCode = "400", description = "Duplicate account attempt")
	})
	@SecurityRequirements()
	public ResponseEntity<ApiResponseObject<String>> signUp(@RequestBody  StudentRegistrationData registrationData) {

		try {
			
			Set<ConstraintViolation<StudentRegistrationData>> violations = validator.validate(registrationData);
			if(!violations.isEmpty()) {
				
				List<String> errors = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
				var errorReport =	processViolations(errors);
				if(errorReport != null) {
					
					return errorReport;
				}
			}
				

			var created = appInterface.studentSignup(registrationData);
			return ResponseEntity.ok(new ApiResponseObject<>(String.valueOf(created), null, true));
		} catch (Exception e) {
		
			
			return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, e.getLocalizedMessage(), false));

		}
	}

	private ResponseEntity<ApiResponseObject<String>> processViolations(List<String> violations) {
		if(!violations.isEmpty() ) {
			
			
			
			List<String> phoneNumberUnrelatedErrors = violations.stream().filter(e -> !e.contains("Phone number")).toList();
			
			if(!phoneNumberUnrelatedErrors.isEmpty()) {
				
				return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, String.join(", ", phoneNumberUnrelatedErrors), false));
			
				
			}
			
			if(violations.size() > 1) {
				return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, "Please check your provided mobile number", false));
				
			}
			
			if(violations.size() == 1 && !(violations.get(0).equals("Phone number is missing"))) {
				return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, "Phone number is missing", false));
				
			}
		}
		
		
		return null;
	}

	@PostMapping("/sign-in")
	@JsonView(UserView.SigninView.class)
	@Operation(summary = "Signin", description = "Allows user to signin")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description    = "Successfully signed in"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@SecurityRequirements()
	public ResponseEntity<ApiResponseObject<AppUserDTO>> signIn(@RequestBody @Valid AuthenticationRequest request,
			@RequestParam String role) {
		
		System.out.println("controller");

		try {
			final  AppUserDTO user = appInterface.signIn(request, role);
			
			return ResponseEntity.ok().body(new ApiResponseObject<>(user, null, true));
			
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			
			return ResponseEntity.badRequest().body(new ApiResponseObject<>(null, e.getLocalizedMessage(), false));
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
		System.out.println("controller");
	
		try {
			Set<ConstraintViolation<AdminRegistrationRequest>> violations = validator.validate(request);
			List<String> errors = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
			if(!errors.isEmpty()) {
				var errorReport = processViolations(errors);
				if(errorReport != null) {
					
					return errorReport;
				}
			}
			
			
			
			appInterface.registerSchoolAdmin(request);
			
			return ResponseEntity.ok(new ApiResponseObject<>("Congrats, "+request.firstName()+" "+request.lastName(), null, true));
			
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest().body(new ApiResponseObject<>(e.getLocalizedMessage(), null, false));
		}
		
		
	}
	
	@PostMapping("/instructors/signup")
	public ResponseEntity<ApiResponseObject<String>> registerAsInstructor(@RequestBody InstructorRegistrationRequest request) {
		
		try {
			Set<ConstraintViolation<InstructorRegistrationRequest>> violations  = validator.validate(request);
			List<String> errors = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
			if(!errors.isEmpty()) {
				var errorReport = processViolations(errors);
				if(errorReport != null) {
					
					return errorReport;
				}
			}
			
			appInterface.instructorSignup(request);
			
			return ResponseEntity.ok().body(new ApiResponseObject<>("Congrats "+request.firstName()+" "+request.lastName(), null, true));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.badRequest().body(new ApiResponseObject<>(e.getLocalizedMessage(), null, false));
		}
		
		
	}
	
	

}

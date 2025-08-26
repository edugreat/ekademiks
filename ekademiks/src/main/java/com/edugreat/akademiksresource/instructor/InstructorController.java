package com.edugreat.akademiksresource.instructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.registrations.InstructorRegistrationRequest;
import com.edugreat.akademiksresource.util.ValidatorService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/instructors")
@RequiredArgsConstructor
public class InstructorController {
	
	private final AppAuthInterface userInterface;
	private final ValidatorService validatorService;
	@PostMapping("/signup")
	public ResponseEntity<Object> signup (@RequestBody InstructorRegistrationRequest request) {
		
		System.out.println("controller called");
		
		try {
			
			List<String> violations = validatorService.validateObject(request);
			
			if(!violations.isEmpty()) {
				
				
				
				return new ResponseEntity<>(String.join(", ", violations), HttpStatus.BAD_REQUEST);
			}
			
			userInterface.instructorSignup(request);
			
			return new ResponseEntity<>(HttpStatus.OK);
			
			
			
		} catch (Exception e) {
			
			System.out.println(e);
			
			return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
		
		
		
		
	}
	
	
	
}

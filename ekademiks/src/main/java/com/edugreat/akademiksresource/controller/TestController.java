package com.edugreat.akademiksresource.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.util.AttemptUtil;

@RestController
@RequestMapping("test")
public class TestController {
	
	private TestInterface service;
	
	public TestController(TestInterface testInterface) {
		
		this.service = testInterface;
	}
	
	@GetMapping("{id}")
	//get mapping that serves questions for the given test id
	public ResponseEntity<Object> takeTest(@PathVariable("id") Integer testId){
		
		TestWrapper questions = service.takeTest(testId);
		return new ResponseEntity<>(questions, HttpStatus.OK);
		
		
	}
	
	//receives the test attempt for submission
		@PostMapping("/submit")
		public ResponseEntity<String> submitTest(@Valid @RequestBody AttemptUtil attempt) {
			
			
			try {
				
				service.submitTest(attempt);
				
			} catch (ConstraintViolationException e) {
				
				throw new AcademicException("Invalid input detected", Exceptions.ILLEGAL_DATA_FIELD.name());
			}
			
			return ResponseEntity.ok("Submitted");
		}
	
	
}

package com.edugreat.akademiksresource.controller;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.service.StudentService;
import com.edugreat.akademiksresource.util.AttemptUtil;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("student")
@AllArgsConstructor
public class StudentController {
	
	private final StudentService service;
	
	

	@GetMapping
	public ResponseEntity<Object> getScore(@RequestParam("id") String stId, 
			@PathVariable("testId") String tId) {
		
		//check if the input path variables match the regular expression for only non white spaced digits
		Pattern pattern = Pattern.compile("^\\d+$");
		
		Matcher m1 = pattern.matcher(stId);
		Matcher m2 = pattern.matcher(tId);
		if(!(m1.matches() && m2.matches())) {
			
			throw new AcademicException("Invalid input '"+stId+"' or '"+tId+"'", Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
		
		Integer studentId = Integer.parseInt(stId);
		Integer testId = Integer.parseInt(tId);
		
		ResponseEntity<Object> response = service.getTestScore(studentId, testId);
		
		return response;
		
	}
	
	@GetMapping
	public Collection<Question> takeTest(@RequestParam("test_id") String tId){
		
		//CHECK TO CONFIRM THE ARGUMENT IS A VALID INTEGER
		Pattern p = Pattern.compile("^\\d+$");
		if(!p.matcher(tId).matches()) {
			throw new AcademicException("Invalid input "+tId, Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
		Integer testId = Integer.parseInt(tId);
		
		Collection<Question> questions = null;
		
		questions = service.takeTest(testId);
		
		return questions;
		
	}
	
	//receives the test attempt for submission
	@PostMapping("/submit")
	public void submitTest(@Valid @RequestBody AttemptUtil attempt) {
		
		
		try {
			
			service.submitTest(attempt);
			
		} catch (ConstraintViolationException e) {
			
			throw new AcademicException("Invalid input detected", Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
	}
	
	
	
}

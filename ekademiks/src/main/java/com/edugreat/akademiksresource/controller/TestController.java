package com.edugreat.akademiksresource.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.projection.TestWrapper;

@RestController
@RequestMapping("acad/v1")
@Validated
public class TestController {
	
	private TestInterface testInterface;
	
	public TestController(TestInterface testInterface) {
		
		this.testInterface = testInterface;
	}
	
	@GetMapping("/test/{id}")
	//get mapping that serves questions for the given test id
	public ResponseEntity<Object> takeTest(@PathVariable("id") Integer testId){
		
		TestWrapper questions = testInterface.takeTest(testId);
		return new ResponseEntity<>(questions, HttpStatus.OK);
		
		
	}
	
	//set a new academic test
	@PostMapping("/tests")
	public ResponseEntity<Object> setTest(@RequestBody @Valid TestDTO testDTO) {
		
		testInterface.setTest(testDTO);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/subject")
	public ResponseEntity<Object> setSubject(@RequestBody @Valid SubjectDTO dto){
		
		testInterface.setSubject(dto);
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}

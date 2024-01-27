package com.edugreat.akademiksresource.controller;

import java.util.Collection;

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
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.util.TestUtil;

@RestController
@RequestMapping("acad/tests")
public class TestController {
	
	private TestInterface testInterface;
	
	public TestController(TestInterface testInterface) {
		
		this.testInterface = testInterface;
	}
	
	@GetMapping("/{id}")
	//get mapping that serves question for the given test id
	public ResponseEntity<Object> getQuestion(@PathVariable("id") Integer testId){
		
		Collection<Question> questions = testInterface.getQuestions(testId);
		return new ResponseEntity<>(questions, HttpStatus.OK);
		
		
	}
	
	//set a new academic test
	@PostMapping
	public ResponseEntity<Object> setTest(@RequestBody @Valid TestUtil testUtil) {
		
		testInterface.setTest(testUtil);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

}

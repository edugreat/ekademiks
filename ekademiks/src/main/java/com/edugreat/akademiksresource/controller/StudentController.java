package com.edugreat.akademiksresource.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.StudentService;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.projection.ScoresWrapper;
import com.edugreat.akademiksresource.util.AttemptUtil;

@RestController
@RequestMapping("acad/tests/student")
public class StudentController {
	
	private StudentService studentService;
	
	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping("/{studentId}/{testId}")
	public ScoresWrapper getScore(@PathVariable Integer studentId, @PathVariable Integer testId) {
		//TODO: CHECK IF THE VARIABLES STUDENTID AND TESTID ARE LEGAL ARGUMENTS BEFORE PASSING THEM TO METHOD CALLS
		//THROW EXCEPTIONS IF APPROPRIATE
		
		ScoresWrapper scores = null;
		
		List<ScoreAndDate> scd = studentService.getTestScore(studentId, testId);
		scores = new ScoresWrapper(scd);
		
		return scores;
		
	}
	
	@GetMapping("/{testId}")
	public Collection<Question> takeTest(@PathVariable Integer testId){
		
		//TODO: CHECK TO CONFIRM THE ARGUMENT IS A VALID INTEGER
		
		Collection<Question> questions = null;
		
		questions = studentService.takeTest(testId);
		
		return questions;
		
	}
	
	//receives the test attempt for submission
	@PostMapping("/submit")
	public void submitTest(@Valid @RequestBody AttemptUtil attempt) {
		
		//TODO: will be executed inside try..catch block when exception handling has been implemented
		//this method will possibly throw IllegalArgumentException if invalid option letter is contained in the
		//list of selected options
		studentService.submitTest(attempt);
		
	}
}

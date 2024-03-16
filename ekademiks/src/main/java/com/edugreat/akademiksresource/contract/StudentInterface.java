package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import org.springframework.http.ResponseEntity;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.util.AttemptUtil;

public interface StudentInterface {
	

	
	//method that serves a collection of questions for the given testId
	public Collection<Question> takeTest(int testId);
	
	//method that submits student's attempt in a test
	public void submitTest(AttemptUtil attempt);
	
	////method that returns the List of student's score in a test. A student could've taken
	//this particular test more than once, so returning a list of their scores and associated time is appropriate a
	public ResponseEntity<Object> getTestScore(int studentId, int testId);
	
}

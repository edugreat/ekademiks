package com.edugreat.akademiksresource.contract;

import java.util.List;

import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.projection.TestWrapper;

//declare contracts that would be implemented
public interface TestInterface {
	
	//sets new test
	public void setTest(TestDTO testDTO);
	
	//Return a wrapper class which contains a collection of questions associated with the given testId
	public TestWrapper takeTest(Integer testId);
	
	
	//for the given testId and studentId, retrieve the list of scores
	//the student made alongside the date the score was made.
	//since students are allowed to re-take a tests, it's 
	//appropriate to return list of scores made
	public List<ScoreAndDate> getScore(int studentId, int testId);


}

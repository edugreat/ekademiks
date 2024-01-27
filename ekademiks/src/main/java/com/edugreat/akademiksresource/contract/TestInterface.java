package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.util.TestUtil;

//declare contracts that would be implemented
public interface TestInterface {
	
	//sets new test
	public void setTest(TestUtil testUtil);
	
	//get a collection of questions for a given test identifier
	public Collection<Question> getQuestions(Integer testId);
	
	
	//for the given testId and studentId, retrieve the list of scores
	//the student made alongside the date the score was made.
	//since students are allowed to re-take a tests, it's 
	//appropriate to return list of scores made
	public List<ScoreAndDate> getScore(int studentId, int testId);
	

}

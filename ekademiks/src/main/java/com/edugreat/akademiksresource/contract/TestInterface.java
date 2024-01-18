package com.edugreat.akademiksresource.contract;

import java.util.List;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;

//declare contracts that would be implemented
public interface TestInterface {
	
	//sets new test
	public void setTest(Test test);
	
	//get a list of questions for a given test identifier
	public List<Question> getQuestions(Integer testId);
	

}

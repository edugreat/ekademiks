package com.edugreat.akademiksresource.contract;

import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.util.AttemptUtil;

//declare contracts that would be implemented
public interface TestInterface {
	
	
	//Return a wrapper class which contains a collection of questions associated with the given testId
	public TestWrapper takeTest(Integer testId);
	
	
	//method that submits student's attempt in a test
		public void submitTest(AttemptUtil attempt);
		
	

}

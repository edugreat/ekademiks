package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.projection.TopicAndDuration;

import com.edugreat.akademiksresource.util.AttemptUtil;

//declare contracts that would be implemented
public interface TestInterface {
	
	
	//Return a wrapper class which contains a collection of questions associated with the given testId
	public TestWrapper takeTest(Integer testId);
	
	
	//method that submits student's attempt in a test
		public String submitTest(AttemptUtil attempt);
		
		//method that retrieves all the test subject for the given academic level
		public List<String> testSubjectFor(String level);
		
		//method that retrieves all the topics and their durations for a subject and category
		public List<TopicAndDuration> testTopics(String subject, String category);
	
		/*
		 * Returns  questions wrapper using the test topic and category
		 *The implementation attaches the instructions for the test to the wrapper
		 */
		
		public TestWrapper takeTest(String topic, String category);
		

		public Collection<String> getWelcomeMessages();


}

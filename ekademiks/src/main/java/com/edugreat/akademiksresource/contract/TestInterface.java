package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.projection.TopicAndDuration;
import com.edugreat.akademiksresource.util.AttemptUtil;
import com.edugreat.akademiksresource.util.PerformanceObj;

//declare contracts that would be implemented
public interface TestInterface {

	// Return a wrapper class which contains a collection of questions associated
	// with the given testId
	public TestWrapper takeTest(Integer testId);

	// method that submits student's attempt in a test
	public String submitTest(AttemptUtil attempt);

	// method that retrieves all the test subject for the given academic level
	public List<String> testSubjectFor(String level);

	// method that retrieves all the topics and their durations for a subject and
	// category
	public List<TopicAndDuration> testTopicsAndDurations(String subject, String category, Integer studentId);

	/*
	 * Returns questions wrapper using the test topic and category The
	 * implementation attaches the instructions for the test to the wrapper
	 */

	public TestWrapper takeTest(String topic, String category);

	public Collection<String> getWelcomeMessages();

//		An implementation of this method should return a Map whose key is a subject name and value is the category name the subject belongs to
	public Map<String, String> subjectAndCategory(int testId);

//		get the topic and duration for a given test id
	public TopicAndDuration testTopicAndDuration(Integer testId);
	
//	returns student's recent performance by querying the redis cache against the supplied caching key.
	public PerformanceObj getRecentPerformanceFromCache(String cachingKey);
	
//	saves students recent performance to the redis cache and returns key for accessing it in in the future(keys are creating for guest users. For logged  users, keys get created on login)
	public String saveRecentPerformanceToCache(PerformanceObj performance, String cachingKey);

}

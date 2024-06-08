package com.edugreat.akademiksresource.projection;

//projection for fetching the test topics and durations
public interface TopicAndDuration {

	//get the topic for the test
	String getTestName();
	
	//get the duration for the test
	Long getDuration();
}

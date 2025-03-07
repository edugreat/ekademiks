package com.edugreat.akademiksresource.util;

import java.util.List;

//  an object of student's recent performance which is cached for easy retrieval of student's
// recent performance.

public record PerformanceObj(
		String subjectName, 
		String testTopic, 
		List<String> selectedOptions, 
		List<String> correctOptions) {

}

package com.edugreat.akademiksresource.util;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/*
 * Utility class that manages the properties of a test object.
 * Field instances of the utility class would be mapped to the  instances of Test object
 */
public class TestUtil {
	
	@NotNull(message = "property test name missing")
	@Size(min = 4, message = "property test name must not be less than 4 characters")
	@Pattern(regexp = "\\b\\w+\\b\\s*", message = "test name not supported")
	private String testName;
	
	@Valid
	@NotEmpty(message = "property questions, missing or empty")
	private Set<QuestionUtil> questions = new HashSet<>();
	
	
	@Min(value = 30, message = "duration must be greater than or equal to 30 mins")
	private long duration;
	
	
	@NotNull(message = "property subject name missing")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "subject name no acceptable")
	//subject that this test is to be associated to; retrievable from the database
	private String subjectName;

	public String getTestName() {
		return testName;
	}

	
	public long getDuration() {
		return duration;
	}

	

	public String getSubjectName() {
		return subjectName;
	}


	public Set<QuestionUtil> getQuestions() {
		
		return questions;
	}


}

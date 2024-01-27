package com.edugreat.akademiksresource.util;

import java.util.List;

import javax.validation.constraints.NotNull;

/*
 * This class models attempts a student makes in an academic test exercise
 */
public class AttemptUtil {
	
	//identifier for test for which attempt is made
	@NotNull(message = "test identifier missing")
	private final Integer testId;
	
	//identifier for student who makes the attempt
	@NotNull(message = "student's identifier missing")
	private final Integer studentId;
	
	//the list of option chosen; to be served from the front-end component
	@NotNull(message = "selected options missing")
	private final List<String> selectedOptions;

	public AttemptUtil(@NotNull(message = "test identifier missing") Integer testId,
			@NotNull(message = "student's identifier missing") Integer studentId,
			@NotNull(message = "selected options missing") List<String> options) {
		this.testId = testId;
		this.studentId = studentId;
		this.selectedOptions = options;
	}

	public Integer getTestId() {
		return testId;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public List<String> getSelectedOptions() {
		return selectedOptions;
	}
	
	
	
	

}

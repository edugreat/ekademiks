package com.edugreat.akademiksresource.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.edugreat.akademiksresource.model.Question;

public class TestWrapper {
	
	List<Question> questions;

	public TestWrapper(Collection<Question> questions) {
		this.questions = new ArrayList<>(questions);
	}

	public List<Question> getQuestions() {
		
		return questions;
	}
	
	

}

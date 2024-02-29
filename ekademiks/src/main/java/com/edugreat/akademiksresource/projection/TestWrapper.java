package com.edugreat.akademiksresource.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.model.Question;

public class TestWrapper {
	
	List<QuestionDTO> questions = new ArrayList<>();

	public TestWrapper(List<QuestionDTO> questions) {
		this.questions = new ArrayList<>(questions);
	}

	public TestWrapper() {}
	
	public List<QuestionDTO> getQuestions() {
		
		return questions;
	}
	
	public void addQuestion(QuestionDTO dto) {
		
		//checks is the QuestionDTO we intend to add is already in existence by returning
		//a count of the dtos in the collection matches the incoming dto
		long count = questions.stream().filter(x -> x == dto).count();
		if(count < 1)
			questions.add(dto);
	}

}

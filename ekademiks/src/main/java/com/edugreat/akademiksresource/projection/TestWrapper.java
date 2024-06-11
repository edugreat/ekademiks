package com.edugreat.akademiksresource.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.model.Question;

public class TestWrapper {
	
	List<QuestionDTO> questions = new ArrayList<>();
	
	//A collection of instructions for the question
	private Collection<String> instructions = new ArrayList<>();

	public TestWrapper(List<QuestionDTO> questions) {
		this.questions = new ArrayList<>(questions);
	}

	public TestWrapper() {}
	
	public List<QuestionDTO> getQuestions() {
		
		return questions;
	}
	
	public void addQuestion(QuestionDTO dto) {
		
		//checks if the QuestionDTO we intend to add is already in existence by returning
		//a count of the dtos in the collection matches the incoming dto
		long count = questions.stream().filter(x -> x == dto).count();
		if(count < 1)
			questions.add(dto);
	}
	
	//Adds instructions to the collection of instructions object
	public void addInstructions(Collection<String> instructions) {
		
		instructions.forEach(instruction -> this.instructions.add(instruction));
	}

	public Collection<String> getInstructions() {
		return instructions;
	}

}

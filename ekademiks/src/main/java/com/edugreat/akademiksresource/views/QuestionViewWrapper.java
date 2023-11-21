package com.edugreat.akademiksresource.views;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edugreat.akademiksresource.model.Question;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
//provides wrapper for the json view of Question entity
public class QuestionViewWrapper {
	
	@JsonView(QuestionView.class)
	private List<Question> questions;
	
	

}

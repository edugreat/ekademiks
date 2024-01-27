package com.edugreat.akademiksresource.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//Utility class for the Question object
public class QuestionUtil {
	
	@Pattern(regexp = "^[1-9][0-9]+$", message = "question number not supported!")
	private int questionNumber;
	
	@NotNull(message = "expected question text not found")
	@Pattern(regexp = "\\b[\\w\\s\\d!@#$%^&*()-_+=,.;:]+\\b")
	private String text;
	
	@NotNull(message = "Expected property answer not found")
	@Pattern(regexp = "\\b[\\w\\s\\d!@#$%^&*()-_+=,.;:]+\\b")
	private String answer;
	
	private List<OptionUtil> options = new ArrayList<>();
	public QuestionUtil(int questioNumber, String text, String answer, List<OptionUtil> options) {
		this.questionNumber = questioNumber;
		this.text = text;
		this.answer = answer;
		this.options = options;
	}
	public int getQuestionNumber() {
		return questionNumber;
	}
	public String getText() {
		return text;
	}
	public String getAnswer() {
		return answer;
	}
	public List<OptionUtil> getOptions() {
		return options;
	}
	public void setQuestionNumber(int questioNumber) {
		this.questionNumber = questioNumber;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setAnswer(String answer) {
		
		if(answer.trim().length() == 0) {
			throw new IllegalArgumentException("Answer not provided!");
		}
		this.answer = answer;
	}
	public void setOptions(List<OptionUtil> options) {
		this.options = options;
	}
	
	

}

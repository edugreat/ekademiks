package com.edugreat.akademiksresource.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

//Utility class for the Question object
public class QuestionUtil {
	
	@Digits(integer = 2, fraction = 0,  message = "Question number not supported!")
	@Min(value = 1, message = "Question number must be greater than 0")
	@Max(value = 50, message = "Question number must not be greater than 50")
	private int questionNumber;
	
	@NotEmpty(message = "expected question text not found")
		private String text;
	
	@NotEmpty(message = "Expected property answer not found")
	private String answer;
	
	@Valid
	@NotEmpty(message = "Options have not been provided")
	private List<OptionUtil> options = new ArrayList<>();
	public QuestionUtil(int questionNumber, String text, String answer, List<OptionUtil> options) {
		this.questionNumber = questionNumber;
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
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(o == null ||  getClass()!= o.getClass()) return false;
		
		QuestionUtil that = (QuestionUtil)o;
		return this.questionNumber == that.getQuestionNumber();
		
		
	}
	
	//Override the equals and hash code so that no two questions in a test can have same question numbers
	
	
	

}

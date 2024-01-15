package com.edugreat.akademiksresource.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.edugreat.akademiksresource.embeddable.Options;


@Entity
@Table
public class Question {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private int questionNumber;
	
	@Column(nullable = false)
	private String text;
	
	@Column(nullable = false)
	private String answer;
	
	@ElementCollection
	@CollectionTable(name = "options", joinColumns = @JoinColumn(name = "question_id"))
	private Set<Options>options = new HashSet<>();
	
	
	public Question() {}
	
	public Question(int questionNumber, String text, String answer) {
		this.questionNumber = questionNumber;
		this.text = text;
		this.answer = answer;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	

	@Override
	public boolean equals(Object o) {
	
		if(this == o) return true;
		
		if(o == null || getClass() != o.getClass()) return false;
		
			Question that = (Question)o;
			
			//For any object having a set of Question instance variable, we consider two questions
			//in that set to be equal if they have the same question number
			return this.questionNumber == that.getQuestionNumber();
					
		
	
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(questionNumber);
	}

}

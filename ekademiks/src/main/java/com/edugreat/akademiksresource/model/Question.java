package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.edugreat.akademiksresource.embeddable.Options;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table
@Data
@NoArgsConstructor
public class Question implements Comparable<Question>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private int questionNumber;
	
	@Column(nullable = false)
	private String text;
	
	@Column(nullable = false)
	private String answer;
	
	//Transient instruction is not persistence but intends to provide instructions for the Test Questions
	@Transient
	private Collection<String> instructions = new ArrayList<>();
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;
	
	@ElementCollection
	@CollectionTable(name = "options", joinColumns = @JoinColumn(name = "question_id"))
	private Set<Options>options = new HashSet<>();
	
	
	
	
	public Question(int questionNumber, String text, String answer) {
		this.questionNumber = questionNumber;
		this.text = text;
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

	//for sorting capability
	@Override
	public int compareTo(Question o) {
		
		if(this.getQuestionNumber() < o.getQuestionNumber()) return -1;
		else if(this.getQuestionNumber() == o.getQuestionNumber()) return 0;
		
		return 1;
	}

}

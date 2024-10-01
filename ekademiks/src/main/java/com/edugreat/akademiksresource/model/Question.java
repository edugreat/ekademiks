package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


import com.edugreat.akademiksresource.embeddable.Options;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Question implements Comparable<Question> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private int questionNumber;

	@Column(nullable = false)
	private String question;

	@Column(nullable = false)
	private String answer;

	// Transient instruction is not persistence but intends to provide instructions
	// for the Test Questions
	@Transient
	private Collection<String> instructions = new ArrayList<>();

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;

//	the choice of TreeSet implementation is for sorting purpose
	@ElementCollection
	@CollectionTable(name = "options", joinColumns = @JoinColumn(name = "question_id"))
	@Getter(AccessLevel.NONE)
	private Set<Options> options = new TreeSet<>();

	public Question(int questionNumber, String text, String answer) {
		this.questionNumber = questionNumber;
		this.question = text;
		this.answer = answer;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Question that = (Question) o;

		// For any object having a set of Question instance variable, we consider two
		// questions
		// in that set to be equal if they have the same question number
		return this.questionNumber == that.getQuestionNumber();

	}

	@Override
	public int hashCode() {

		return Objects.hash(questionNumber);
	}

	// for sorting capability
	@Override
	public int compareTo(Question o) {

		if (this.getQuestionNumber() < o.getQuestionNumber())
			return -1;
		else if (this.getQuestionNumber() == o.getQuestionNumber())
			return 0;

		return 1;
	}
	
	public Set<Options> getOptions(){
		
		
		return new TreeSet<>(options);
		
	}
	
}

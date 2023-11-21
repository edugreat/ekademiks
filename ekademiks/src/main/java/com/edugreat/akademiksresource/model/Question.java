package com.edugreat.akademiksresource.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.edugreat.akademiksresource.views.QuestionView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//models a question for an academic test exercise
public class Question {
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(QuestionView.class)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for question number is missing")
	@JsonView(QuestionView.WithQuestionTextAndNumber.class)
	//question number
	private int questionNumber;
	
	@Column
	@NotNull(message = "Required field for question topic is missing")
	@JsonView(QuestionView.class)
	//topic for which this question was asked
	private String topic;
	
	@Column
	@NotNull(message = "Required field for question text is missing")
	@JsonView(QuestionView.WithQuestionTextAndNumber.class)
	//the actual problem being asked 
	private String questionText;
	
	@Column
	@NotNull(message = "Required field for question answer is missing")
	//the answer to the question
	private String answer;
	
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "question_id")
	//options(likely answers) associated with this question
	private Set<Option> options;
	
	
	//convenience method
	public void addOption(Option option) {
		
		if(this.options == null) 
		this.options = new HashSet<>();
		
		this.options.add(option);
		
	}
	

}

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
@Builder
//models a question for an academic test exercise
public class Question {
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for question number is missing")
	//question number
	private int questionNumber;
	
	@Column
	@NotNull(message = "Required field for question topic is missing")
	//topic for which this question was asked
	private String topic;
	
	@Column
	@NotNull(message = "Required field for question text is missing")
	//the actual problem being asked 
	private String questionText;
	
	@Column
	@NotNull(message = "Required field for question answer is missing")
	//the answer to the question
	private String answer;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "test_id")
	//information about the test in which this question was test
	private Test test;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "question")
	//options(likely answers) associated with this question
	private Set<Option> options;
	
	
	//convenience method
	public void addOption(Option option) {
		
		if(this.options == null) 
		this.options = new HashSet<>();
		
		this.options.add(option);
		
	}
	

}

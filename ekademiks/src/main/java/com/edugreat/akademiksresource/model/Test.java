package com.edugreat.akademiksresource.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "test")
public class Test {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String testName;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "test", orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Question> questions = new HashSet<>();
	
	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	@Column
	private long duration;
	
	@ManyToOne
	@JoinColumn(name = "subject_id", nullable = false)
	private Subject subject;
	
	public Test() {}

	public Test(String testName, Set<Question> questions, long duration) {
		this.testName = testName;
		this.questions = questions;
		this.duration = duration;
	}

	public Test(String testName, long duration) {
		this.testName = testName;
		this.duration = duration;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Set<Question> getQuestions() {
		return Collections.unmodifiableSet(questions);
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void addQuestion(Question question) {
		
		//TODO: Would provide an exception to handle attempt to add null question 
		if(question!= null) questions.add(question);
		
		//if the question object has already been associated with a test, we shouldn't re-associate it 
		if(question.getTest() != null) question.setTest(this);
		
		
	}
	

}

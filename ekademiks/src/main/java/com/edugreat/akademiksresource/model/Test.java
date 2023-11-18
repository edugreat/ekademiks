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
//models the academic test
public class Test {
	
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for test name is missing")
	private String testName;//the name for this particular test
	
	@Column(updatable = false)
	@NotNull(message = "Required field for test duration is missing")
	//expected duration for this test
	private int duration; 
	
	
	@ManyToOne
	@JoinColumn(name = "subject_id")
	//many to one relationship with subject object
	private Subject subject;
	
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "test")
	//one to many relationship with the question object.
	//Every test has one or more questions associated with
	private Set<Question> questions;
	
	//many to one association with student_test object
	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<StudentTest> studentTests;
	
	
	
	@ManyToOne
	@JoinColumn(name = "level_id")
	//many-to-one relationship with level
	//each academic level has multiple test associated with it
	private Level level;
	
	
	
	//convenience method
	public void addQuestion(Question question) {
		
		if(questions == null)
			questions = new HashSet<>();
		questions.add(question);
	}

	//convenience method
	public void addStudentTest(StudentTest studentTest) {
		
		if(this.studentTests == null)
			this.studentTests = new HashSet<>();
		
		this.studentTests.add(studentTest);
		
	}
}

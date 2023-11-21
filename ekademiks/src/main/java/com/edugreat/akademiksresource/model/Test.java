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

import com.edugreat.akademiksresource.views.TestView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//models the academic test
public class Test {
	
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(TestView.class)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for test name is missing")
	@JsonView(TestView.class)
	private String testName;//the name for this particular test
	
	@Column(updatable = false)
	@NotNull(message = "Required field for test duration is missing")
	//expected duration for this test
	private int duration; 
	
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id")
	//one to many relationship with the question object.
	//Every test has one or more questions associated with
	private Set<Question> questions;
	
	//many to one association with student_test object
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "test_id")
	private Set<StudentTest> studentTests;
	
	
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

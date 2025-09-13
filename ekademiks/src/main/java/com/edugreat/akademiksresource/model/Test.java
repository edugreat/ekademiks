package com.edugreat.akademiksresource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "test")
@EntityListeners(AuditingEntityListener.class)
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String testName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "test", orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Question> questions = new HashSet<>();

	@OneToMany(mappedBy = "test", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	@JsonIgnore
	private Set<StudentTest> studentTests = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instructor_id", nullable = true)
	private Instructor instructor;
	
	@Column
	private long duration;
	
//	institution that owns the Test assessment. By default, the value null showing it belongs to all institutions
	@Column
	private Integer owningInstitution;

	@ElementCollection
	@CollectionTable(name = "instructions")
	@Column(name = "instructions")
	private Collection<String> instructions = new ArrayList<>(); // Instruction for the given test which students should
																	// adhere to

	@ManyToOne
	@JoinColumn(name = "subject_id", nullable = false)
	private Subject subject;
	
	@ManyToOne
	@JoinColumn(name = "classroom_id")
	private Classroom classroom;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime creationDate = LocalDateTime.now();
	
	@LastModifiedDate
	@Column
	private LocalDateTime modifiedOn;
	
	@LastModifiedBy
	@Column
	private String modifiedBy;

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	

	public Classroom getClassroom() {
		return classroom;
	}

	public LocalDateTime getUpdatedOn() {
		return modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public Test() {
	}

	public Test(String testName, Set<Question> questions, long duration) {
		this.testName = testName;
		this.questions = questions;
		this.duration = duration;
	}

	public Test(String testName, long duration, Set<String> instructions) {
		this.testName = testName;
		this.duration = duration;
		this.instructions = instructions;

	}
	
	

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Collection<String> getInstructions() {
		return instructions;
	}

	@SuppressWarnings("unchecked")
	public void setInstructions(Object instructions) {
		this.instructions = (List<String>) instructions;
	}

	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Integer getId() {

		return this.id;
	}
	
	
	

	public Integer getOwningInstitution() {
		return owningInstitution;
	}

	public void setOwningInstitution(Integer owningInstitution) {
		this.owningInstitution = owningInstitution;
	}

	public void addQuestions(Collection<Question> questions) {

		// TODO: Would provide an exception to handle attempt to add null question

		if (this.questions == null) {

			questions = new HashSet<>();
		}

		for (Question question : questions) {
			// if this question is not null and hasn't been associated to any test object,
			// add it to the set of questions asked for this test, then do the bidirectional
			// association
			if (question != null && question.getTest() == null) {

				this.questions.add(question);
				question.setTest(this);
			}
		}

	}

	public Set<StudentTest> getStudentTests() {
		return studentTests;
	}

	public void setStudentTests(Set<StudentTest> studentTests) {
		this.studentTests = studentTests;
	}

	// convenience method
	public void addStudentTest(StudentTest studentTest) {

		if (studentTest != null) {

			this.studentTests.add(studentTest);
		}
	}
	
        public void setClassroom(Classroom classroom) {
		
		if (classroom != null) {
			this.classroom = classroom;
			classroom.getAssessments().add(this);
		} else {
			this.classroom = null;
		}
	}

	@Override
	public int hashCode() {

		return Objects.hash(testName.toLowerCase(), creationDate, 
				classroom.getId(), modifiedBy);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		Test that = (Test) obj;


		
		return Objects.equals(testName.toLowerCase(), that.testName.toLowerCase())
				&& Objects.equals(creationDate, that.creationDate)
				&& Objects.equals(modifiedBy, that.modifiedBy)
				&& Objects.equals(classroom.getId(), that.getClassroom().getId());
	}

	

}
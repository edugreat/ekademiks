package com.edugreat.akademiksresource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.edugreat.akademiksresource.enums.OptionLetter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

/*
 * A StudentTest entity is an intermediate entity between the Student entity and the Test entity.
 * For every student who has taken one or more tests, their information along with the test taken
 * should be persisted in this table. 
 * The student_test table can hold a reference to one student and many tests taken taken.
 * It can also hold the reference to a particular test and many students who have taken.
 * Therefore StudentTest should've the Many-To-Many relationships with the respective Student and Test
 * entities.
 * For the purpose of allowing a particular student take a particular test multiple times, we would
 * declare the field to store the time each test was taken by a student, so we can pull different
 * performances for different attempt.
 */

@Entity
@Table(name = "student_test")
public class StudentTest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// score made by the student in the test.
	// the score for each test can not be updated or manipulated
	@Column(nullable = false, updatable = false)
	private double score;

	// student's grade after a test
	@Column(updatable = false)
	@Pattern(regexp = "^\\d+(\\.\\d+)?%$")
	private String grade;

	// time the test was started or taken as received from the ui framework.
	// This time is read only(can not be updated once created)
	@Column(name = "_when", nullable = false, updatable = false)
	private LocalDateTime when;

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false, updatable = false)
	private Student student;

	@ManyToOne
	@JoinColumn(name = "test_id", nullable = false, updatable = false)
	private Test test;

	// student's response to academic test questions
	@ElementCollection(targetClass = OptionLetter.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "student_response", joinColumns = @JoinColumn(name = "student_test_id"))
	@Column(name = "selected_option")
	private List<OptionLetter> studentResponses = new ArrayList<>();

	public StudentTest() {
	}

	public StudentTest(double score, LocalDateTime when, Student student, Test test,
			List<OptionLetter> studentResponses) {
		this.score = score;
		this.when = when;
		this.student = student;
		this.test = test;
		this.studentResponses = studentResponses;
	}

	public double getScore() {
		return score;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade + "%";
	}

	public LocalDateTime getWhen() {
		return when;
	}

	public Integer getId() {
		return id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public List<OptionLetter> getStudentResponse() {
		return studentResponses;
	}

	@Override
	public int hashCode() {

		final int value = 35;

		int x = 31 * (value + (id == null ? 0 : id.hashCode()));

		return x;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		StudentTest that = (StudentTest) obj;

		if (id == null) {

			if (that.getId() != null)
				return false;

		}
		return id == that.getId();

	}

}

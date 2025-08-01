package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.edugreat.akademiksresource.classroom.ClassroomSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String subjectName;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;

	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Test> tests = new ArrayList<>();
	
	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Set<ClassroomSubject> classroomSubjects = new HashSet<>();


	public Subject() {
	}

	public Subject(String subjectName, Level level) {
		this.subjectName = subjectName;
		this.level = level;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level levelId) {
		this.level = levelId;
	}

	public List<Test> getTest() {
		return tests;
	}

	public void setTest(List<Test> tests) {
		this.tests = tests;
	}

	// convenience method to add test to a list tests
	public void addTest(Test test) {
		// if this test is not null, and has't been associated to any Subject, add it
		// the set of tests for this Subject
		if (test != null) {

			tests.add(test);
			test.setSubject(this);
		}

	}

	
	
	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public Set<ClassroomSubject> getClassroomSubjects() {
		return classroomSubjects;
	}

	public void setClassroomSubjects(Set<ClassroomSubject> classroomSubjects) {
		this.classroomSubjects = classroomSubjects;
	}

	

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (this == null || getClass() != o.getClass())
			return false;

		Subject that = (Subject) o;

		return subjectName.equals(that.getSubjectName()) && level.getCategory() == that.getLevel().getCategory();

	}

	@Override
	public int hashCode() {
		return Objects.hash(subjectName.concat(level.getCategory().toString()));
	}

}

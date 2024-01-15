package com.edugreat.akademiksresource.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

//object that models a student who has taken one or more tests

@Entity
@Table(name = "student_test")
public class StudentTest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	
	//tests which the student has taken
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Set<Test> tests = new HashSet<>();
	
	// a collection of true and or false showing the number of options that are correct or wrong
	@ElementCollection
	@CollectionTable(name = "selected_option")
	private Collection<Boolean> selectedOptions = new ArrayList<>();
	
	
	@Column(name = "started_on", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDate startedOn;
	
	@Column(name = "score", nullable = false)
	private double score;
	
	public StudentTest() {}

	public StudentTest(LocalDate startedOn, double score) {
		this.startedOn = startedOn;
		this.score = score;
	}

	public Set<Test> getTests() {
		return Collections.unmodifiableSet(tests);
	}

	public void setTests(Set<Test> tests) {
		this.tests = tests;
	}

	public LocalDate getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(LocalDate startedOn) {
		this.startedOn = startedOn;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Integer getId() {
		return id;
	}
	
	
	
	
	public Collection<Boolean> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(Collection<Boolean> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	//convenience method to add tests taken by a student
	public void addTest(Test test) {
		
		this.tests.add(test);
	}

	//convenience method to set student selected options(logical values only)
	public void addSelectedOption(boolean selected) {
		
		this.selectedOptions.add(selected);
		
	}
}

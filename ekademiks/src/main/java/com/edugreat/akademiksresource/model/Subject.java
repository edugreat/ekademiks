package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Subject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private String subjectName;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "level_id",nullable = false)
	private Level level;

	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Test> tests = new ArrayList<>();
	
	
	public Subject() {}
	
	public Subject(String subjectName, Level levelId) {
		this.subjectName = subjectName;
		this.level = levelId;
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

	
	//convenience method to add test to a list tests
	public void addTest(Test test) {
		
		tests.add(test);
		
		
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		
		if(this == null || getClass() != o.getClass()) return false;
		
		Subject that = (Subject)o;
		
		return subjectName.equals(that.getSubjectName());
		
	}
	
}

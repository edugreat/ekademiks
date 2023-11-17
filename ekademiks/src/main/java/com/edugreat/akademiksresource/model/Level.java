package com.edugreat.akademiksresource.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import enums.Levels;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
@Builder
//models the academic level(example 'SENIOR' or 'JUNION) suitable for an online test
public class Level {
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Required field, category is missing")
	private Levels category;
	
	//one-to-one relationship with subject
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "level")
	private Set<Subject> subjects;
	
	//one-to-many relationship with test
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "level")
	private Set<Test> tests;
	
	
	//convenience method
	public void addSubject(Subject subject) {
		
		if(this.subjects == null)
			this.subjects = new HashSet<>();
		
		this.subjects.add(subject);
	}
	
	//convenience method
	public void addTest(Test test) {
		
		if(this.tests == null)
			this.tests = new HashSet<>();
		
		this.tests.add(test);
	}

}

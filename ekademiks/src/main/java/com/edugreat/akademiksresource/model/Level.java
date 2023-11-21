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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	//each academic level has a category it belongs to.
	private Levels category;
	
	
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "level_id")
	//the subjects that belong to this academic level
	private Set<Subject> subjects;
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "level_id")
	//information about the tests in this academic level
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

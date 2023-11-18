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
//this class models an academic subject for an online test
public class Subject {
	
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
 	@NotNull(message = "Required field for subject is missing")
	private String subjectName;
	
	//bidirectional relationship with Level.
	//the level is the academic level this subject belongs to
	@ManyToOne
	@JoinColumn(name = "level_id")
	private Level level;
	
	//one to many relationship with test object
	//each academic subject is expected to feature in one of more academic tests
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "subject")
	private Set<Test> tests;
	
	//convenience method to associate a test with a subject
	public void addTest(Test test) {
		
		if(tests == null)
			tests = new HashSet<>();
		tests.add(test);
	}
	

}

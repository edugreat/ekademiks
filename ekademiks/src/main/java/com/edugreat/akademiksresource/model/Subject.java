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

import com.edugreat.akademiksresource.views.SubjectView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subject")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//this class models an academic subject for an online test
public class Subject {
	
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(SubjectView.class)//forms part of the object view for object serialization
	private Integer id;
	
	@Column
 	@NotNull(message = "Required field for subject is missing")
	@JsonView(SubjectView.class)//forms part of the object view for object serialization
	private String subjectName;
	
	
	
	//one to many relationship with test object
	//each academic subject is expected to feature in one of more academic tests
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "subject_id")
	private Set<Test> tests;
	
	//convenience method to associate a test with a subject
	public void addTest(Test test) {
		
		if(tests == null)
			tests = new HashSet<>();
		tests.add(test);
	}
	

}

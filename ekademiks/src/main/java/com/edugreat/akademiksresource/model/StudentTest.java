package com.edugreat.akademiksresource.model;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
@Builder

//models a student who'd participated in an academic test
public class StudentTest {
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for score is missings")
	private double score;
	
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStarted;
	
	
	@Column(updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastResumedOn;//last date the student resumed on this test
	
	
	//many-to-one relationship with the test object; a test can have many students
	@ManyToOne
	@JoinColumn(name  = "test_id")
	private Test test;
	
	//many to one relationship between the student_test and Student table; a Student can take multiple tests
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;
	
    @OneToMany(cascade =CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval= true, mappedBy = "studentTest")
    private Set<StudentSelectedOption> studentSelectedOptions;
    
    
}

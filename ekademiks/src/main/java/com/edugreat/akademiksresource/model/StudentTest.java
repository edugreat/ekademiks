package com.edugreat.akademiksresource.model;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name ="student_test")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//models a student who'd participated in an academic test
public class StudentTest {
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for score is missings")
	//every student who participated in a test has some score
 	private double score;
	
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	//this is the date a student first attempted a test.
	//this date is expected not to change after its first creation
	private Date dateStarted;
	
	
	@Column(updatable = true)
	@Temporal(TemporalType.TIMESTAMP)
	//last date since the student resumed on this test
	//this field is always updatable based on how frequent the student retries
	private Date lastResumedOn;
	
	
    @OneToMany(cascade =CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval= true)
    @JoinColumn(name = "student_test_id")
    //options(likely answers) selected by a student while taking test  
    private Set<StudentSelectedOption> studentSelectedOptions;
    
    
    //convenience method 
    public void addStudentSelectedOption(StudentSelectedOption selectedOption) {
    	if(this.studentSelectedOptions == null)
    		this.studentSelectedOptions = new HashSet<StudentSelectedOption>();
    	
    	this.studentSelectedOptions.add(selectedOption);
    	
    }
    
    
}

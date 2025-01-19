package com.edugreat.akademiksresource.assignment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Institution;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
// Entity holds details about a given assignment
public class AssignmentDetails {
	
//	unique identifier
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
//	name of the assignment
	@Column(nullable = false)
	private String name;
	
//	the instructor that set the assignment 
	@OneToOne
	private Admins instructor;
	
//	the assignment subject
	@Column(nullable = false)
	private String subject;
	
//	institution the assignment belongs to
	@OneToOne
	private Institution institution;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AssignmentPDF> assignmentPDFs = new HashSet<>();
	
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Assignment> assignments = new HashSet<>();
	
//	allocated mark for the assignment
	@Column(updatable = true, nullable = false)
	private Double allocatedMark;
	
//	when the assignment was created
	@Column(updatable = false, nullable = false)
	@Setter(AccessLevel.NONE)
	private LocalDateTime creationDate;
	
//	last submission date for the assignment
	@Column(updatable = true, nullable = false)
	@Future
	private LocalDateTime submissionEnds;
	
//	for junior or senior students
	@Column(nullable = false)
	private String category; 
	
//	Whether it's theory or objective based assignment
	@Column(nullable = true)
	private String type;
	
//	number of questions contained in the assignment
	private int totalQuestions;
	
	public AssignmentDetails() {
		
		creationDate = LocalDateTime.now();
	}

	public AssignmentDetails(Admins instructor, String subject, Institution institution, Double allocatedMark,
			LocalDateTime submissionEnds, String name, String category, String type, int totalQuestions) {
		
		this.instructor = instructor;
		this.subject = subject;
		this.institution = institution;
		this.allocatedMark = allocatedMark;
		this.submissionEnds = submissionEnds;
		this.name = name;
		this.category = category;
		this.type = type;
		this.totalQuestions = totalQuestions;
		
		creationDate = LocalDateTime.now();
	}
	
	
	public void addPDF(AssignmentPDF pdf) {
		
		if(assignmentPDFs.contains(pdf)) throw new IllegalArgumentException("Atempt to upload duplicate PDFs");
		
		assignmentPDFs.add(pdf);
	}
	
	
	public void addAssignment(Assignment assignment) {
		
		if(assignments.contains(assignment)) throw new IllegalArgumentException("Duplicate assignment");
		
		assignments.add(assignment);
	}
	

}

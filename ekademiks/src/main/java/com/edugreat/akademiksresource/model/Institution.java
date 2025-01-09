package com.edugreat.akademiksresource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
public class Institution {
	
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	@Column
	private String name; //name of institution
	
	@Column(updatable = false)
	private LocalDateTime createdOn;
	
	@Column
	private String state; // the state the institution is located
	
	@Column
	private String localGovt; // the LGA the institution is located
	@Column(updatable = false)
	private Integer createdBy;  // the identifier of the admin who created the account
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "institution")
	private List<Student> studentList = new ArrayList<>();
	
	public Institution() {
		
		createdOn = LocalDateTime.now();
	}
	
	
	
	
	public boolean addStudent(List<Student> existingStudents, Student student) {
		
		List<Student> list = existingStudents.stream().filter(s -> s.getId() == student.getId()).collect(Collectors.toList());
		
		if(!list.isEmpty()) return false; //false return shows the student is already member of the institution and hence cannot be added again
		
		student.setInstitution(this);
		
		return studentList.add(student);
		
		
		
		
		
		
		
	}




	public Institution(String name, String state, String localGovt, Integer createdBy) {
		
		this.createdOn = LocalDateTime.now();
		this.name = name;
		this.state = state;
		this.localGovt = localGovt;
		this.createdBy = createdBy;
	}

}

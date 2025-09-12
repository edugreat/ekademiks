package com.edugreat.akademiksresource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.edugreat.akademiksresource.instructor.Instructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdOn;
	
	@Column
	private String state; // the state the institution is located
	
	@Column
	private String localGovt; // the LGA the institution is located
	
	@Column(updatable = false)
	private Integer createdBy;  // the identifier of the admin who created the account
	
	@Column
	private Integer studentPopulation;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "institution")
	//@JsonManagedReference
	@JsonIgnore
	private List<Student> students = new ArrayList<>();
	
	@ManyToMany(mappedBy = "institutions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Instructor> instructors = new HashSet<>();
	
	public Institution() {
		
		createdOn = LocalDateTime.now();
		
		studentPopulation = 0;
	}
	
	
public Institution(String name, String state, String localGovt, Integer createdBy) {
		
		this.createdOn = LocalDateTime.now();
		this.name = name;
		this.state = state;
		this.localGovt = localGovt;
		this.createdBy = createdBy;
	}
	
	
	
	
	public boolean addStudent(List<Student> existingStudents, Student student) {
		
		List<Student> list = existingStudents.stream().filter(s -> s.getId() == student.getId()).collect(Collectors.toList());
		
		if(!list.isEmpty()) return false; //false return shows the student is already member of the institution and hence cannot be added again
		
		student.setInstitution(this);
		

		
		
		return students.add(student);
		
		
	}
	
	// In Institution.java
	public void addInstructor(Instructor instructor) {
	    if (instructor != null && !this.instructors.contains(instructor)) {
	        this.instructors.add(instructor);
	        instructor.getInstitutions().add(this);
	    }
	}

	public void removeInstructor(Instructor instructor) {
	    if (instructor != null && this.instructors.contains(instructor)) {
	        this.instructors.remove(instructor);
	        instructor.getInstitutions().remove(this);
	    }
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		Institution other = (Institution) obj;
		return Objects.equals(localGovt.toLowerCase(), other.localGovt.toLowerCase()) 
				&& Objects.equals(name.toLowerCase(), other.name.toLowerCase())
				&& Objects.equals(state.toLowerCase(), other.state.toLowerCase());
	}


	@Override
	public int hashCode() {
		return Objects.hash(localGovt, name.toLowerCase(), state.toLowerCase());
	}




	

}

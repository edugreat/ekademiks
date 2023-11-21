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
import javax.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//models a student taking an academic test
public class Student {
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	@Column(updatable = false)
	private Integer id;
	
	@Column
	@NotNull(message = "username is required")
	private String username;
	
	@Column
	@NotNull(message = "first name is required")
	private String first_name;
	
	@Column
	@NotNull(message = "last name is required")
	private String last_name;
	
	@Column
	@NotNull(message = "email is required")
	private String email;
	
	@Column
	@NotNull(message = "password is required")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,}$",
	message ="must be at least 8 characters "
			+ "with at least one uppercase letter, "
			+ "one lowercase letter, one digit, "
			+ "and one special character")
	private String password;
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "question_id")
	//one to many relationship with the student_test
	//a student can have more than tests they have taken
	private Set<StudentTest> studentTest;
	
	//convenience method
	public void addStudentTest(StudentTest stdTest) {
		
		if(studentTest == null)
			studentTest = new HashSet<>();
		
		studentTest.add(stdTest);
		
	}

}

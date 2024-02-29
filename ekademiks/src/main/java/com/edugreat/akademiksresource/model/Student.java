package com.edugreat.akademiksresource.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Data
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "first_name", nullable = false)
	@NotBlank(message = "first name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	private String firstName;

	@Column(name = "last_name", nullable = false)
	@NotBlank(message = "last name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	private String lastName;

	@Column(name = "email", unique = true, nullable = true)
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email required")
	private String email;

	@Column(name = "mobile", unique = true, nullable = false)
	@Pattern(regexp = "^(?:\\+234|\\b0)([789]\\d{9})$", message = "Unsupported mobile number")
	@NotBlank(message = "mobile number required")
	private String mobileNumber;

	@Column(name = "password", nullable = false)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_+=<>?]).{8,}$")
	@NotBlank(message = "password required")
	private String password;

	@Setter
	@Getter
	private byte storedHash[];

	@Setter
	@Getter
	private byte storedSalt[];

	public Student() {
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "student")
	private Set<StudentTest> studentTests = new HashSet<>();

	// convenience method
	public void addStudentTest(StudentTest studentTest) {

		if (studentTest != null) {
			this.studentTests.add(studentTest);
		}

	}

	public Student(String firstName, String lastName, String email, String mobileNumber, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.password = password;
	}

	public Student(String firstName, String lastName, String mobileNumber, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobileNumber = mobileNumber;
		this.password = password;
	}

}

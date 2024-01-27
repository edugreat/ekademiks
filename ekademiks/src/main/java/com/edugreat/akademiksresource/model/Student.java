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

@Entity
@Table
public class Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "email", nullable = true)
	private String email;
	
	@Column(name = "password")
	private String password;
	
	public Student() {}

	public Student(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "student")
	private Set<StudentTest> studentTests = new HashSet<>();
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public Set<StudentTest> getStudentTests() {
		return studentTests;
	}

	public void setTests(Set<StudentTest> studentTests) {
		this.studentTests = studentTests;
	}
	
	
		//convenience method
	public void addStudentTest(StudentTest studentTest) {
		
		if(studentTest != null) {
			this.studentTests.add(studentTest);
		}
		
	}
	
	

}

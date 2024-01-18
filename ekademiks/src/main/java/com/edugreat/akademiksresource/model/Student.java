package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinTable(name = "student_user_test")
	private List<StudentTest> studentTests = new ArrayList<>();
	
	
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
	
	

}

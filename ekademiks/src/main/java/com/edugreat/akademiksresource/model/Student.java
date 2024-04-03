
package com.edugreat.akademiksresource.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.edugreat.akademiksresource.enums.Roles;

@Entity
@Table
//this is a subclass of the AppUser base class
public class Student extends AppUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserRoles> roles = new HashSet<>();

	public Student() {
		super();

	}

	public Student(String firstName, String lastName, String email, String mobileNumber, String password) {
		super(firstName, lastName, email, mobileNumber, password);

	}

	public Student(String firstName, String lastName, String email, String password) {
		super(firstName, lastName, email, password);

	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "student", orphanRemoval = true)
	private Set<StudentTest> studentTests = new HashSet<>();

	// convenience method
	public void addStudentTest(StudentTest studentTest) {

		if (studentTest != null) {
			this.studentTests.add(studentTest);
		}

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return List.of(new SimpleGrantedAuthority(Roles.Student.name()));
	}

	public Set<StudentTest> getStudentTests() {
		return studentTests;
	}

	public void setStudentTests(Set<StudentTest> studentTests) {
		this.studentTests = studentTests;
	}

	public Set<String> getRoles() {
		return roles.stream().map(role -> role.getRole().toString()).collect(Collectors.toSet());
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}
	
	public void addRoles(Set<String> roles) {
		for(String role: roles) {
			this.roles.add(new UserRoles(Roles.valueOf(role)));
		}
	}
	
	

}

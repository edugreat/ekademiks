
package com.edugreat.akademiksresource.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

//	A set of assessmentUploadNotifications a student receives
	@ManyToMany
	@JoinTable(name = "student_notifications", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "notification_id"))

	private Set<AssessmentUploadNotification> assessmentNotifications = new HashSet<>();

	// convenience method
	public void addStudentTest(StudentTest studentTest) {

		if (studentTest != null) {
			this.studentTests.add(studentTest);
		}

	}

	public Set<AssessmentUploadNotification> getNotifications() {
		return assessmentNotifications;
	}

//     Convenience method to add notification to student
	public void AddNotification(AssessmentUploadNotification notication) {

		if (!assessmentNotifications.contains(notication)) {

			assessmentNotifications.add(notication);
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
		for (String role : roles) {
			this.roles.add(new UserRoles(Roles.valueOf(role)));
		}
	}

}

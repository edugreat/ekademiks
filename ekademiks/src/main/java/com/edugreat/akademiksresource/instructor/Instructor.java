package com.edugreat.akademiksresource.instructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.edugreat.akademiksresource.classroom.ClassroomSubject;
import com.edugreat.akademiksresource.model.AppUser;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.model.UserRoles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table
public class Instructor extends AppUser {

    @Serial
    private static final long serialVersionUID = 1L;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	    name = "instructor_institution",
	    joinColumns = @JoinColumn(name = "instructor_id"), 
	    inverseJoinColumns = @JoinColumn(name = "institution_id") 
	)
	private List<Institution> institutions = new ArrayList<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	    name = "instructor_student",
	    joinColumns = @JoinColumn(name = "instructor_id"), 
	    inverseJoinColumns = @JoinColumn(name = "student_id") 
	)
	private Set<Student> students = new HashSet<>();

	
	@OneToMany(mappedBy = "instructor")
	private Set<ClassroomSubject> classroomSubjects = new HashSet<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "instructor",  cascade = {
		    CascadeType.PERSIST, 
		    CascadeType.MERGE,
		    CascadeType.REFRESH,
		    CascadeType.DETACH
		})
	private List<Test> tests = new ArrayList<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserRoles> roles = new HashSet<>();
	
	

	public Instructor() {
		super();
		
	}

	public Instructor(String firstName, String lastName, String email, String mobileNumber, String password) {
		super(firstName, lastName, email, mobileNumber, password);
		
	}

	public Instructor(String firstName, String lastName, String email, String password) {
		super(firstName, lastName, email, password);
		
	}

	public List<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<Institution> institutions) {
		this.institutions = institutions;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}
	public Set<String> getRoles() {
		return roles.stream().map(role -> role.getRole().toString()).collect(Collectors.toSet());
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String role : this.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role));

		}

		return authorities;
	}
	
	
	
	public void addTest(Test t) {
		
		if(!tests.contains(t)) {
			
			tests.add(t);
			t.setInstructor(this);
		}
	}
	
	// In Instructor.java
	public void addStudent(Student student) {
	    students.add(student);
	    student.getInstructors().add(this);
	}

	public void removeStudent(Student student) {
	    students.remove(student);
	    student.getInstructors().remove(this);
	}
	
	// In Instructor.java
	public void addInstitution(Institution institution) {
	    if (institution != null && !this.institutions.contains(institution)) {
	        this.institutions.add(institution);
	        institution.getInstructors().add(this);
	    }
	}

	public void removeInstitution(Institution institution) {
	    if (institution != null && this.institutions.contains(institution)) {
	        this.institutions.remove(institution);
	        institution.getInstructors().remove(this);
	    }
	}

	public Set<ClassroomSubject> getClassroomSubjects() {
		return classroomSubjects;
	}

	public void setClassroomSubjects(Set<ClassroomSubject> classroomSubjects) {
		this.classroomSubjects = classroomSubjects;
	}

	@Override
	public boolean equals(Object o) {
		
		if(this == o)return true;
		if(!(o instanceof Student)) return false;
		Instructor that = (Instructor)o;
		
		return Objects.equals(this.getEmail().toLowerCase(), that.getEmail().toLowerCase());
				
	}

	@Override
	public int hashCode() {
		
		return Objects.hash(this.getEmail().toLowerCase());
	}
	

	
	
	

}

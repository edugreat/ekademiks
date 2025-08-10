package com.edugreat.akademiksresource.classroom;

import java.util.HashSet;
import java.util.Set;

import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "subject_classroom")
public class ClassroomSubject {
	
	

	@EmbeddedId
	private ClassroomSubjectId id;
	
	@ManyToOne
	@MapsId("classroomId")
	private Classroom classroom;
	
	@ManyToOne
	@MapsId("subjectId")
	private Subject subject;
	
	@OneToMany(mappedBy = "classroomSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<StudentElectiveEnrollment> electiveEnrollments = new HashSet<>();
	
	@ManyToOne
	private Instructor instructor;
	
	@Column(name = "is_primary_instructor", nullable = false)
	private boolean isPrimaryInstructor = false;
	
	
	
	public ClassroomSubject() {
		
	}
	

	public ClassroomSubject(Classroom classroom, Subject subject) {
	
		this.classroom = classroom;
		this.subject = subject;
	}
	
	
	public ClassroomSubject(Classroom classroom, Subject subject, Instructor instructor) {
		
		if(!classroom.getLevel().equals(subject.getLevel())) {
			throw new IllegalArgumentException("Classroom level must match subject level");
		}
		
		this.classroom = classroom;
		this.subject = subject;
		this.instructor = instructor;
		this.isPrimaryInstructor = instructor.equals(classroom.getPrimaryInstructor());
		this.id = new ClassroomSubjectId(classroom.getId(), subject.getId());
		
	}
	
	
	
	 public void assignInstructor(Instructor instructor) {
	        this.instructor = instructor;
	        instructor.getClassroomSubjects().add(this);
	    }






	public Classroom getClassroom() {
		return classroom;
	}



	public boolean isPrimaryInstructor() {
		return isPrimaryInstructor;
	}


	
	

	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}



	public Subject getSubject() {
		return subject;
	}



	public void setSubject(Subject subject) {
		this.subject = subject;
	}



	public Instructor getInstructor() {
		return instructor;
	}



	public Set<StudentElectiveEnrollment> getElectiveEnrollments() {
		return electiveEnrollments;
	}


	public void setElectiveEnrollments(Set<StudentElectiveEnrollment> electiveEnrollments) {
		this.electiveEnrollments = electiveEnrollments;
	}


	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}



	public ClassroomSubjectId getId() {
		return id;
	}
	
	public void addElectiveStudent(Student student) {
	    if (student == null || !this.classroom.getStudents().contains(student)) {
	        throw new IllegalArgumentException("Student not in classroom");
	    }
	    StudentElectiveEnrollment enrollment = new StudentElectiveEnrollment(student, this);
	    electiveEnrollments.add(enrollment);
	    enrollment.setClassroomSubject(this); 
	}
	
	

	public void removeElectiveStudent(Student student) {
	    if (student == null) return;
	    
	    electiveEnrollments.removeIf(enrollment -> {
	        if (enrollment.getStudent().getId().equals(student.getId())) {
	            enrollment.setClassroomSubject(null);
	            return true;
	        }
	        return false;
	    });
	}
	
}



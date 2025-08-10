package com.edugreat.akademiksresource.classroom;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.edugreat.akademiksresource.model.Student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "student_elective_enrollment",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "classroom_subject_id"}))
@EntityListeners(AuditingEntityListener.class)
public class StudentElectiveEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumns({
	   @JoinColumn(name = "classroom_id", referencedColumnName = "classroom_id"),
	    @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
   })
    private ClassroomSubject classroomSubject;

    @Column(nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();
    
    @LastModifiedBy
    @Column(nullable = false)
    private String enrolledBy;
    
    public StudentElectiveEnrollment () {}

	public StudentElectiveEnrollment(Student student, ClassroomSubject classroomSubject) {
		
		this.student = student;
		this.classroomSubject = classroomSubject;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public ClassroomSubject getClassroomSubject() {
		return classroomSubject;
	}

	public void setClassroomSubject(ClassroomSubject classroomSubject) {
		this.classroomSubject = classroomSubject;
	}

	public LocalDateTime getEnrolledAt() {
		return enrolledAt;
	}

	public void setEnrolledAt(LocalDateTime enrolledAt) {
		this.enrolledAt = enrolledAt;
	}

	public Integer getId() {
		return id;
	}

    
}
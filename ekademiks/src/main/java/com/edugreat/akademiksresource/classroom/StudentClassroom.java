package com.edugreat.akademiksresource.classroom;

import java.time.LocalDateTime;

import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
  * This entity tracks which student is enrolled in which class,
  * Which instructor or admin enrolled a particular student,
  * At what time a student get enrolled,
  * Which institution is the student enrolled in the class
  * Note: A student cannot be enrolled in multiple classrooms of the same institution
  */

@Entity
@Table(name = "student_classroom",
       indexes = {
           @Index(columnList = "student_id, classroom_id", unique = true),
           @Index(columnList = "student_id, institution_id", unique = true)
       })
public class StudentClassroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(nullable = false)
    private LocalDateTime enrollmentDate;

    @Column(nullable = false, length = 100)
    private String enrolledBy; // Stores email of admin/instructor who enrolled the student

    // Constructors, getters, and setters
    public StudentClassroom() {
       
    }

    public StudentClassroom(Student student, Classroom classroom, String enrolledBy) {
        this.student = student;
        this.classroom = classroom;
        this.institution = classroom.getInstitution();
        this.enrolledBy = enrolledBy;
    }

   
    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getEnrolledBy() {
        return enrolledBy;
    }

    public void setEnrolledBy(String enrolledBy) {
        this.enrolledBy = enrolledBy;
    }
    
    @PrePersist
    protected void enrollmentDate() {
    	 this.enrollmentDate = LocalDateTime.now();
    }
}
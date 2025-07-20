package com.edugreat.akademiksresource.classroom;

import java.io.Serializable;
import java.util.Objects;

import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.model.Subject;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

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



	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}



	public ClassroomSubjectId getId() {
		return id;
	}

	
	
}

@Data
 class ClassroomSubjectId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer classroomId;
	private Integer subjectId;

	public ClassroomSubjectId() {
	}

	public ClassroomSubjectId(Integer classroomId, Integer subjectId) {
		this.classroomId = classroomId;
		this.subjectId = subjectId;
	}

	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof ClassroomSubjectId)) return false;
	        ClassroomSubjectId that = (ClassroomSubjectId) o;
	        return Objects.equals(classroomId, that.classroomId) &&
	               Objects.equals(subjectId, that.subjectId);
	    }
	    
	    @Override
	    public int hashCode() {
	        return Objects.hash(classroomId, subjectId);
	    }
	
}
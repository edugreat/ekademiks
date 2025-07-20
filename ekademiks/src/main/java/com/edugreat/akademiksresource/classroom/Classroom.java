package com.edugreat.akademiksresource.classroom;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Setter;

@Entity
@Table(name = "classroom",
indexes = {
	    @Index(columnList = "level_id, academicYear, section", unique = true),
	    @Index(columnList = "instructor_id")
	}
		)

public class Classroom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	@Column(nullable = false, length = 100, unique = true)
	private String name;
	
	@Column( length = 500)
	private String description;
	
	@Column(nullable = false)
	private Integer academicYear; //enables performance tracking by academic year
	
	@Column(length = 10)
	private String section;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instructor_id", nullable = false)
	private Instructor primaryInstructor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;
	
	@OneToMany(
			   mappedBy = "classroom",
			    cascade = CascadeType.ALL,
			    orphanRemoval = true
			)
	private Set<Student> students = new HashSet<>();
	
	
	
	@OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ClassroomSubject> classroomSubjects = new HashSet<>();
	
	
	
	@OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Test> assessments = new HashSet<>();

	
	public Classroom() {
	
	}
	
	public void addStudent(Student student) {
		if (student != null) {
			students.add(student);
			student.setClassroom(this);
		}
	}
	
	public void removeStudent(Student student) {
		if (student != null) {
			students.remove(student);
			student.setClassroom(null);
		}
	}
	
	
	public void migrateStudent(Student student, Classroom newClassroom) {
		if (student != null && newClassroom != null) {
			removeStudent(student);
			newClassroom.addStudent(student);
		}
	}
	
	public void promoteToNextAcademicYear() {
		this.academicYear++;
		
	}

	
	
	public void addAssessment(Test test) {
	    if (test != null) {
	        assessments.add(test);
	        test.setClassroom(this);
	    }
	}

	public Set<Test> getAssessmentsBySubject(Subject subject) {
	    return assessments.stream()
	        .filter(t -> t.getSubject().equals(subject))
	        .collect(Collectors.toSet());
	}
	
	public void assignSubject(Subject subject, Instructor instructor) {
		
	 if(!subject.getLevel().equals(this.level)) {
			throw new IllegalArgumentException("Subject level does not match classroom level");
		}
		
		ClassroomSubject classroomSubject = new ClassroomSubject(
				this, subject, instructor
				);
		
		classroomSubjects.add(classroomSubject);
	}
	
	 public Instructor getSubjectInstructor(Subject subject) {
	        return classroomSubjects.stream()
	            .filter(cs -> cs.getSubject().equals(subject))
	            .findFirst()
	            .map(ClassroomSubject::getInstructor)
	            .orElse(null);
	    }
	 
	 public Set<Instructor> getAllInstructors() {
		    Set<Instructor> instructors = new HashSet<>();
		    instructors.add(this.primaryInstructor); // Always include primary
		    this.classroomSubjects.stream()
		        .map(ClassroomSubject::getInstructor)
		        .forEach(instructors::add);
		    return instructors;
		}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(Integer academicYear) {
		this.academicYear = academicYear;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Instructor getPrimaryInstructor() {
		return primaryInstructor;
	}

	public void setPrimaryInstructor(Instructor primaryInstructor) {
		this.primaryInstructor = primaryInstructor;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public Set<ClassroomSubject> getClassroomSubjects() {
		return classroomSubjects;
	}

	public void setClassroomSubjects(Set<ClassroomSubject> classroomSubjects) {
		this.classroomSubjects = classroomSubjects;
	}

	public Set<Test> getAssessments() {
		return assessments;
	}

	public void setAssessments(Set<Test> assessments) {
		this.assessments = assessments;
	}

	public Integer getId() {
		return id;
	}
	 
	 
	

}

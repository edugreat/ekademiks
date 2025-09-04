package com.edugreat.akademiksresource.classroom;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.edugreat.akademiksresource.classroom.StudentClassroom.EnrollmentStatus;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
	    @Index(columnList = "level_id, institution_id, academicYear, name"),
	    @Index(columnList = "instructor_id"),
	    @Index(columnList="institution_id, name, academicYear", unique = true)
	}
		)
@EntityListeners(AuditingEntityListener.class)
public class Classroom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column( length = 500)
	private String description;
	
	@Column(nullable = false)
	private Integer academicYear; //enables performance tracking by academic year
	
	@Column(length = 10)
	private String section;
	
	@Column(updatable = false, nullable = false)
	@CreatedDate
	private LocalDateTime creationDate;
	
	@Column(nullable = false)
	@LastModifiedDate
	private LocalDateTime lastModified;
	
	@Column
	@LastModifiedBy
	private String lastModifiedBy;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instructor_id", nullable = false)
	private Instructor primaryInstructor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;
	
	
	
	 @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
	    private Set<StudentClassroom> studentEnrollments = new HashSet<>();

	
	
	@OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ClassroomSubject> classroomSubjects = new HashSet<>();
	
	
	
	@OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Test> assessments = new HashSet<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_id", nullable = false)
	private Institution institution;

	
	public Classroom() {
	
	}
	
//	public void addStudent(Student student,String enrolledBy) {
//		if (student != null) {
//			
//			System.out.println("about to enroll");			
//			StudentClassroom enrollment = new StudentClassroom(student, this, enrolledBy);
//		
//			studentEnrollments.add(enrollment);
//			
//			
//			student.getClassroomEnrollments().add(enrollment);
//		}
//	}
	
	public void removeStudent(Student student) {
		if (student != null) {
			
			
			studentEnrollments.removeIf(enrollment -> {
				
				if(enrollment.getStudent().equals(student)) {
					
					student.getClassroomEnrollments().remove(enrollment);
					
					return true;
				}
				
				return false;
			});
			
			
			
		}
	}
	
	
	public void promoteStudent(Student student, Classroom targetClassroom, String enrolledBy) {
		
		StudentClassroom currentEnrollment = findActiveEnrollment(student)
				                             .orElseThrow(() -> new IllegalArgumentException("Student not enrolled in this class"));
	
	currentEnrollment.setEnrollmentStatus(EnrollmentStatus.PROMOTED);
	currentEnrollment.setCompletionDate(LocalDateTime.now());
	
	currentEnrollment.setCompletedBy(enrolledBy);
	//targetClassroom.addStudent(student, enrolledBy);
	
	}
	
	  protected Optional<StudentClassroom> findActiveEnrollment(Student student) {
		  
		  
		  
	        return studentEnrollments.stream()
	            .filter(e -> e.getStudent().equals(student)) 
	            .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.ACTIVE)
	            .findFirst();
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
	
	  public Set<StudentClassroom> getStudentEnrollments() {
	        return studentEnrollments;
	    }

	    public void setStudentEnrollments(Set<StudentClassroom> studentEnrollments) {
	        this.studentEnrollments = studentEnrollments;
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
		    instructors.add(this.primaryInstructor); 
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
	
	

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
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

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
	
	public Set<Student> getActiveStudents(){
		
		return this.studentEnrollments.stream()
				.filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.ACTIVE)
				.map(StudentClassroom::getStudent)
				.collect(Collectors.toSet());
	}
	
	
	 
	

}
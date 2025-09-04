package com.edugreat.akademiksresource.classroom;
import java.util.List;
import java.util.Set;

import com.edugreat.akademiksresource.dto.StudentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassroomDTO(
    @Null(message = "ID must be null for creation")
    Integer id,
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    String name,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    @Positive(message = "Institution ID must be positive")
     Integer institutionId,
     
     String institutionName,
    
    @NotNull(message = "Academic year is required")
    @Min(value = 2000, message = "Year must be ≥ 2000")
    @Max(value = 2100, message = "Year must be ≤ 2100")
    Integer academicYear,
    
    @Size(max = 10, message = "Section cannot exceed 10 characters")
    String section,
    
    @NotNull(message = "Level ID is required")
    Integer levelId,
    
    @NotNull(message = "level name is required")
    String levelName,
    
    String categoryLabel,
    
    @NotNull(message = "Primary instructor ID is required")
    Integer primaryInstructorId,
    
    String primaryInstructorName,
    
   
    @PositiveOrZero(message = "Student count cannot be negative")
    Integer studentCount,
    
    @PositiveOrZero(message = "Subject count cannot be negative")
    Integer subjectCount,
    
    
    @Null(message = "Students must be managed separately")
    Set<StudentDTO> students,
    
    @Null(message = "Subjects must be assigned post-creation") 
    List<SubjectAssignmentDTO> subjectAssignments
) {
   
    
    public record SubjectAssignmentDTO(
        @NotNull Integer subjectId,
        @NotBlank String subjectName,
        @NotNull Integer instructorId
    ) {}
    
   
    public static final class Builder {
        private Integer id;
        private String name;
        private String description;
        private Integer institutionId;
        private String institutionName;
        private Integer academicYear;
        private String section;
        private Integer levelId;
        private String levelName;
        private String levelLabel;
        private Integer primaryInstructorId;
        private String primaryInstructorName;
        private Integer studentCount;
        private Integer subjectCount;
        private Set<StudentDTO> students;
        private List<SubjectAssignmentDTO> subjectAssignments;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder institutionId(Integer institutionId) {
			this.institutionId = institutionId;
			return this;
		}
        
        public Builder institutionName(String institutionName) {
        	
        	this.institutionName = institutionName;
            return this; 
        }

        public Builder academicYear(Integer academicYear) {
            this.academicYear = academicYear;
            return this;
        }

        public Builder section(String section) {
            this.section = section;
            return this;
        }

        public Builder levelId(Integer levelId) {
            this.levelId = levelId;
            return this;
        }
        
        public Builder levelName(String levelName) {
        	
        	this.levelName = levelName;
        	
        	return this;
        }
        
        public Builder categoryLabel(String label) {
        	this.levelLabel = label;
        	
        	return this;
        	
        }

        public Builder primaryInstructorId(Integer primaryInstructorId) {
            this.primaryInstructorId = primaryInstructorId;
            return this;
        }
        
        public Builder primaryInstructorName(String primaryInstructorName) {
			this.primaryInstructorName = primaryInstructorName;
			return this;
		}

        public Builder studentCount(Integer studentCount) {
            this.studentCount = studentCount;
            return this;
        }

        public Builder subjectCount(Integer subjectCount) {
            this.subjectCount = subjectCount;
            return this;
        }

        public Builder students(Set<StudentDTO> students) {
            this.students = students;
            return this;
        }

        public Builder subjectAssignments(List<SubjectAssignmentDTO> subjectAssignments) {
            this.subjectAssignments = subjectAssignments;
            return this;
        }

        public ClassroomDTO build() {
            
        	return new ClassroomDTO(
				id,
				name,
				description,
				institutionId,
				institutionName,
				academicYear,
				section,
				levelId,
				levelName,
				levelLabel,
				primaryInstructorId,
				primaryInstructorName,
				studentCount,
				subjectCount,
				students,
				subjectAssignments
			);
        }
    }

}
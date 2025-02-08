package com.edugreat.akademiksresource.assignment;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentDetailsDTO {
	
	private Integer id;
	
//	name of assignment
	@NotNull
	private String name;
	
//	ID of the admin who set the assignment
	private Integer admin;
	
	@NotNull
	private String subject;
	
//	institution that owns the assignment
	private Integer institution;


	@NotNull
	@Digits(fraction = 1, integer = 3,  message = "invalid mark for assignment")
	private Double allocatedMark;
	
//	AssignmentResource can be one or more objective questions, one or theoretical questions or none(the case where assignment is contained in an uploaded file)
	private Set<AssignmentResourceDTO> assignmentResourceDTO;
	
	
	private LocalDateTime creationDate;
	
	@Future
	private LocalDateTime submissionEnds;
	
	@NotNull
	private String category;
	
//	object or theory
	private String type;
	
	@Min(value = 1)
	private int totalQuestions;
	
	public AssignmentDetailsDTO() {}

	public AssignmentDetailsDTO(Integer id, @NotNull String name, Integer instructor, @NotNull String subject,
			Integer institution,
			@NotNull @Digits(fraction = 1, integer = 3, message = "invalid mark for assignment") Double allocatedMark,
			 LocalDateTime creationDate, @Future LocalDateTime submissionEnds,
			@NotNull String category, @Min(value = 1) int totalQuestions) {
		this.id = id;
		this.name = name;
		this.admin = instructor;
		this.subject = subject;
		this.institution = institution;
		this.allocatedMark = allocatedMark;
		this.creationDate = creationDate;
		this.submissionEnds = submissionEnds;
		this.category = category;
		this.totalQuestions = totalQuestions;
	}

	
	
	
	

}

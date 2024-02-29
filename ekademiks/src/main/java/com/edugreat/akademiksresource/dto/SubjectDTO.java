package com.edugreat.akademiksresource.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
	
	@NotEmpty(message = "subject name must not empty")
	private String subjectName;
	@NotEmpty(message = "field level must be provided")
	private String category;

}

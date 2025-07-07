package com.edugreat.akademiksresource.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

	@Min(value = 0, message = "Invalid id")
	private Integer id;
	
	@NotEmpty(message = "subject name must not empty")
	@NotNull(message = "subject name is missing")
	private String subjectName;
	
	@NotEmpty(message = "field level must be provided")
	@NotNull(message = "provide category/level for the subject")
	private String category;

}

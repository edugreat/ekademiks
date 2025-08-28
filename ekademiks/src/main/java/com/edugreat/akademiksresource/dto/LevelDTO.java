package com.edugreat.akademiksresource.dto;


import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LevelDTO {

	
	private Integer id;

	@NotNull(message = "category name is required")
	@NotBlank(message = "invalid category")
	@Length(max = 20, min = 2, message = "category must between 2 and 20 characters")
	private String category;
	
	private String categoryLabel;

}

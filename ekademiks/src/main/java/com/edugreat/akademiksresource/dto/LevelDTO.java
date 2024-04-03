package com.edugreat.akademiksresource.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LevelDTO {
	
	@Min(value = 0, message = "invalid id")
	private Integer id;
	
	@Pattern(regexp = "^(?:SENIOR|JUNIOR)$", message = "Invalid category")
	private String category;

}

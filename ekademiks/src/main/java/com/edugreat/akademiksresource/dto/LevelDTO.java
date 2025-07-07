package com.edugreat.akademiksresource.dto;

import jakarta.validation.constraints.Min;
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

	@Min(value = 0, message = "invalid id")
	private Integer id;

	@NotNull(message = "category name is required")
	@NotBlank(message = "invalid category")
	private String category;

}

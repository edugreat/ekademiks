package com.edugreat.akademiksresource.util;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Utility class for the Options object
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionUtil {

	@NotBlank(message = "option must not be null or blank")
	private String text;
	@NotBlank(message = "option letter must neither be null not blank")
	@Pattern(regexp = "^[A-E]$", message = "option aside A-E not acceptable")
	private String letter;

}

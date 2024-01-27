package com.edugreat.akademiksresource.util;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

//Utility class for the Options object
public class OptionUtil {
	
	@NotBlank(message = "option must not be null or blank")
	private String text;
	@NotBlank(message = "option letter must neither be null not blank")
	@Pattern(regexp = "^[A-E]$", message = "option aside A-E not acceptable")
	private String letter;
	public OptionUtil(String text, String letter) {
		this.text = text;
		this.letter = letter;
	}
	public String getText() {
		return text;
	}
	public String getLetter() {
		return letter;
	}
	
	

}

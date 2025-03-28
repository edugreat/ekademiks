package com.edugreat.akademiksresource.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import com.edugreat.akademiksresource.util.OptionUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Data transfer object for the Question object
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {

	
	
	
	@Digits(integer = 2, fraction = 0, message = "Question number not supported!")
	@Min(value = 1, message = "Question number must be greater than 0")
	@Max(value = 50, message = "Question number must not be greater than 50")
	private int questionNumber;

	@NotEmpty(message = "expected question not found")
	private String question;

	@NotEmpty(message = "Expected property answer not found")
	private String answer;

	@Valid
	@NotEmpty(message = "Options have not been provided")
	private List<OptionUtil> options = new ArrayList<>();

	@Override
	public int hashCode() {

		return Objects.hash(getQuestionNumber());
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		QuestionDTO that = (QuestionDTO) o;
		return this.questionNumber == that.getQuestionNumber();

	}

	public void addOption(OptionUtil option) {

		this.options.add(option);
	}

}

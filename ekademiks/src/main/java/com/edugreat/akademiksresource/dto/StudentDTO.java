package com.edugreat.akademiksresource.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Data transfer object for the Student object
public class StudentDTO {

	@Min(value = 0, message = "id must be greater than 0")
	private Integer id;
	
	@NotBlank(message = "first name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	private String firstName;

	@NotBlank(message = "last name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	private String lastName;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_+=<>?]).{8,}$")
	@NotBlank(message = "Password required")
	private String password;

	@Pattern(regexp = "^(?:\\+234|\\b0)([789]\\d{9})$", message = "Unsupported mobile number")
	@NotBlank(message = "mobile number required")
	private String mobileNumber;

	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "invalid email type")
	private String email;

	public StudentDTO(String firstName, String lastName, String password, String phoneNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.mobileNumber = phoneNumber;
	}

}

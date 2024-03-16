package com.edugreat.akademiksresource.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.edugreat.akademiksresource.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public abstract class AppUserDTO {
	

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
	@JsonIgnore
	private String password;

	@Pattern(regexp = "^(?:\\+234|\\b0)([789]\\d{9})$", message = "Unsupported mobile number")
	@NotBlank(message = "mobile number required")
	private String mobileNumber;

	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "invalid email type")
	private String email;
	
	private int statusCode;
	private String token;
	private String signInErrorMessage;
	
	
	
	
	private Set<String> roles = new HashSet<>();
	
	public AppUserDTO(String firstName, String lastName, String password,String email) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email =email;
	}
	
public AppUserDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.mobileNumber = mobileNumber;
	}

       
     //each child class should implement this method to populate the user roles specific to the type of the user
     public abstract Set<Roles> getUserRoles();
	

	

}

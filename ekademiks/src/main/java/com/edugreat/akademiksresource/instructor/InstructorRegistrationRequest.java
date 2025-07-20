package com.edugreat.akademiksresource.instructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record InstructorRegistrationRequest(
		
		  @NotBlank(message = "first name must be provided")
		    @Pattern(regexp = "^[a-zA-Z]{2,}$")
		    @NotNull(message = "first name is missing")
		    String firstName, 

		    @NotBlank(message = "last name must be provided")
		    @NotNull(message = "last name is missing")
		    @Pattern(regexp = "^[a-zA-Z]{2,}$")
		    String lastName, 

		    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "invalid email type")
		   @NotNull(message = "email is missing")
		    @NotBlank(message = "Email required")
		    String email,

		    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
		    @NotBlank(message = "Password required")
		    @NotNull(message = "Password is missing")
		    String password,

		    @Pattern(
		    	    regexp = "^(\\+234|0)[7-9][0-1]\\d{8}$",
		    	    message = "Phone number format is invalid"
		    	)
		    @NotNull(message = "Phone number is missing")
		    	String mobileNumber,
		    	
		    	@NotNull(message = "Provide your institution")
		    	Integer institution

		
		) 

{

}

package com.edugreat.akademiksresource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// provide object for taking student record for registering students with an institution
public record StudentRecord(
		
		  @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "invalid email type")
		   @NotNull(message = "email is missing")
		    @NotBlank(message = "Email and or password error")
		    String email,
		
		    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
		    @NotBlank(message = "Password required")
		    @NotNull(message = "Email and or password error")
		    String password

		
		) {}

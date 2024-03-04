package com.edugreat.akademiksresource.auth;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable{
	
	
	
	private static final long serialVersionUID = 1L;

	@Pattern(regexp = "^(?:\\+234|\\b0)([789]\\d{9})$", message = "Unsupported mobile number")
	private String mobileNumber;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_+=<>?]).{8,}$", message = "Password must be at least eight characters")
	private String password;

}

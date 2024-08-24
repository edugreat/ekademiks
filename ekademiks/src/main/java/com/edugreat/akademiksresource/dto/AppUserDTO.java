package com.edugreat.akademiksresource.dto;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public  class AppUserDTO {
	
	public AppUserDTO() {}

	@Min(value = 0, message = "id must be greater than 0")
	@JsonView(UserView.class)
	private Integer id;
	
	@NotBlank(message = "first name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	@JsonView(UserView.class)
	private String firstName;

	@NotBlank(message = "last name must be provided")
	@Pattern(regexp = "^[a-zA-Z]{2,}$")
	@JsonView(UserView.class)
	private String lastName;

	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
	@NotBlank(message = "Password required")
	private String password;

	@Pattern(regexp = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$", message = "Unsupported mobile number")
	@JsonView(UserView.class)
	private String mobileNumber;

	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "invalid email type")
	@JsonView(UserView.class)
	private String email;
	
	@JsonView(UserView.SigninView.class)
	private int statusCode;
	
	@JsonView(UserView.SigninView.class)
	private String token;
	
	@JsonView(UserView.SigninView.class)
	private String signInErrorMessage;
	

	
	@JsonView(UserView.class)
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
//     public Set<Roles> getUserRoles(){
//    	 return null;
//     }
	

	

}

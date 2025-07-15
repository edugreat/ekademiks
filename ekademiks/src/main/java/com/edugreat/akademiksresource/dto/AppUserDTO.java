package com.edugreat.akademiksresource.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.edugreat.akademiksresource.instructor.InstructorDTO;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@JsonTypeInfo(
		 use = JsonTypeInfo.Id.NAME,
		 property = "type",
		 visible = true
		 
		)
@JsonSubTypes({
	@JsonSubTypes.Type(value = StudentDTO.class, name = "student"),
	@JsonSubTypes.Type(value = AdminsDTO.class, name = "admin"),
    @JsonSubTypes.Type(value = InstructorDTO.class, name = "instructor")	
})
public class AppUserDTO {
	public AppUserDTO() {}
	
	
protected AppUserDTO(String type) {
		
		this.type = type;
	}
	

	@Min(value = 0, message = "id must be greater than 0")
	@JsonView(UserView.class)
	private Integer id;
	
	@JsonProperty("type")
	private String type;
	
	@JsonView(UserView.class)
	private boolean isGroupMember;

	@JsonView(UserView.class)
	private String firstName;

	
	@JsonView(UserView.class)
	private String lastName;

	
	private String password;

	
	@JsonView(UserView.class)
	private String mobileNumber;


	@JsonView(UserView.class)
	private String email;
	
	@JsonView(UserView.class)
	private LocalDateTime accountCreationDate;

	@JsonView(UserView.SigninView.class)
	private int statusCode;

	@JsonView(UserView.SigninView.class)
	private String accessToken;
	
	@JsonView(UserView.SigninView.class)
	private String refreshToken;

	@JsonView(UserView.SigninView.class)
	private String signInErrorMessage;
	
	@JsonView(UserView.SigninView.class)
	private String cachingKey;
	

	@JsonView(UserView.class)
	private Set<String> roles = new HashSet<>();

	public AppUserDTO(String firstName, String lastName, String password, String email) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
	}

	public AppUserDTO(String firstName, String lastName, String password, String email, String mobileNumber) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.mobileNumber = mobileNumber;
	}
	
	
	public String getType() {
		
		return type;
	}
	
	public void setIsGroupMember(boolean isAMember) {
		
		this.isGroupMember = isAMember;
		
	}



}

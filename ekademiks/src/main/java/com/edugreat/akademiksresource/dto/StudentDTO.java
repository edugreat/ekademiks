package com.edugreat.akademiksresource.dto;

import java.util.Set;

import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

//Data transfer object for the Student object
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO extends AppUserDTO {
	
//	depicts SENIOR or JUNIOR status for the given student
	@JsonView(UserView.class)
	private String status;
	
//	property specifically used to enable json de-serialize logged in user properly
	private String type = "student";

	public StudentDTO() {
		super();
	}

	public StudentDTO(String firstName, String lastName, String password, String email) {
		super(firstName, lastName, password, email);

	}

	public StudentDTO(String firstName, String lastName, String password, String email, String mobileNumber) {
		super(firstName, lastName, password, email, mobileNumber);

	}

	@Override
	public Set<String> getRoles() {

		return Set.of("Student");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String  category) {
		this.status = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}

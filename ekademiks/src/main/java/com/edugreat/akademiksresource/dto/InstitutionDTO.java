package com.edugreat.akademiksresource.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class InstitutionDTO {
	
	private Integer id;
	
	@NotNull(message = "Missing name for institution")
	@NotEmpty(message = "Institution's name is required")
	private String name;
	
	private LocalDateTime  createdOn;
	
	@NotNull(message = "Please indicate your state")
	@NotEmpty(message = "Your state is missing")
	private String state;
	
	@NotNull(message = "Local government not found")
	@NotEmpty(message = "Please indicate local government")
	private String localGovt;
	
	@Min(value = 1, message = "Could not identify instructor")
	private Integer createdBy;
	
	private Integer studentPopulation;
	
	public InstitutionDTO() {}

	public InstitutionDTO(
			Integer id,
			@NotNull(message = "Please indicate your state")
	        @NotEmpty(message = "Your state is missing") String name, 
	        Integer studentPopulation,
	        String state,
	        String localGovt,
	        int createdBy,
	        LocalDateTime createdOn
	        
			) {
		
		
		this.id = id;
		this.state = state;
		this.localGovt = localGovt;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
		this.name = name;
		this.studentPopulation = studentPopulation;
	}
	
	
	
	

}

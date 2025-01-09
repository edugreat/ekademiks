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
	
	@NotNull
	@NotEmpty
	private String name;
	
	private LocalDateTime  createdOn;
	
	@NotNull
	@NotEmpty
	private String state;
	
	@NotNull
	@NotEmpty
	private String localGovt;
	
	@Min(value = 1)
	private Integer createdBy;

	public InstitutionDTO(Integer id, @NotNull @NotEmpty String name) {
		
		this.id = id;
		this.name = name;
	}
	
	
	
	

}

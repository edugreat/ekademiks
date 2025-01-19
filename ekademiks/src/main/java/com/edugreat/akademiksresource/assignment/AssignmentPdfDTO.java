package com.edugreat.akademiksresource.assignment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentPdfDTO {
	
	private Integer id;
	
	@NotNull
	private String fileName;
	
	@NotNull
	private String fileType;
	
	@NotNull
	private byte[] fileByte;
	
	public AssignmentPdfDTO() {}

	public AssignmentPdfDTO(Integer id, @NotNull String fileName, @NotNull String fileType, @NotNull byte[] fileByte) {
		this.id = id;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileByte = fileByte;
	}

	public AssignmentPdfDTO(@NotNull String fileName, @NotNull String fileType, @NotNull byte[] fileByte) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileByte = fileByte;
	}
	
	
	
	

}

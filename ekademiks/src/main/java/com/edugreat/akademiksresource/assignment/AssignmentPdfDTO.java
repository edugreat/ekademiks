package com.edugreat.akademiksresource.assignment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



public class AssignmentPdfDTO extends AssignmentResourceDTO {
	
	
	
	@NotNull
	private String fileName;
	
	@NotNull
	private String fileType;
	
	private String type = "pdf";
	
	@Override
	public String getType() {
		
		return  type;
	}


	@NotNull
	private byte[] fileByte;
	
	public AssignmentPdfDTO() {}

	
	public AssignmentPdfDTO(@NotNull String fileName, @NotNull String fileType, @NotNull byte[] fileByte) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileByte = fileByte;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public byte[] getFileByte() {
		return fileByte;
	}


	public void setFileByte(byte[] fileByte) {
		this.fileByte = fileByte;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
	

}

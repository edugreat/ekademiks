package com.edugreat.akademiksresource.assignment;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("pdf")
public class AssignmentPDF extends AssignmentResource {
	

	

	public AssignmentPDF() {
		super();
		
	}


	public AssignmentPDF(String fileName, String fileType, byte[] fileByte) {
		super(fileName, fileType, fileByte);
		
	}


	@Override
	public String getType() {
		
		return "application/pdf";
	}
	
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    Objectives that = (Objectives) o;
	    return super.getId() != null && super.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}


	
}

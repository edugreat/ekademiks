package com.edugreat.akademiksresource.assignment;

import java.util.Set;

public interface AssignmentInterface {
	
//	sets assignment with PDF files
	Integer setAssignment(AssignmentDetailsDTO details, Set<AssignmentPdfDTO> pdfs);
	
//	sets assignment without PDF files
	Integer setAssignment(AssignmentDetailsDTO details);
	
//	used to determines if the admin user whose ID is referenced has any registered institutions for which they can be allowed
//	to post assignments
	boolean hasInstitution(Integer adminId);
	
	

}

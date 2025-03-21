package com.edugreat.akademiksresource.assignment;

import java.util.Set;

public interface AssignmentInterface {
	
//	sets assignment with PDF files
	Integer setAssignment(AssignmentDetailsDTO details, Set<AssignmentPdfDTO> pdfs);
	
//	sets assignment without PDF files
	Integer setAssignment(AssignmentDetailsDTO details);


	AssignmentDetailsDTO getAssignmentDetails(Integer assignmentId);

	

}

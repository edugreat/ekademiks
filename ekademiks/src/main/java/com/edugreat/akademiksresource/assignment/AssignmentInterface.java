package com.edugreat.akademiksresource.assignment;

import java.util.Set;

public interface AssignmentInterface {
	
//	sets assignment with PDF files
	Integer setAssignment(AssignmentDetailsDTO details, Set<AssignmentPdfDTO> pdfs);
	
//	sets assignment without PDF files
	Integer setAssignment(AssignmentDetailsDTO details);


	AssignmentDetailsDTO getAssignmentDetails(Integer assignmentId);
	
//	fetches assignment resource using the assignment details ID and the assignment type
	 <T extends AssignmentResourceDTO> Set<T> getAssignmentResource(Integer assignmentDetailsId);

	

}

package com.edugreat.akademiksresource.assignment;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;

import jakarta.transaction.Transactional;

@Service
public class AssignmentService implements AssignmentInterface {

	@Autowired
	private AssignmentDetailsDao assignmentDetailsDao;

	@Autowired
	private AdminsDao adminsDao;

	@Autowired
	private InstitutionDao institutionDao;

	@Override
	@Transactional
	public Integer setAssignment(AssignmentDetailsDTO details, Set<AssignmentPdfDTO> pdfs) {
		
//		confirm such assignment does not exist for the institution
		final boolean conflicts = assignmentDetailsDao.existsConflicts(details.getInstitution(), details.getCategory(),
				details.getName());
		
		
		
		

		if (conflicts)
			throw new IllegalArgumentException("duplicate assignment");

//		get the admin's ID
		var admin = adminsDao.findById(details.getAdmin())
				.orElseThrow(() -> new IllegalArgumentException("Admin not found"));
		
		

//		get the institution
		var institution = institutionDao.findById(details.getInstitution())
				.orElseThrow(() -> new IllegalArgumentException("institution not found"));
		
		

		AssignmentDetails assignmentDetails = new AssignmentDetails(admin, details.getSubject(), institution,
				details.getAllocatedMark(), details.getSubmissionEnds(), details.getName(), details.getCategory(),
				details.getType(), details.getTotalQuestions());
		
		

		

		for (var _pdf : pdfs) {

			var pdf = new AssignmentPDF(_pdf.getFileName(), _pdf.getFileType(), _pdf.getFileByte());
			assignmentDetails.addAssignment(pdf);
		}
		
		var saved =  assignmentDetailsDao.save(assignmentDetails);
		
		
		
		

		

//		returns the ID to the client
		return saved.getId();

	}

	@Override
	@Transactional
	public Integer setAssignment(AssignmentDetailsDTO details) {

		Integer assignmentId = null;
		
//		checks there is no duplicate attempt
		final boolean conflicts = assignmentDetailsDao.existsConflicts(details.getInstitution(), details.getCategory(),
				details.getName());

		if (conflicts)
			throw new IllegalArgumentException("duplicate assignment");

//		get the admin's ID
		var admin = adminsDao.findById(details.getAdmin())
				.orElseThrow(() -> new IllegalArgumentException("Admin not found"));

//		get the institution
		var institution = institutionDao.findById(details.getInstitution())
				.orElseThrow(() -> new IllegalArgumentException("institution not found"));

		AssignmentDetails assignmentDetails = new AssignmentDetails(admin, details.getSubject(), institution,
				details.getAllocatedMark(), details.getSubmissionEnds(), details.getName(), details.getCategory(),
				details.getType(), details.getTotalQuestions());

			
	
		
//		confirm the type of assignment posted
		if(details.getType().equalsIgnoreCase("objectives")) {
			
			
			for(var resourceDTO: details.getAssignmentResourceDTO()) {
				
				var objAssignment= new Objectives(
						((ObjectiveAssignmentDTO)resourceDTO).getAnswer(),
						
						((ObjectiveAssignmentDTO)resourceDTO).get_index(), 
						
						((ObjectiveAssignmentDTO)resourceDTO).getProblem());
				
				objAssignment.addOptions(((ObjectiveAssignmentDTO)resourceDTO).getOptions());
			
				assignmentDetails.addAssignment(objAssignment);
			
			}
			
			
			assignmentId = assignmentDetailsDao.saveAndFlush(assignmentDetails).getId();
			
			System.out.println("ID returned: ");
			System.out.println(assignmentId);
		}else if(details.getType().equalsIgnoreCase("theory")) {
			
			
			for(var resourceDTO : details.getAssignmentResourceDTO()) {
				
				var theoryAssignment = new Theories(
						((TheoreticalAssigDTO)resourceDTO).getAnswer(), 
						((TheoreticalAssigDTO)resourceDTO).get_index(),
						((TheoreticalAssigDTO)resourceDTO).getProblem());
				
				assignmentDetails.addAssignment(theoryAssignment);
						
				
			}
			
			assignmentId = assignmentDetailsDao.saveAndFlush(assignmentDetails).getId();
			
			System.out.println("ID returned: ");
			System.out.println(assignmentId);
		}
		
		
		
		
		
		

		
	

//		save and return the ID of assignment for notification purpose
		return assignmentId;
	
	}

	@Override
	public AssignmentDetailsDTO getAssignmentDetails(Integer assignmentId) {
		
		var assignmentDetails = assignmentDetailsDao.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found!"));
		
		AssignmentDetailsDTO dto = mapToAssignmentDetailsDTO(assignmentDetails);
		
		dto.setType(assignmentDetails.getType());
		
		return dto;
	}

	private AssignmentDetailsDTO mapToAssignmentDetailsDTO(AssignmentDetails details) {
		
		AssignmentDetailsDTO dto = new AssignmentDetailsDTO(details.getId(), 
				details.getName(),details.getInstructor().getId(), 
				details.getSubject(), details.getInstitution().getId(), 
				details.getAllocatedMark(), details.getCreationDate(), 
				details.getSubmissionEnds(), details.getCategory(), 
				details.getTotalQuestions());
		
		return null;
	}
	

}

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
		
		System.out.println("returned value");
		System.out.println(conflicts);
		
		

		if (conflicts)
			throw new IllegalArgumentException("duplicate assignment");

//		get the admin's ID
		var admin = adminsDao.findById(details.getAdmin())
				.orElseThrow(() -> new IllegalArgumentException("Admin not found"));
		
		System.out.println("after 1");

//		get the institution
		var institution = institutionDao.findById(details.getInstitution())
				.orElseThrow(() -> new IllegalArgumentException("institution not found"));
		System.out.println("after 2");
		

		AssignmentDetails assignmentDetails = new AssignmentDetails(admin, details.getSubject(), institution,
				details.getAllocatedMark(), details.getSubmissionEnds(), details.getName(), details.getCategory(),
				details.getType(), details.getTotalQuestions());
		
		System.out.println("after 3");

		

		for (var _pdf : pdfs) {

			var pdf = new AssignmentPDF(_pdf.getFileName(), _pdf.getFileType(), _pdf.getFileByte());
			assignmentDetails.addAssignment(pdf);
		}
		
		var saved =  assignmentDetailsDao.save(assignmentDetails);
		
		System.out.println("saved: "+saved.getId());
		
		

		

//		returns the ID to the client
		return saved.getId();

	}

	@Override
	@Transactional
	public Integer setAssignment(AssignmentDetailsDTO details) {

//		checks there is no duplicate attempt
		final boolean conflicts = assignmentDetailsDao.existsConflicts(details.getInstitution(), details.getCategory(),
				details.getName());

		if (conflicts)
			throw new IllegalArgumentException("duplicate assignment");

//		get the admin's ID
		var admin = adminsDao.findById(details.getAdmin())
				.orElseThrow(() -> new IllegalArgumentException("Admin not found"));

//		get the institution
		var institution = institutionDao.findById(null)
				.orElseThrow(() -> new IllegalArgumentException("institution not found"));

		AssignmentDetails assignmentDetails = new AssignmentDetails(admin, details.getSubject(), institution,
				details.getAllocatedMark(), details.getSubmissionEnds(), details.getName(), details.getCategory(),
				details.getType(), details.getTotalQuestions());

		Set<AssignmentResourceDTO> dtos = details.getAssignmentResourceDTO();
		
		

		
		for(var _dto : dtos) {
			
			if(_dto.getType().equalsIgnoreCase("objectives")) {
				
				System.out.println("objectives question");
				
				var obj = (ObjectiveAssignmentDTO)_dto;
				
				var assignment = new Objectives(obj.getAnswer(), obj.get_index(), obj.getProblem());
				
				assignment.addOptions(obj.getOptions());
				
				
				assignmentDetails.addAssignment(assignment);
				
				
				
			}else if(_dto.getType().equalsIgnoreCase("theory")) {
				
				System.out.println("theory question");
				
				var theory = (TheoreticalAssigDTO)_dto;
				
				var assignment = new Theories(theory.getAnswer(), theory.get_index(), theory.getProblem());	
				
				assignmentDetails.addAssignment(assignment);
				
			}
			

		
		
	}

	
		return assignmentDetailsDao.save(assignmentDetails).getId();
	
	}
	

}

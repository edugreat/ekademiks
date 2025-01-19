package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;

import jakarta.transaction.Transactional;

public class AssignmentService implements AssignmentInterface {
	
	@Autowired
	private AssignmentDetailsDao assignmentDetailsDao;
	
	@Autowired
	private AssignmentPDFDao pdfsDao;
	
	@Autowired
	private AdminsDao adminsDao;
	
	@Autowired
	private InstitutionDao institutionDao;

	@Override
	@Transactional
	public Integer setAssignment(AssignmentDetailsDTO details, Set<AssignmentPdfDTO> pdfs) {
		
		
//		confirm such assignment does not exist for that institution
		final boolean conflicts = assignmentDetailsDao.existConflicts(details.getId(), details.getName());
		
		if(conflicts) throw new IllegalArgumentException("duplicate assignment");
		
		
//		get the admin's ID
		var admin = adminsDao.findById(details.getAdmin()).orElseThrow(() -> new IllegalArgumentException("Admin not found"));
		
//		get the institution
		var  institution = institutionDao.findById(null).orElseThrow(() -> new IllegalArgumentException("institution not found"));
		
		
		AssignmentDetails assignmentDetails = new AssignmentDetails(
				admin, 
				details.getSubject(), 
				institution, 
				details.getAllocatedMark(), 
				details.getSubmissionEnds(), 
				details.getName(), 
				details.getCategory(), 
				details.getType(), 
				details.getTotalQuestions());
		
		Set<AssignmentPDF> _pdfs = new HashSet<>();
		
		for(var pdf : pdfs) {
			
			var _pdf = new AssignmentPDF(pdf.getFileName(), pdf.getFileType(), pdf.getFileByte());
			_pdfs.add(_pdf);
		}
		
		assignmentDetails.setAssignmentPDFs(_pdfs);
		
		
//		returns the ID to the client
		return assignmentDetailsDao.save(assignmentDetails).getId();
		
		

	}

}

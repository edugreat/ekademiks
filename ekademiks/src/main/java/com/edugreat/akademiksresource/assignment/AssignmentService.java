package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
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
		final boolean conflicts = assignmentDetailsDao.existConflicts(details.getInstitution(), details.getCategory(),
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

		Set<AssignmentPDF> _pdfs = new HashSet<>();

		for (var pdf : pdfs) {

			var _pdf = new AssignmentPDF(pdf.getFileName(), pdf.getFileType(), pdf.getFileByte());
			_pdfs.add(_pdf);
		}

		assignmentDetails.setAssignmentPDFs(_pdfs);

//		returns the ID to the client
		return assignmentDetailsDao.save(assignmentDetails).getId();

	}

	@Override
	@Transactional
	public Integer setAssignment(AssignmentDetailsDTO details) {

//		checks there is no duplicate attempt
		final boolean conflicts = assignmentDetailsDao.existConflicts(details.getInstitution(), details.getCategory(),
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

		Set<AssignmentDTO> dtos = details.getAssignmentDTO();

//		get the question type
		switch (details.getType().toLowerCase()) {

		case "theory":

			for (var dto : dtos) {

				Assignment theory = new TheoryAssignment(dto.get_index(), dto.getProblem(), dto.getAnswer());
				assignmentDetails.addAssignment(theory);

			}

			break;

		case "objectives":

			for (var dto : dtos) {

				Assignment obj = new ObjAssignment(dto.get_index(), dto.getProblem(), dto.getAnswer());
				((ObjAssignment) obj).setOptions(((ObjAssigDTO) dto).getOptions());

			}

			break;

		default:
			throw new IllegalArgumentException("Unexpected value: " + details.getType().toLowerCase());
		}

		return assignmentDetailsDao.save(assignmentDetails).getId();
	}

	
	

}

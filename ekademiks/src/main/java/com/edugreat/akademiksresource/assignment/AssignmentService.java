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

		var saved = assignmentDetailsDao.save(assignmentDetails);

		System.out.println("saved: " + saved.getId());

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
		var institution = institutionDao.findById(details.getInstitution())
				.orElseThrow(() -> new IllegalArgumentException("institution not found"));

		AssignmentDetails assignmentDetails = new AssignmentDetails(admin, details.getSubject(), institution,
				details.getAllocatedMark(), details.getSubmissionEnds(), details.getName(), details.getCategory(),
				details.getType(), details.getTotalQuestions());

		Set<AssignmentResourceDTO> dtos = details.getAssignmentResourceDTO();

		if (details.getType().equalsIgnoreCase("objectives")) {

			for (var _dto : dtos) {


				var assignment = new Objectives(_dto.getAnswer(), _dto.get_index(), _dto.getProblem());

				assignment.addOptions(_dto.getOptions());

				assignmentDetails.addAssignment(assignment);

			}

		} else if (details.getType().equalsIgnoreCase("theory")) {

			for (var _dto : dtos) {

				
				
				var assignment = new Theories(_dto.getAnswer(), _dto.get_index(), _dto.getProblem());

				assignmentDetails.addAssignment(assignment);

			}

		}

		return assignmentDetailsDao.save(assignmentDetails).getId();

	}

}

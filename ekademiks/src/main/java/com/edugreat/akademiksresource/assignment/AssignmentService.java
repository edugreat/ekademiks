package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.config.RedisValues;
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

	@Autowired
	private CacheManager cacheManager;

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

		var saved = assignmentDetailsDao.save(assignmentDetails);

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
		if (details.getType().equalsIgnoreCase("objectives")) {

			for (var resourceDTO : details.getAssignmentResourceDTO()) {

				var objAssignment = new Objectives(((ObjectiveAssignmentDTO) resourceDTO).getAnswer(),

						((ObjectiveAssignmentDTO) resourceDTO).get_index(),

						((ObjectiveAssignmentDTO) resourceDTO).getProblem());

				objAssignment.addOptions(((ObjectiveAssignmentDTO) resourceDTO).getOptions());

				assignmentDetails.addAssignment(objAssignment);

			}

			assignmentId = assignmentDetailsDao.saveAndFlush(assignmentDetails).getId();

		} else if (details.getType().equalsIgnoreCase("theory")) {

			for (var resourceDTO : details.getAssignmentResourceDTO()) {

				var theoryAssignment = new Theories(((TheoreticalAssigDTO) resourceDTO).getAnswer(),
						((TheoreticalAssigDTO) resourceDTO).get_index(),
						((TheoreticalAssigDTO) resourceDTO).getProblem());

				assignmentDetails.addAssignment(theoryAssignment);

			}

			assignmentId = assignmentDetailsDao.saveAndFlush(assignmentDetails).getId();

		}

//		save and return the ID of assignment for notification purpose
		return assignmentId;

	}

	@Override
	public AssignmentDetailsDTO getAssignmentDetails(Integer assignmentId) {

		
		Cache cache = cacheManager.getCache(RedisValues.ASSIGNMENT_DETAILS);
		if (cache != null && cache.get(assignmentId) != null) {

			return getCachedAssignment(assignmentId, cache);

		}

		var assignmentDetails = assignmentDetailsDao.findById(assignmentId)
				.orElseThrow(() -> new RuntimeException("Assignment not found!"));

		cacheAssignment(assignmentId, assignmentDetails);

		return mapToAssignmentDetailsDTO(assignmentDetails);

	}

//	returns previously cached assignment
	private AssignmentDetailsDTO getCachedAssignment(Integer assignmentId, Cache cache) {
		Cache.ValueWrapper valueWrapper = cache.get(assignmentId);

		return (AssignmentDetailsDTO) valueWrapper.get();

	}

//	cache the assignment
	private void cacheAssignment(Integer detailsId, AssignmentDetails assignmentDetails) {
		Cache assignmentCache = cacheManager.getCache(RedisValues.ASSIGNMENT_DETAILS);
		assignmentCache.put(detailsId, mapToAssignmentDetailsDTO(assignmentDetails));

//		create a key for the assignment resource
		final String resourceKey = detailsId + "_resource";

//  get the actual assignment
		String assignmentType = assignmentDetails.getType().substring(0, 3);
		switch (assignmentType) {
		case "obj":

			Set<Objectives> objs = assignmentDetailsDao.getAssignment(detailsId);

			Set<ObjectiveAssignmentDTO> dtos = new HashSet<>();
			for (Objectives obj : objs) {

				dtos.add(mapToObjResourceDTO(obj));
			}

			assignmentCache.put(resourceKey, dtos);
			break;

		case "the":

			Set<Theories> theories = assignmentDetailsDao.getAssignment(detailsId);

			Set<TheoreticalAssigDTO> _theories = new HashSet<>();

			for (Theories theory : theories) {

				_theories.add(mapToTheoryResourceDTO(theory));
			}

			assignmentCache.put(resourceKey, _theories);

			break;

		case "pdf":

			Set<AssignmentPDF> pdfs = assignmentDetailsDao.getAssignment(detailsId);

			Set<AssignmentPdfDTO> pdfDTOs = new HashSet<>();

			for (AssignmentPDF pdf : pdfs) {

				pdfDTOs.add(mapToPdfResourceDTO(pdf));

			}
			assignmentCache.put(resourceKey, pdfDTOs);
			break;
		}

	}

	private AssignmentPdfDTO mapToPdfResourceDTO(AssignmentPDF pdf) {

		return new AssignmentPdfDTO(pdf.getFileName(), pdf.getFileType(), pdf.getFileByte());
	}

	private AssignmentDetailsDTO mapToAssignmentDetailsDTO(AssignmentDetails details) {

		AssignmentDetailsDTO dto = new AssignmentDetailsDTO(details.getId(), details.getName(),
				details.getInstructor().getId(), details.getSubject(), details.getInstitution().getId(),
				details.getAllocatedMark(), details.getCreationDate(), details.getSubmissionEnds(),
				details.getCategory(), details.getTotalQuestions());

		dto.setType(details.getType());

		return dto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AssignmentResourceDTO> Set<T> getAssignmentResource(Integer assignmentDetailsId) {

//		check previous caches for the given assignment
		Cache cache = cacheManager.getCache(RedisValues.ASSIGNMENT_DETAILS);

		String key = assignmentDetailsId + "_resource";

		Cache.ValueWrapper wrapper = cache.get(key);

		Set<T> dto = new HashSet<>((List<T>) wrapper.get());
		

		return dto;

	}

	private ObjectiveAssignmentDTO mapToObjResourceDTO(Objectives assignment) {

		return new ObjectiveAssignmentDTO(assignment.getProblem(), assignment.getAnswer(),
				new HashSet<>(assignment.getOptions()));

	}

	private TheoreticalAssigDTO mapToTheoryResourceDTO(Theories assignment) {

		return new TheoreticalAssigDTO(assignment.getProblem(), assignment.getAnswer());
	}

}

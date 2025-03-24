package com.edugreat.akademiksresource.assignment;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.edugreat.akademiksresource.amqp.notification.broadcast.NotificationBroadcast;
import com.edugreat.akademiksresource.contract.NotificationInterface;
import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.model.AssessmentUploadNotification;
import com.edugreat.akademiksresource.model.Institution;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

	@Autowired
	private AssignmentInterface _interface;

	@Autowired
	private AssignmentDetailsDao assignmentDetailsDao;

	@Autowired
	NotificationBroadcast notification;

	@Autowired
	private NotificationInterface notificationInterface;

//	post assignment details along with pdf 
	@PostMapping(path = "/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Object> postAssignment(@RequestPart("details") AssignmentDetailsDTO details,
			@RequestPart("pdf") MultipartFile[] files) {

		Integer detailsId = null;

		try {

			detailsId = _interface.setAssignment(details, processFiles(files));

			sendInstantNotification(assignmentCandidates(detailsId, details.getCategory()), detailsId);
		} catch (Exception e2) {

			System.out.println(e2.getMessage());
			return new ResponseEntity<Object>(e2.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Object>(detailsId, HttpStatus.OK);
	}

//	helper method that processes the assignment files
	private Set<AssignmentPdfDTO> processFiles(MultipartFile[] files) throws IOException {

		Set<AssignmentPdfDTO> pdfs = new HashSet<>();

		for (MultipartFile file : files) {

			AssignmentPdfDTO pdf = new AssignmentPdfDTO(file.getOriginalFilename(), file.getContentType(),
					file.getBytes());

			pdfs.add(pdf);

		}

		return pdfs;

	}

//	post assignment with no pdf
	@PostMapping
	public ResponseEntity<Object> postAssignment(@RequestBody @Valid AssignmentDetailsDTO assignmentDetails) {

		Integer detailsId = null;

		try {

//			returns assignment ID
			detailsId = _interface.setAssignment(assignmentDetails);

//		send instant notifications to the concerned students
			sendInstantNotification(assignmentCandidates(detailsId, assignmentDetails.getCategory()), detailsId);

		} catch (Exception e) {

			return new ResponseEntity<Object>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Object>(detailsId, HttpStatus.OK);
	}

//	retrieve details for a give assignment
	@GetMapping("/details")
	public ResponseEntity<Object> getAssignmentDetails(@RequestParam String id) {

		try {

		} catch (Exception e) {

			return ResponseEntity.ok(_interface.getAssignmentDetails(Integer.parseInt(id)));
		}

		return ResponseEntity.badRequest().build();
	}

//	returns a list of student's ID for who should take the assignment referenced by the given assignmentId
	private List<String> assignmentCandidates(Integer assignmentId, String category) {

//		get institution
		Institution institution = assignmentDetailsDao.getInstitution(assignmentId);

//		filter the concerned students
		List<String> candidateIds = institution.getStudentList().stream()
				.filter(student -> student.getStatus().equalsIgnoreCase(category)).map(x -> x.getId().toString())
				.collect(Collectors.toList());

		return candidateIds;
	}

	private void sendInstantNotification(List<String> candidateIds, Integer assignmentId) {

		NotificationRequestDTO dto = new NotificationRequestDTO("assignment", "Uploaded Assignment", assignmentId,
				candidateIds);

//		create a new AssessmentUpload object.
//		second method argument is of no importance here
		AssessmentUploadNotification assignmentNotification = notificationInterface.postAssessmentNotification(dto,
				null);

		notification.sendInstantNotification(assignmentNotification);

	}

}

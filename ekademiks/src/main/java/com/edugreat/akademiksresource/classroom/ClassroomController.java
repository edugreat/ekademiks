package com.edugreat.akademiksresource.classroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.classroom.details.ClassroomFullDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSpecificDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSummarizedDetails;
import com.edugreat.akademiksresource.classroom.details.InstructorBasicDetails;
import com.edugreat.akademiksresource.classroom.details.StudentBasicDetails;
import com.edugreat.akademiksresource.classroom.service.ClassroomInterface;
import com.edugreat.akademiksresource.util.ApiResponseObject;
import com.edugreat.akademiksresource.util.SubjectAssignmentRequest;
import com.edugreat.akademiksresource.util.ValidatorService;



@RestController
@RequestMapping(path = "/classrooms")
public class ClassroomController {
	
	
	
	
	private final ClassroomInterface _interface;
	private final ValidatorService validatorService;

	public ClassroomController(ClassroomInterface _interface, ValidatorService validatorService) {
		this._interface = _interface;
		this.validatorService = validatorService;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponseObject<ClassroomDTO>> createClassroom(@RequestBody  ClassroomDTO classroomDTO) {
		
		
		try {
			
			List<String> validationErrors = validatorService.validateObject(classroomDTO);
			
			if(!validationErrors.isEmpty()) {
				
				String errorMessages = String.join(", ", validationErrors);
				System.out.println("errors: "+errorMessages);
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, errorMessages, false));
				
				
			}
			ClassroomDTO dto = _interface.createClassroom(classroomDTO);
			
			ApiResponseObject<ClassroomDTO> response = new ApiResponseObject<>(dto, null, true);
					
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
		
			System.out.println(e);
			
			ApiResponseObject<ClassroomDTO> response = new ApiResponseObject<>(null, e.getMessage(), false);
			return ResponseEntity.badRequest().body(response);
		}
		
	}
	
	@PostMapping("/enroll")
	public ResponseEntity<ApiResponseObject<String>> postMethodName
	(@RequestBody EnrollmentRequest enrollmentReq, @RequestParam String role) {
		
		try {
			
			List<String> validationErrors = validatorService.validateObject(enrollmentReq);
			
			if(!validationErrors.isEmpty()) {
				
				String errors = String.join(", ", validationErrors);
				
				return ResponseEntity.badRequest()
						             .body(new ApiResponseObject<>(null, errors, false));
			}
			
			_interface.enrollStudents(enrollmentReq, role);
			
			final String successMessage = enrollmentReq.studentIds().size()+" students successfully enrolled";
			
			return ResponseEntity.ok(new ApiResponseObject<>(successMessage, null, true));
			
		} catch (Exception e) {
			
			System.out.println("enrollment error "+e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
		
		
	}
	
	
	@GetMapping
	public ResponseEntity<ApiResponseObject<Page<ClassroomDTO>>> getManagedClassrooms(@RequestParam Integer userId, 
			@RequestParam String role,
			   @RequestParam(defaultValue = "0") int page,
			    @RequestParam(defaultValue = "5") int size
			) {
		
		try {
			Page<ClassroomDTO> classrooms = _interface.getManagedClassrooms(userId, role, page, size);
			
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(classrooms, null, true);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			
			System.out.println(e);
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(null, e.getMessage(), false);
			
			
		
			return ResponseEntity.badRequest().body(response);
		}
		
		
	}
	
	@GetMapping("/inst")
	public ResponseEntity<ApiResponseObject<Page<ClassroomDTO>>> classroomsByInstitution(@RequestParam Integer userId,
			@RequestParam String role, @RequestParam("inst")Integer instId,
			 @RequestParam(defaultValue = "0") int page,
			    @RequestParam(defaultValue = "5") int size) {
		try {

			final Page<ClassroomDTO> classrooms = _interface.getManagedClassroomsByInstitution(userId, role, instId, page, size);
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(classrooms, null, true);
			
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			System.out.println(e);
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(null, e.getMessage(), false);
			
			return ResponseEntity.badRequest().body(response);
		}
		
	}
	
	@GetMapping("/lev")
	public ResponseEntity<ApiResponseObject<Page<ClassroomDTO>>> classroomsByLevel(@RequestParam Integer userId,
			                                        @RequestParam String role, 
			                                        @RequestParam("levId") Integer level,
			                                        @RequestParam(defaultValue = "0") int page, 
			                                       
			                                         @RequestParam(defaultValue = "5") int size ) {
		
	try {
		final Page<ClassroomDTO> classrooms = _interface.getManagedClassroomsByLevel(userId, role, level, page, size);
		ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(classrooms, null, true);
		
		return ResponseEntity.ok(response);
		
		
	} catch (Exception e) {
		System.out.println(e);
		ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(null, e.getMessage(), false);
		
		return ResponseEntity.badRequest().body(response);
	}
	
	}
	
	@GetMapping("/levInst")
	public ResponseEntity<ApiResponseObject<Page<ClassroomDTO>>> classroomsByInstitutionAndLevel (
			@RequestParam Integer userId,
			@RequestParam String role,
			@RequestParam Integer levId,
			@RequestParam Integer instId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5")int size
			) {
		
		
		
		try {
			Page<ClassroomDTO> classrooms = _interface.getManagedClassroomsByLevelAndInstitution(userId, role, instId, levId, page, size);
			
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<Page<ClassroomDTO>>(classrooms, null, true);
			
			return ResponseEntity.ok(response);
			
			
		} catch (Exception e) {
			System.out.println(e);
			ApiResponseObject<Page<ClassroomDTO>> response = new ApiResponseObject<>(null, e.getMessage(), false);
			
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@GetMapping("/details/adm")
	public ResponseEntity<ApiResponseObject<ClassroomFullDetails>> fullDetails(@RequestParam Integer institutionId,
			@RequestParam Integer classroomId, @RequestParam Integer userId) {
		
		System.out.println("controller");
		try {
			final ClassroomFullDetails detailedInformation = _interface.adminClassroomDetails(classroomId, userId, institutionId);
			
			return ResponseEntity.ok(new ApiResponseObject<>(detailedInformation, null, true));
		} catch (Exception e) {
			
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	}
	
	@GetMapping("/details/prim")
	public ResponseEntity<ApiResponseObject<ClassroomSummarizedDetails>> summarizedDetails(@RequestParam Integer institutionId,
			@RequestParam Integer classroomId, @RequestParam Integer userId) {
		
		
		
		try {
			final ClassroomSummarizedDetails summarizedDetails = _interface.primaryInstructorClassroomDetails(classroomId, userId, institutionId);
			
			return ResponseEntity.ok(new ApiResponseObject<>(summarizedDetails, null, true));
		} catch (Exception e) {
			System.out.println("error:");
			
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	}
	
	
	@GetMapping("/details/instr")
	public ResponseEntity<ApiResponseObject<ClassroomSpecificDetails>> specificDetils(@RequestParam Integer institutionId,
			@RequestParam Integer classroomId, @RequestParam Integer userId) {
		
		try {
			
			final ClassroomSpecificDetails specificDetails = _interface.subjectInstructorClassroomDetails(classroomId, userId, institutionId);
			
			return ResponseEntity.ok(new ApiResponseObject<>(specificDetails, null, true));
			
		} catch (Exception e) {
			
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	}
	
	
	@GetMapping("/search/adm")
	public ResponseEntity<ApiResponseObject<ClassroomFullDetails>> searchAdminClassroom(@RequestParam Integer institutionId, @RequestParam Integer userId, @RequestParam String searchQuery) {
		
		try {
			
			if(searchQuery.trim().length() == 0) {
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, "No record found", false));
			}
			
			ClassroomFullDetails classroom = _interface.searchAdminClassroom(userId, institutionId, searchQuery);
			
			return ResponseEntity.ok(new ApiResponseObject<>(classroom, null, true));
					
			
		} catch (Exception e) {
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
	}
	
	
	@GetMapping("/search/prim")
	public ResponseEntity<ApiResponseObject<ClassroomSummarizedDetails>> searchPrimaryInstructorClassroom(@RequestParam Integer institutionId, @RequestParam Integer userId, @RequestParam String searchQuery) {
		
		try {
			
			if(searchQuery.trim().length() == 0) {
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, "No record found", false));
			}
			
			ClassroomSummarizedDetails classroom = _interface.searchPrimaryInstructorClassroom(userId, institutionId, searchQuery);
			
			return ResponseEntity.ok(new ApiResponseObject<>(classroom, null, true));
					
			
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
	}
	
	
	
	@GetMapping("/search/instr")
	public ResponseEntity<ApiResponseObject<ClassroomSpecificDetails>> searchSubjectInstructorClassroom(@RequestParam Integer institutionId, @RequestParam Integer userId, @RequestParam String searchQuery) {
		
		try {
			if(searchQuery.trim().length() == 0) {
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, "No record found", false));
			}
			ClassroomSpecificDetails classroom = _interface.searchSubjectInstructorClassroom(userId, institutionId, searchQuery);
			
			
			
			return ResponseEntity.ok(new ApiResponseObject<>(classroom, null, true));
					
			
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
	}
	
	@GetMapping("/enrolled")
	public ResponseEntity<ApiResponseObject<List<StudentBasicDetails>>> getEnrolledStudents(@RequestParam Integer classroomId,
			@RequestParam Integer institutionId) {
		
		try {
			
			List<StudentBasicDetails> enrolledStudents = _interface.getEnrolledStudents(classroomId, institutionId);
			
			return ResponseEntity.ok(new ApiResponseObject<>(enrolledStudents, null, true));
			
			
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest()
                    .body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	 }
	
	
	@GetMapping("/instructors")
	public ResponseEntity<ApiResponseObject<List<InstructorBasicDetails>>> getInstructors(@RequestParam Integer adminId, @RequestParam Integer institutionId) {
		
		try {
			
			List<InstructorBasicDetails> instructors = _interface.getInstructorsInInstitution(adminId, institutionId);
			
			return ResponseEntity.ok(new ApiResponseObject<>(instructors, null, true));
		} catch (Exception e) {
			
			System.out.println(e);
			return ResponseEntity.badRequest()
                    .body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
	}
	
	@PostMapping("/assign")
	public ResponseEntity<ApiResponseObject<String>> assignSubjects(@RequestBody List<SubjectAssignmentRequest> requests ,@RequestParam Integer classroom) {
		try {

			List<String> violations = validatorService.validateObjectList(requests);
			
			if(!violations.isEmpty()) {
				
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, String.join(",", violations), false));
			}
			
			_interface.assignSubjectInstructor(requests, classroom);
			
			
			return ResponseEntity.ok(new ApiResponseObject<>("Sucessful", null, true));
		} catch (Exception e) {
			
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
		
	}
	
	@PutMapping("/{classroomId}/{instructorId}")
	public ResponseEntity<ApiResponseObject<String>> updatePrimaryInstructor(@PathVariable Integer classroomId, @PathVariable Integer instructorId) {
		
		try {
			
			_interface.updatePrimaryInstructor(instructorId, classroomId);
			
			return ResponseEntity.ok(new ApiResponseObject<>("Successful!", null, true));
		} catch (Exception e) {
			
			System.out.println(e);
			
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	}
	
	
	
	
	
	
}

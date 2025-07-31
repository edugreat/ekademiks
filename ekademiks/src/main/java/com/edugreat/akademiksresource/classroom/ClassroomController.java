package com.edugreat.akademiksresource.classroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.classroom.service.ClassroomInterface;
import com.edugreat.akademiksresource.util.ApiResponseObject;
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
	
	
	
	
	
}

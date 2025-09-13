package com.edugreat.akademiksresource.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.contract.InstitutionInterface;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.util.ApiResponseObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/institutions")

public class InstitutionController {

    
	
	private final InstitutionInterface institutionInterface;
	
	public InstitutionController(InstitutionInterface institutionInterface, InstitutionDao institutionDao) {
		
		this.institutionInterface = institutionInterface;
		
		
	}
	
	@GetMapping("/subjects")
	public ResponseEntity<ApiResponseObject<List<SubjectDTO>>> getSubjectsByCategory(@RequestParam("userId") Integer adminOrInstructorId,
			                                                    @RequestParam Integer categoryId) {
		
		
		try {
			List<SubjectDTO> subjects = institutionInterface.findSubjectsForCategory(adminOrInstructorId, categoryId);
			
			return ResponseEntity.ok()
					.body(new ApiResponseObject<>(subjects, null, true)); 
			
		} catch (Exception e) {
			
			System.out.println("Error  => institution controller");
			return ResponseEntity
					.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
	}
	
	@GetMapping("/promotional/classes")
	public ResponseEntity<ApiResponseObject<Set<Classroom>>> getPromotionalClassrooms(@RequestParam Integer currentClassroomId,
			                                          Integer institutionId, Integer enrollmentOfficerId) {
		
		try {
			Set<Classroom> classrooms = institutionInterface.getPromotionClassrooms(currentClassroomId, institutionId, enrollmentOfficerId);
		
			return ResponseEntity.ok()
					.body(new ApiResponseObject<>(classrooms,null, true));
		} catch (Exception e) {
			System.out.println(e);
			return ResponseEntity.badRequest()
					             .body(new ApiResponseObject<>(null, e.getMessage(), false));
		}
		
		
	}
	
	
	@PostMapping("/promote")
	public ResponseEntity<ApiResponseObject<String>> promoteStudents(@RequestBody List<Integer> studentIds, 
			                      @RequestParam Integer targetClassroom,
			                     @RequestParam Integer currentClassroom,
			                     @RequestParam Integer promotingOfficerId,
			                     @RequestParam Integer institutionId) {
		
		try {
			if(studentIds.isEmpty()) {
				return ResponseEntity.badRequest()
						.body(new ApiResponseObject<>(null, "student identifiable records not provided", false));
			}
			
		final String targetClassroomName = 	institutionInterface.promoteStudents
			(studentIds, targetClassroom, currentClassroom, promotingOfficerId, institutionId);
		
		StringBuilder responseMessageBuilder = new StringBuilder();
		responseMessageBuilder.append("successfully promoted ")
		.append(studentIds.size())
		.append("into ")
		.append(targetClassroomName)
		.append("classroom");
		
			return ResponseEntity.ok()
					.body(new ApiResponseObject<>(responseMessageBuilder.toString(), null, true));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ApiResponseObject<>(null, e.getMessage(), false));
					
		}
		
		
	}
	
	
	

}

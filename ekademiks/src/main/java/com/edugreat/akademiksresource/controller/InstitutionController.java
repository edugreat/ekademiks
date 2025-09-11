package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.InstitutionInterface;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.util.ApiResponseObject;


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
	

}

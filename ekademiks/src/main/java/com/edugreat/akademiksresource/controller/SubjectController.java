package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.model.Subject;


@RestController
@RequestMapping("subjects")
public class SubjectController {
	
	private final SubjectDao dao;
	
	public SubjectController(SubjectDao dao) {
		this.dao = dao;
	}
	
	
	
	@GetMapping("/by")
	public ResponseEntity<List<SubjectDTO>> getByInstitutionAndCategory(@RequestParam Integer inst, @RequestParam Integer cateId) {
		
		
		List<Subject> subjects = dao.findByLevelIdAndInstitutionId(cateId, inst);
		
		
		
		return ResponseEntity.ok(subjects.stream().map(this::mapToSubjectDTO).toList());
	}
	
	
	private SubjectDTO mapToSubjectDTO(Subject subject){
		
		
		return new SubjectDTO(subject.getId(), subject.getSubjectName(), subject.getLevel().getCategoryLabel());
	}
	

}

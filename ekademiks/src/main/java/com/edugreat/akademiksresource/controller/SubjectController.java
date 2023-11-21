package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.views.SubjectView;
import com.edugreat.akademiksresource.views.SubjectViewWrapper;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping(path = "/subjects")
public class SubjectController {
	
	
	private SubjectDao subjectDao;
	
	public SubjectController(SubjectDao subjectDao) {
		this.subjectDao = subjectDao;
	}

	@GetMapping
	@JsonView(SubjectView.class)
	private ResponseEntity<Object> getSubjects(){
		
		SubjectViewWrapper subjectWrapper = new SubjectViewWrapper();
		List<Subject> subjects = subjectDao.findAll();
		subjectWrapper.setSubjects(subjects);
		
		
		return new ResponseEntity<Object>(subjectWrapper, HttpStatus.OK);
		
	}
}

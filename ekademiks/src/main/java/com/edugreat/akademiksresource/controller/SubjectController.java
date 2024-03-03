package com.edugreat.akademiksresource.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.SubjectInterface;
import com.edugreat.akademiksresource.dto.SubjectDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("acad/v1/subject")
@RequiredArgsConstructor
public class SubjectController {
	
	private final SubjectInterface service;
	

	@PostMapping
	public ResponseEntity<Object> setSubject(@RequestBody @Valid SubjectDTO dto){
		
		
		
		return new ResponseEntity<>(service.setSubject(dto), HttpStatus.CREATED);
	}

}

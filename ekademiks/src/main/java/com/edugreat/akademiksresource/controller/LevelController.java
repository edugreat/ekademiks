package com.edugreat.akademiksresource.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.LevelInterface;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/acad/v1")
@RequiredArgsConstructor
public class LevelController {
	
	private final LevelInterface service;
	
	@GetMapping("levels")
	public ResponseEntity<Object> findAll(){
		
		return new ResponseEntity<Object>(service.findAll(), HttpStatus.OK);
	}
	
	
	

}

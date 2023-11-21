package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.views.TestView;
import com.edugreat.akademiksresource.views.TestViewWrapper;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/tests")
@JsonView(TestView.class)
public class TestController {
	
	private TestDao dao;
	
	private TestViewWrapper wrapper;

	public TestController(TestDao dao, TestViewWrapper wrapper) {
		
		this.dao = dao;
		this.wrapper = wrapper;
	}
	
	@GetMapping("/{id}")
	@JsonView(TestView.class)
	public ResponseEntity<Object> getAllByQuestionId(@PathVariable("id") Integer id){
	
		List<Test> tests = dao.findTestByQuestionId(id);
		
		wrapper.setTests(tests);
		
		return new ResponseEntity<Object>(wrapper, HttpStatus.OK);
	}
	

}

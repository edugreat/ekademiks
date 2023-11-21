package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.OptionDao;
import com.edugreat.akademiksresource.model.Option;
import com.edugreat.akademiksresource.views.OptionView;
import com.edugreat.akademiksresource.views.OptionViewWrapper;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/options")
public class OptionController {
	
	private OptionDao dao;
	
	private OptionViewWrapper wrapper;

	public OptionController(OptionDao dao, OptionViewWrapper wrapper) {
		
		this.dao = dao;
		this.wrapper = wrapper;
	}
	
	@GetMapping("/{id}")
	@JsonView(OptionView.class)
	public ResponseEntity<Object> getAllByQuestionId(@PathVariable("id") Integer id){
		
		List<Option> options = dao.findByQuestionId(id);
		wrapper.setOptions(options);
		
		return new ResponseEntity<Object>(options, HttpStatus.OK);
	}
	

}

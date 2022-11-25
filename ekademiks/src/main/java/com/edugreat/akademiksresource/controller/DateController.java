package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.custom.dao.DatesOnly;
import com.edugreat.akademiksresource.dao.SubjectDao;

/*
 * This controller class is used to retrieve an array of dates from the SubjectDao interface
 */
@RestController
@RequestMapping("/subject")
public class DateController {
	
	@Autowired
	private SubjectDao subjectDao;
	
	
	@GetMapping()
   public List<DatesOnly> getDates(@RequestParam("id") Integer id){
		return subjectDao.findAllByCategoryId(id);
		
	}
}

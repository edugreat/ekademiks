package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.CategoryDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.projection.DatesOnly;
import com.edugreat.akademiksresource.projection.NamesAndId;
import com.edugreat.akademiksresource.projection.NamesOnly;

/*
 * This controller class is used to retrieve an array of dates from the SubjectDao interface
 */
@RestController
@RequestMapping("/subject")
@CrossOrigin(value = { "http://localhost:4200" })
public class MultiController {

	@Autowired
	private SubjectDao subjectDao;
	
	@Autowired
	private CategoryDao categoryDao;

	@GetMapping()
	public List<DatesOnly> getDates(@RequestParam("name")String name) {
		return subjectDao.findDistinctByCategoryName(name);

	}

	// routes to get all subjects with the date parameter
	@GetMapping("/date")
	public List<NamesOnly> getSubjectWithNames(@RequestParam("date") String date,
			@RequestParam("categoryName") String categoryName) {
		
		Integer year = Integer.parseInt(date);
		
		return subjectDao.findByExamCategoryAndYear(categoryName, year);
	}
	@GetMapping("/category")
	public List<NamesAndId> findDistinctCategory(@RequestParam("name")String name){
		
		return categoryDao.findDistinctBySubjectsNameContaining(name);
	}
	//endpoint to retrieve dates when a particular question has been searched by the user
	@GetMapping("/search")
	public List<DatesOnly> 
	searchDates(@RequestParam("category")String category, @RequestParam("subject")String subject){
		
		return subjectDao.findDistinctByCategoryNameAndNameContaining(category, subject);
	}
}

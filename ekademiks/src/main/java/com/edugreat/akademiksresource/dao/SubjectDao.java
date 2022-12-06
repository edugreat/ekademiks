package com.edugreat.akademiksresource.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.projection.DatesOnly;
import com.edugreat.akademiksresource.projection.SubjectAndOptions;
import com.edugreat.akademiksresource.projection.SubjectNamesOnly;
//Jpa interface to manage the Subject entity
//@RepositoryRestResource(excerptProjection = DatesOnly.class)
@CrossOrigin(value = {"http://localhost:4200"})
public interface SubjectDao extends JpaRepository<Subject, Integer> {
  
	//Return an array of date attributes contained in the subject entity whose category id matches the request parameter
	List<DatesOnly> findAllByCategoryId(@RequestParam("id") Integer id);
	
	//Returns an array of subjects with their name attribute
	/*
	 * Query method retrieves Subject entity whose examYear property and course_category property
	 * matches the provided arguments
	 */
	@Query(value ="SELECT * From Subject WHERE YEAR(exam_year) =:year AND course_category = "
			+ "(Select id From Category Where name =:categoryName)", nativeQuery = true)
	List<SubjectNamesOnly> findByExamCategoryAndYear(String categoryName, Integer year);
	
	Page<SubjectAndOptions> findByNameContaining(@RequestParam("subjectName")String subjectName, Pageable pageable);
}

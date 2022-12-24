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
import com.edugreat.akademiksresource.projection.NamesOnly;
//Jpa interface to manage the Subject entity
//@RepositoryRestResource(excerptProjection = DatesOnly.class)
@CrossOrigin(value = {"http://localhost:4200"})
public interface SubjectDao extends JpaRepository<Subject, Integer> {
  
	//Return an array of date attributes contained in the subject entity whose category id matches the request parameter
	List<DatesOnly> findDistinctByCategoryIdAndCategoryName(@RequestParam("id") Integer id, @RequestParam("name")String name);

	//Returns an array of subjects with their name attribute
	/*
	 * Query method retrieves Subject entity whose examYear property and course_category property
	 * matches the provided arguments
	 */
	@Query(value ="SELECT * From Subject WHERE YEAR(exam_year) =:year AND course_category = "
			+ "(Select id From Category Where name =:categoryName)", nativeQuery = true)
	List<NamesOnly> findByExamCategoryAndYear(String categoryName, Integer year);
	
	/*
	 * Returns Subject entities for the given year, name and exam category
	 */
	@Query(value ="SELECT * FROM Subject WHERE name Like %?1% AND YEAR(exam_year) Like %?2 AND course_category Like %?3", nativeQuery = true)
	Page<Subject> findByNameAndExamDate(@RequestParam("name")String name, @RequestParam("examYear")String examYear, @RequestParam("categoryId")Integer categoryId, Pageable pageable);
	
	List<DatesOnly> 
	findDistinctByCategoryNameAndNameContaining(String category, String subject);
}

package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import com.edugreat.akademiksresource.custom.dao.DatesOnly;
import com.edugreat.akademiksresource.model.Subject;
//Jpa interface to manage the Subject entity
//@RepositoryRestResource(excerptProjection = DatesOnly.class)
public interface SubjectDao extends JpaRepository<Subject, Integer> {
  
	//Return an array of date attributes contained in the subject entity whose category id matches the request parameter
	List<DatesOnly> findAllByCategoryId(@RequestParam("id") Integer id);
	
	
	
}

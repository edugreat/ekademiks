package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Test;
//@CrossOrigin("http://localhost:4200")
public interface TestDao extends JpaRepository<Test, Integer>{

	@Query("SELECT t FROM Test t JOIN t.subject s ON t.testName =:testName AND s.level.category =:category")
	Test findByTestName(String testName, Category category);
	
	
	
}

package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Test;

public interface TestDao extends JpaRepository<Test, Integer>{

	Test findByTestName(String testName);
	
	
	
}

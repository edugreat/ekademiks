package com.edugreat.akademiksresource.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.TopicAndDuration;

@RepositoryRestResource(collectionResourceRel = "Tests")
public interface TestDao extends JpaRepository<Test, Integer>{

	//Fetches from the database a list of questions using the given arguments
	@Query("SELECT t.questions FROM Test t JOIN t.subject s ON t.testName =:testName AND s.level.category =:category")
	List<Question> findTestQuestions(String testName, Category category);
	
	//Fetches from the database, Test object using the test name and the test category
	@Query("SELECT t FROM Test t JOIN t.subject s ON t.testName =:testName AND s.level.category =:category")
	public Test findByTestName(String testName, Category category);
	
	//fetches all the tests for the given subject and category
	@Query("SELECT t FROM Test t JOIN t.subject s ON s.level.category =:category AND s.subjectName =:subjectName")
	List<TopicAndDuration> findByTestNameAndCategory(String subjectName, Category category);

	//return instructions for the test matching the given criteria
	@Query("SELECT i FROM Test t JOIN t.instructions i JOIN t.subject s ON t.testName =:topic AND s.level.category =:category")
	Collection<String> getInstructionsFor(String topic, Category category);

//	Retrieves id of a test using its information
	@Query("SELECT t.id FROM Test t join t.subject s ON t.testName =:testName AND s.level.category =:category")
	Integer findId(String testName, Category category);


	
}

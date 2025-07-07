package com.edugreat.akademiksresource.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.TopicAndDuration;

import jakarta.persistence.Tuple;

@Repository
@RepositoryRestResource(collectionResourceRel = "Tests")
public interface TestDao extends JpaRepository<Test, Integer> {

	// Fetches from the database a list of questions using the given arguments
	@Query("SELECT t.questions FROM Test t JOIN t.subject s ON t.testName =:testName AND s.level.category =:category")
	List<Question> findTestQuestions(String testName, Category category);

	// Fetches from the database, Test object using the test name and the test
	// category
	@Query("SELECT t From Test t JOIN t.subject s WHERE t.id =:assessmentId AND s.level.id =:categoryId")
	public Test findByTestIdAndCategoryId(Integer assessmentId, Integer categoryId);
	
	@Query("SELECT t FROM Test t JOIN t.subject s WHERE t.testName =:testName AND s.level.category =:category")
    public Test findByTestNameAndCategory(String testName, Category category);

	// fetches all the tests for the given subject and category belonging to the given institutions plus all those meant for any user
	@Query("SELECT new com.edugreat.akademiksresource.projection.TopicAndDuration(t.testName, t.duration) FROM Test t JOIN t.subject s ON (t.owningInstitution =:institutionId OR t.owningInstitution IS NULL) AND s.level.category =:category AND s.subjectName =:subjectName")
	List<TopicAndDuration> findAllTopicsAndDurations(String subjectName, Category category, Integer institutionId);
	
	@Query("SELECT new com.edugreat.akademiksresource.projection.TopicAndDuration(t.testName, t.duration) FROM Test t JOIN t.subject s ON t.owningInstitution IS NULL AND s.level.category =:category AND s.subjectName =:subjectName")
	List<TopicAndDuration> findAllTopicsAndDurationsForGuestUser(String subjectName, Category category);

	// return instructions for the test matching the given criteria
	@Query("SELECT i FROM Test t JOIN t.instructions i JOIN t.subject s ON t.testName =:topic AND s.level.category =:category")
	Collection<String> getInstructionsFor(String topic, Category category);

//	Retrieves id of a test using its information
	@Query("SELECT t.id FROM Test t join t.subject s ON t.testName =:testName AND s.level.category =:category")
	Integer findId(String testName, Category category);

//	Retrieves subject name by their test id
	@Query("SELECT t.subject.subjectName FROM Test t WHERE t.id =:id")
	String findSubjectNameByTestId(int id);

//	Retrieves category(level name) for the given test id
	@Query("SELECT t.subject.level.category FROM Test t WHERE t.id =:id")
	Category findCategoryNameByTestId(int id);

//	Retrieves the topic and duration for a given test id
	@Query("SELECT t FROM Test t WHERE t.id =:testId")
	TopicAndDuration retrieveTopicAndurationById(Integer testId);

//	Retrieves all assessment topics for the given assessment category
	@Query("SELECT CONCAT(t.testName) FROM Test t JOIN t.subject s WHERE s.level.id = :categoryId")  
	List<String> getTopicsFor(Integer categoryId);
	
	   @Query("SELECT t. id, t.testName FROM Test t JOIN t.subject s WHERE s.level.id = :categoryId ORDER BY t.testName ASC")
	List<Tuple> getTopicsAsTuples(@Param("categoryId") Integer categoryId);

}

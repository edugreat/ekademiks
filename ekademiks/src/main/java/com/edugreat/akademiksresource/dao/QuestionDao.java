package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.Question;

public interface QuestionDao extends JpaRepository<Question, Integer> {
	
	//find all the questions whose test_id column matches the provided testID argument
	@Query(nativeQuery = true, value = "select * from "
			+ "question where test_id = ?1")
	public List<Question>findByTestId(Integer testId);
	
	//find all questions whose topic matches the argument
	public List<Question> findByTopic(String topic);

}

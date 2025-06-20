package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.Question;
@Repository
//@RepositoryRestResource(exported = false)
public interface QuestionDao extends JpaRepository<Question, Integer> {

	// gets all the questions for the given test identifier
	@Query("SELECT q FROM Question q join q.test t WHERE t.id =:testId")
	public List<Question> findByTestId(@Param("testId") int id);

}

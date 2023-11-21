package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.Test;

public interface TestDao extends JpaRepository<Test, Integer> {
	
	@Query(nativeQuery = true, value = "SELECT * FROM test where subject_id = ?1")
	public List<Test> findTestByQuestionId(Integer id);

}

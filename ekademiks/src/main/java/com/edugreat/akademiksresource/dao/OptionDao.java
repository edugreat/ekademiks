package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.Option;

public interface OptionDao extends JpaRepository<Option, Integer> {

	@Query(nativeQuery = true, value = "SELECT * FROM `option` where question_id = ?1")
	public List<Option> findByQuestionId(Integer id);
}

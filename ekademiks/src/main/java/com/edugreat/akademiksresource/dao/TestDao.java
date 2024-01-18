package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;

public interface TestDao extends JpaRepository<Test, Integer>{
	
	
	//loads all questions given a test id
	public List<Question> findQuestionsById(int testId);
}

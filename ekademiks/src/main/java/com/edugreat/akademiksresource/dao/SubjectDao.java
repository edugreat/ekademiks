package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Subject;

public interface SubjectDao extends JpaRepository<Subject, Integer>{
	
	//finds subject by the subject name
	public Subject findBySubjectName(String name);

}

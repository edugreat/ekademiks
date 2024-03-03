package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.Subject;


public interface SubjectDao extends JpaRepository<Subject, Integer>{
	
	//finds subject by the subject name
	public Subject findBySubjectName(String name);
	
	//checks if the subject already exists by name
	@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.id =:levelId")
	public boolean subjectExists(String name, Integer levelId);

}

package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Subject;


public interface SubjectDao extends JpaRepository<Subject, Integer>{
	
	//finds subject by the subject name
	@Query("SELECT s FROM Subject s JOIN s.level l ON s.subjectName =:name AND l.category =:category")
	public Subject findBySubjectName(String name, Category category);
	
	//checks if the subject already exists by name
	@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.id =:levelId")
	public boolean subjectExists(String name, Integer levelId);
	
	@Query("SELECT s.subjectName FROM Subject s join s.level l ON l.category =:category")
	public List<String> findSubjetByLevelCategory(Category category);

}

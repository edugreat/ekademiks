package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Subject;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
//@RepositoryRestResource(exported = false)
public interface SubjectDao extends JpaRepository<Subject, Integer> {

	// finds subject by the subject name
	@Query("SELECT s FROM Subject s JOIN s.level l ON s.subjectName =:name AND l.category =:category")
	public Subject findBySubjectNameAndCategory(String name, Category category);

	// checks if the subject already exists by name
	@RestResource(path = "existsByCategoryId")
	@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.id =:levelId")
	public boolean subjectExists(String name, Integer levelId);
	
	// checks if the subject already exists by name
	@RestResource(path = "existsByCategory")
		@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.category =:category")
		public boolean subjectExists(String name, Category category);


	@Query("SELECT s.subjectName FROM Subject s join s.level l ON l.category =:category")
	public List<String> findSubjectNamesByCategory(Category category);
	
	@RestResource(exported = false)
	public List<Subject> findByLevelIdAndInstitutionId(@Param("levelId") Integer levelId, @Param("inst") Integer inst);

	@RestResource(exported = false)
	@Query("SELECT s FROM Subject s WHERE s.institutionId IN :institutionIds AND s.level.id =:categoryId")
	public List<Subject> findByInstitutionIds(Set<Integer> institutionIds, Integer categoryId);



}

package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Subject;

public interface SubjectDao extends JpaRepository<Subject, Integer> {

	// finds subject by the subject name
	@Query("SELECT s FROM Subject s JOIN s.level l ON s.subjectName =:name AND l.category =:category")
	public Subject findBySubjectNameAndCategory(String name, Category category);

	// checks if the subject already exists by name
	@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.id =:levelId")
	public boolean subjectExists(String name, Integer levelId);
	
	// checks if the subject already exists by name
		@Query("SELECT CASE WHEN COUNT(s.subjectName) > 0 THEN true ELSE false END FROM Subject s WHERE s.subjectName =:name AND s.level.category =:category")
		public boolean subjectExists(String name, Category category);


	@Query("SELECT s.subjectName FROM Subject s join s.level l ON l.category =:category")
	@Cacheable(value = "subjectNamesCache", key = "#category.name()")
	public List<String> findSubjectNamesByCategory(Category category);

	@Query("SELECT s.id FROM Subject s WHERE s.level.category =:category")
	public List<Integer> allIdsByCategory(Category category);

	@Query("SELECT s.subjectName FROM Subject s WHERE s.id =:id")
	public String findSubjectNameById(Integer id);

	@Override
	@CachePut(value = "subjectCache", key ="#entity.id")
	 <S extends Subject> S save(S entity);
	
	
	

	@Override
	@Cacheable(value = "allSubjectsCache", key = "'allSubjects'")
	 List<Subject> findAll();

	@Override
	@Caching(evict = {
			@CacheEvict(value = "studentCache", key = "#id"),
			@CacheEvict(value = "subjectNamesCache", allEntries = true),
			@CacheEvict(value = "allSubjectsCache", key = "'allSubjects'")
	})
	 void deleteById(Integer id);

	@Override
	@Caching(evict = {
			@CacheEvict(value = "studentCache", key = "#id"),
			@CacheEvict(value = "subjectNamesCache", key = "#entity.level.category.name()"),
			@CacheEvict(value = "allSubjectsCache", key = "'allSubjects'")
	})
	 void delete(Subject entity);
	
	


	

}

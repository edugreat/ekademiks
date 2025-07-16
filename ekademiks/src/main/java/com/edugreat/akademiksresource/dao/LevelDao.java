package com.edugreat.akademiksresource.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Level;
@Repository
public interface LevelDao extends JpaRepository<Level, Integer> {

	Level findByCategory(Category category);
	
	// checks if a record exists by the category, return boolean value
	@Query("SELECT CASE WHEN COUNT(l.category) > 0 THEN true ELSE false END FROM Level l WHERE l.category =:category")
	public boolean existsByCategory(Category category);

	void deleteByCategory(Category valueOf);
	
	@Query("Select l FROM Level l JOIN l.subjects s JOIN s.tests t "
			+ "ON t.instructor.id =:id")
	@RestResource(path = "/instructor")
	Page<Level> findByInstructor(@Param("id")Integer id, Pageable pageble);

}

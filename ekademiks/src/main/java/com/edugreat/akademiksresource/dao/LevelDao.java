package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Level;

public interface LevelDao extends JpaRepository<Level, Integer> {

	Level findByCategory(Category category);
	// checks if a record exists by the category, return boolean value

	@Query("SELECT CASE WHEN COUNT(l.category) > 0 THEN true ELSE false END FROM Level l WHERE l.category =:category")
	public boolean existsByCategory(Category category);

}

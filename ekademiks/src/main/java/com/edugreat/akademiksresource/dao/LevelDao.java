package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.model.Level;



public interface LevelDao extends JpaRepository<Level, Integer> {

	Level findByCategory(Category category);

}

package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Category;

//Jpa interface to manage the Category entity
public interface CategoryDao extends JpaRepository<Category, Integer> {

}

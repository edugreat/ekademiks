package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Category;

public interface CategoryDao extends JpaRepository<Category, Integer> {

}

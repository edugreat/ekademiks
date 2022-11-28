package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.edugreat.akademiksresource.model.Category;

//Jpa interface to manage the Category entity
@CrossOrigin(value = {"http://localhost:4200"})
public interface CategoryDao extends JpaRepository<Category, Integer> {

}

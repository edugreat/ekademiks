package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.edugreat.akademiksresource.model.Category;
import com.edugreat.akademiksresource.projection.NamesAndId;

//Jpa interface to manage the Category entity
@CrossOrigin(value = {"http://localhost:4200"})
public interface CategoryDao extends JpaRepository<Category, Integer> {

	/**
	 * 
	 * @param name subject name supplied to retrieve
	 * distinct subject categories under which those subjects belong
	 * @return Category list that has only two attributes of 'id' and 'name'
	 * implemented by jpa projection
	 */
public List<NamesAndId> findDistinctBySubjectsNameContaining(String name);	
	
}

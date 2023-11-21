package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.edugreat.akademiksresource.model.StudentSelectedOption;

@RepositoryRestResource(path = "student_selected_options", collectionResourceRel = "student_selected_options")
public interface StudentSelectedOptionDao extends JpaRepository<StudentSelectedOption, Integer> {

}

package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.edugreat.akademiksresource.model.StudentTest;

@RepositoryRestResource(path = "student_test", collectionResourceRel = "student-test")
public interface StudentTestDao extends JpaRepository<StudentTest, Integer> {

}

package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.model.Student;

public interface StudentDao extends JpaRepository<Student, Integer> {

}

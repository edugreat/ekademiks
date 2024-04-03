package com.edugreat.akademiksresource.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.edugreat.akademiksresource.model.Student;

public interface StudentDao extends JpaRepository<Student, Integer>{
	
	//checks the the existence of a student by their email
	@Query("SELECT CASE WHEN COUNT(s.email) > 0 THEN true ELSE false END FROM Student s WHERE s.email =:email")
	public boolean existsByEmail(String email);
	
	//checks the existence of a student by their phone number
	@Query("SELECT CASE WHEN COUNT(s.mobileNumber) > 0 THEN true ELSE false END FROM Student s WHERE s.mobileNumber =:mobile")
	public boolean existsByMobile(String mobile);
	
	//finds student by their phone number
	public Student findByMobileNumber(String mobileNumber);
	
	//finds student by their email address
	public Optional<Student> findByEmail(String email);

	@Modifying
	@Query("DELETE FROM Student s WHERE s.email =:email")
	public void deleByEmail(@Param("email") String email);

}

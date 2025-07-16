package com.edugreat.akademiksresource.instructor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface InstructorDao extends JpaRepository<Instructor, Integer> {

	Optional<Instructor> findByEmail(String username);
	
	boolean existsByEmail(String email);
	
	

}

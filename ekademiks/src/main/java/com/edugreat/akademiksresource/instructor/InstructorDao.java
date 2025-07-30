package com.edugreat.akademiksresource.instructor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface InstructorDao extends JpaRepository<Instructor, Integer> {

	Optional<Instructor> findByEmail(String username);
	
	boolean existsByEmail(String email);
	
	
	@Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Instructor i JOIN i.institutions ins WHERE i.email =:email AND ins.id =:institutionId")
	boolean isDuplicateAccountCreationAttempt(String email, Integer institutionId);
	
	@Query("SELECT i FROM Instructor i JOIN i.institutions ins WHERE i.id =:instructorId AND ins.id =:institutionId")
	Optional<Instructor> findByInstitutionId(Integer institutionId, Integer instructorId);
	
	

}

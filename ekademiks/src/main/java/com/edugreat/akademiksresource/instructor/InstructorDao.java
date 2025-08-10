package com.edugreat.akademiksresource.instructor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;



public interface InstructorDao extends JpaRepository<Instructor, Integer> {

	@RestResource(exported = false)
	Optional<Instructor> findByEmail(String username);
	
	@RestResource(exported = false)
	boolean existsByEmail(String email);
	
	@RestResource(exported = false)
	boolean existsByMobileNumber(String mobileNumber);
	
	@RestResource(exported= false)
	@Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Instructor i JOIN i.institutions ins WHERE i.email =:email AND ins.id =:institutionId")
	boolean isDuplicateAccountCreationAttempt(String email, Integer institutionId);
	
	@RestResource(exported = false)
	@Query("SELECT i FROM Instructor i JOIN i.institutions ins WHERE i.id =:instructorId AND ins.id =:institutionId")
	Optional<Instructor> findByInstitutionId(Integer institutionId, Integer instructorId);
	
	
	
	

}

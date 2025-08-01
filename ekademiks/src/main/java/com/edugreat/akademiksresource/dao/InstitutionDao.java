package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.Institution;

@Repository
public interface InstitutionDao extends JpaRepository<Institution, Integer> {
	
	//@RestResource(exported = false)
	Optional<Institution> findByNameAndLocalGovt(String name, String localGovt);

	@RestResource(path = "A")
	List<Institution> findByCreatedByOrderByNameAsc(@Param("by") Integer by, Pageable pageable);


	  @RestResource(path = "location")
	    Page<Institution> findByStateAndLocalGovtContainingIgnoreCaseOrderByName(
	        @Param("state") String state, 
	        @Param("lga") String lga,  Pageable pageable);
	   

	  
	  @RestResource(path = "B")
	  @Query("SELECT i FROM Institution i JOIN i.instructors instr WHERE instr.id =:instr ORDER BY i.name ASC")
	  Page<Institution> findByInstructor(@Param("instr") Integer instr,Pageable pageable);
	  
	  @RestResource(path = "islegible")
	  @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Institution i JOIN i.instructors instr WHERE instr.id =:instr AND i.id =:inst")
	  boolean isAnInstructorOfInstitution(@Param("instr") Integer instr, @Param("inst") Integer inst);
	  
	  @RestResource(path = "isadmin")
	  @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Institution  i WHERE i.id =:inst AND i.createdBy =:admin")
	  boolean isAdminOfInstitution(@Param("inst") Integer inst, @Param("admin") Integer admin);
	  
	  

}

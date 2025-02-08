package com.edugreat.akademiksresource.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lombok.Builder.Default;

public interface AssignmentDetailsDao extends JpaRepository<AssignmentDetails, Integer> {

	
	@Query("SELECT a FROM AssignmentDetails a WHERE a.name =:name AND a.category =:category  AND a.institution.id =:institution")
	 AssignmentDetails findIfExists(Integer institution, 
		                       String category, 
		                       String name);
	
	default 
	boolean existsConflicts(Integer institution, String category, String name){
		
		var obj = findIfExists(institution, category, name);
		
		return obj == null ? false : true;
		
		
	}
	
	

	
}

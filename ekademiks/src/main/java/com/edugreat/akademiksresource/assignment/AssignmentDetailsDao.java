package com.edugreat.akademiksresource.assignment;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.Institution;

@Repository
@RepositoryRestResource(exported = false)
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

	@Query("SELECT d.institution FROM AssignmentDetails d WHERE d.id =:assignmentId")
	Institution getInstitution(Integer assignmentId);

	@Query("SELECT d.assignmentResources FROM AssignmentDetails d WHERE d.id =:assignmentDetailsId")
	<T extends AssignmentResource> Set<T> getAssignment(Integer assignmentDetailsId);
	
	

	
}

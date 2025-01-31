package com.edugreat.akademiksresource.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentDetailsDao extends JpaRepository<AssignmentDetails, Integer> {

	@Query("SELECT CASE WHEN a.id IS NOT NULL AND a.name IS NOT NULL THEN TRUE ELSE FALSE END"
			+ " FROM AssignmentDetails a WHERE a.institution.id =:institution AND a.category =:category AND a.name =:name")
	boolean existConflicts(Integer institution, String category, String name);


	
}

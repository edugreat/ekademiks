package com.edugreat.akademiksresource.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentDetailsDao extends JpaRepository<AssignmentDetails, Integer> {

	@Query("SELECT CASE WHEN a.id IS NOT NULL AND a.name IS NOT NULL THEN TRUE ELSE FALSE END"
			+ " FROM AssignmentDetails a WHERE a.institution.id =:institution AND a.category =:category AND a.name =:name")
	boolean existConflicts(Integer institution, String category, String name);
	
//	get the number of institutions the admin whose ID is referenced has registered
	@Query("SELECT COUNT(*) FROM AssignmentDetails a WHERE a.instructor.id =:adminId")
	int countInstitutions(Integer adminId);

//	checks if the admin user whose ID is referenced has any institution for which they can post assignments
	
	default boolean hasInstitution(Integer adminId) {
		
		
		return countInstitutions(adminId) > 0 ? true : false;
	}

	
}

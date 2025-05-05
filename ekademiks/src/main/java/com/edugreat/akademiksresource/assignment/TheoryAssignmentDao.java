package com.edugreat.akademiksresource.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface TheoryAssignmentDao extends JpaRepository<Theories, Integer> {

}

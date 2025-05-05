package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.Institution;

@Repository
@RepositoryRestResource(exported = false)
public interface InstitutionDao extends JpaRepository<Institution, Integer> {
	
	
	Optional<Institution> findByNameAndLocalGovt(String name, String localGovt);

	List<Institution> findByCreatedByOrderByNameAsc(Integer adminId);

}

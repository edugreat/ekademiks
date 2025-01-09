package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edugreat.akademiksresource.dto.InstitutionDTO;
import com.edugreat.akademiksresource.model.Institution;

public interface InstitutionDao extends JpaRepository<Institution, Integer> {
	
	
	Optional<Institution> findByNameAndLocalGovt(String name, String localGovt);

	List<Institution> findByCreatedByOrderByNameAsc(Integer adminId);

}

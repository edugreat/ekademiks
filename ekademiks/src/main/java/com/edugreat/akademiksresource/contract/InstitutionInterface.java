package com.edugreat.akademiksresource.contract;

import java.util.List;

import com.edugreat.akademiksresource.dto.SubjectDTO;

public interface InstitutionInterface {


	List<SubjectDTO> findSubjectsForCategory(Integer adminOrInstructorId, Integer categoryId);
	
}

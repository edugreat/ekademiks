package com.edugreat.akademiksresource.classroom.details;

import java.time.LocalDateTime;

import com.edugreat.akademiksresource.model.Institution;

public record InstitutionBasicDetails(
		
		Integer id,
		String name,
		String state,
		String localGovt,
		LocalDateTime createdOn
		) {
	public InstitutionBasicDetails(Institution institution) {
		
		this(institution.getId(), institution.getName(),institution.getLocalGovt(), institution.getState(), institution.getCreatedOn());
	}

}

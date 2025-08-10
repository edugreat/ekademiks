package com.edugreat.akademiksresource.classroom.details;

import com.edugreat.akademiksresource.classroom.Classroom;

public record ClassroomBasicDetails(
		
		Integer id,
		String name,
		String description,
		Integer academicYear,
		String creationDate,
		String lastModifiedDate,
		String lastModifiedBy,
		Integer studentCount,
		String section,
		LevelBasicDetails level,
		InstitutionBasicDetails institution
		) {
	
	public ClassroomBasicDetails(Classroom classroom) {
		
		this(
			classroom.getId(),
			classroom.getName(),
			classroom.getDescription(),
			classroom.getAcademicYear(),
			classroom.getCreationDate().toString(),
			classroom.getLastModified().toString(),
			classroom.getLastModifiedBy(),
			classroom.getStudents().size(),
			classroom.getSection(),
			new LevelBasicDetails(classroom.getLevel()),
			new InstitutionBasicDetails(classroom.getInstitution())
				
		    );
	}

}

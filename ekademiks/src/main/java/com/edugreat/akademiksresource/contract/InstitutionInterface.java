package com.edugreat.akademiksresource.contract;

import java.util.List;
import java.util.Set;

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.dto.SubjectDTO;

public interface InstitutionInterface {


	List<SubjectDTO> findSubjectsForCategory(Integer adminOrInstructorId, Integer categoryId);
	
	Set<Classroom> getPromotionClassrooms(Integer currentClassroomId, Integer institutionId, Integer promotingOfficerId);
	
//	returns name of the target classroom after successful promotion
	String promoteStudents(List<Integer> studentIds, Integer targetClassroomId, 
			                            Integer currentClassroomId, 
			                            Integer promotingOfficerId,
			                            Integer institutionId);
	
}

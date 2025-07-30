package com.edugreat.akademiksresource.classroom.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.edugreat.akademiksresource.classroom.ClassroomDTO;
import com.edugreat.akademiksresource.model.Student;

public interface ClassroomInterface {
	
	ClassroomDTO createClassroom(ClassroomDTO classroomDTO);
	
	Page<ClassroomDTO> getManagedClassrooms(Integer userId, String userRole, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByInstitution(Integer userId, String userRole, Integer institutionId, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByLevel(Integer userId, String userRole, Integer categoryId, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByLevelAndInstitution(Integer userId, String userRole, Integer institutionId, Integer categoryId, int page, int pageSize);

	void enrollStudents(Integer classroomId, Set<Student> students);
	

	
}

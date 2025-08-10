package com.edugreat.akademiksresource.classroom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.edugreat.akademiksresource.classroom.ClassroomDTO;
import com.edugreat.akademiksresource.classroom.EnrollmentRequest;
import com.edugreat.akademiksresource.classroom.details.ClassroomFullDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSpecificDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSummarizedDetails;
import com.edugreat.akademiksresource.classroom.details.InstructorBasicDetails;
import com.edugreat.akademiksresource.classroom.details.StudentBasicDetails;
import com.edugreat.akademiksresource.util.SubjectAssignmentRequest;

public interface ClassroomInterface {
	
	ClassroomDTO createClassroom(ClassroomDTO classroomDTO);
	
	Page<ClassroomDTO> getManagedClassrooms(Integer userId, String userRole, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByInstitution(Integer userId, String userRole, Integer institutionId, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByLevel(Integer userId, String userRole, Integer categoryId, int page, int pageSize);
	
	Page<ClassroomDTO> getManagedClassroomsByLevelAndInstitution(Integer userId, String userRole, Integer institutionId, Integer categoryId, int page, int pageSize);

	void enrollStudents(EnrollmentRequest enrollmentReq, String role);
	
	ClassroomFullDetails adminClassroomDetails(Integer classroomId, Integer userId, Integer institutionId);

	
	ClassroomSpecificDetails subjectInstructorClassroomDetails(Integer classroomId, Integer userId, Integer institutionId);
	
	ClassroomSummarizedDetails primaryInstructorClassroomDetails(Integer classroomId, Integer userId, Integer institutionId);
	
	ClassroomFullDetails searchAdminClassroom(Integer userId, Integer institutionId, String searchQuery);
	
	ClassroomSummarizedDetails searchPrimaryInstructorClassroom(Integer userId, Integer institutionId, String searchQuery);
	
	ClassroomSpecificDetails searchSubjectInstructorClassroom(Integer userId, Integer institutionId, String searchQuery);
	List<StudentBasicDetails> getEnrolledStudents(Integer classroomId, Integer institutionId);
	List<InstructorBasicDetails> getInstructorsInInstitution(Integer adminId, Integer institutionId);
	void assignSubjectInstructor(List<SubjectAssignmentRequest> subjectAssignments, Integer classroomId);
	void updatePrimaryInstructor(Integer instructorId, Integer classroomId);
}

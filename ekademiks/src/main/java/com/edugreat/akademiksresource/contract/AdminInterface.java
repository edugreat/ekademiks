package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;

/*
 * The contracts declared here are for user with the Admin roles.
 * User with roles not Admin are not expected to invoke the implementations of these contracts
 */
public interface AdminInterface {

	// provides contract that updates student's password
	public void updatePassword(AppUserDTO dto);

	// Searches a user by their email
	public AppUserDTO searchByEmail(String email);

	public List<StudentDTO> allStudents();

	public List<AdminsDTO> allAdmins();

	public void deleteUser(String email);

	public void setSubject(List<SubjectDTO> dtos);

	// sets new test and return its id
	public Integer uploadAssessment(TestDTO testDTO);

	public void addLevels(List<LevelDTO> dtos);

	public Iterable<LevelDTO> findAllLevels();

	public void updateTest(Integer testId, Map<String, Object> updates);// method that updates existing Test object,
																		// intended to use the patch method

	public void createWelcomeMessages(Map<String, Collection<String>> msgs);

	public void deleteStudentAccount(Integer studentId);
	
	public void disableStudentAccount(Integer studentId);
	
	public void enableStudentAccount(Integer studentId);
	
//	Provides capability for modifying questions referenced by the given testId
	public void modifyQuestion(List<QuestionDTO> questions, Integer testId);
	
//	provides capability for deleting a particular question from the assessment using the provided fields
	public void deleteQuestion(Integer testId, Integer questionId);
	
//	provides functionality for modifying the 'assessment topic and assessment duration' for the given assessment id
//	The key of the map is the new assessment topic and value 
	public void modifyAssessment(Map<String, Object> modifiedAssessment, Integer assessmentId);
	
//	provides functionality for deleting a particular assessment from the database
	public void deleteAssessment(Integer testId);
	
}

package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.InstitutionDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.StudentRecord;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.util.AssessmentTopicRequest;

/*
 * The contracts declared here are for user with the Admin roles.
 * User with roles not Admin are not expected to invoke the implementations of these contracts
 */
public interface AdminInterface {

	// provides contract that updates student's password
	 void updatePassword(AppUserDTO dto);

	// Searches a user by their email
	public AppUserDTO searchByEmail(String email);

	 List<StudentDTO> allStudents();

	 List<AdminsDTO> allAdmins();

	public void deleteUser(String email);

	 void setSubjects(List<SubjectDTO> dtos);

	// sets new test and return its id
	 Integer uploadAssessment(TestDTO testDTO);

	 void addLevels(List<LevelDTO> dtos);

	 Iterable<LevelDTO> findAllLevels();

	 void updateTest(Integer testId, Map<String, Object> updates);// method that updates existing Test object,
																		// intended to use the patch method

	 void createWelcomeMessages(Map<String, Collection<String>> msgs);

	 void deleteStudentAccount(Integer studentId);
	
	 void disableStudentAccount(Integer studentId);
	
	 void enableStudentAccount(Integer studentId);
	
//	Provides capability for modifying questions referenced by the given testId
	public void modifyQuestion(List<QuestionDTO> questions, Integer testId);
	
//	provides capability for deleting a particular question from the assessment using the provided fields
	 void deleteQuestion(Integer testId, Integer questionId);
	
//	provides functionality for modifying the 'assessment topic and assessment duration' for the given assessment id
//	The key of the map is the new assessment topic and value is the new duration
	 void modifyAssessment(Map<String, Object> modifiedAssessment, Integer assessmentId);
	
//	provides functionality for deleting a particular assessment from the database
	 void deleteAssessment(Integer testId);
	
//	provides functionality to retrieve all the assessment topics for a given assessment category(e.g senior, junior etc) for editing or deletion purpose.
//	 Key is the assessment ID and value is the assessment name
	Map<Integer, String> getAssessmentTopics(int categoryId);
	
	 

//	provides functionality for updating assessment topic. The key being the old value while the value is the current value.
//	Category represents the assessment category under which the assessment falls
	 void updateAssessmentTopic(AssessmentTopicRequest update);

//	deleted the given assessment from the database
	void deleteAssessment(Integer assessmentId, Integer categoryId);

//	provides functionality for retrieving all assessment subject names as a map whose keys are the assessment categories and values are the subject names
	 Map<String, List<String>> assessmentSubjects();

//	 provides functionality for updating an assessment subject's name, where map's key is the category the subject belongs in,
//	 and value is the new name for subject
	void updateSubjectName(Map<String, String> editedObject, String oldName);

//	provides functionality for deleting an assessment subject
	void deleteSubject(String category, String subjectName);

//	provides functionality for updating assessment category name(referenced by 'previousName') with the current name
	void updateCategoryName(String currentName, String previousName);

//	provides functionality that deletes an assessment category by the category's name
	void deleteCategory(String category);
	
	void registerInstitution(InstitutionDTO institutionDTO);

//	returns a list of institutions registered by the given admin
	List<InstitutionDTO> getInstitutions(Integer adminId);

//	provides method that adds student's records to registered institution
	void addStudentRecords(List<StudentRecord> studentRecords, Integer instutionId);
	
	List<String> getAssessmentNamesFor(List<Integer> studentTestIds);
	
	
}

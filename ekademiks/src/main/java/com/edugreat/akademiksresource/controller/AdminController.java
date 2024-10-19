package com.edugreat.akademiksresource.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AdminInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


/*
 * Only users with the Admin role can call end-points declared in this class
 */
@RestController
@AllArgsConstructor
@RequestMapping("/admins")
//@CrossOrigin("http://localhost:4200")
public class AdminController {

	private final AdminInterface service;

	@GetMapping("/user")
	@JsonView(UserView.class)
	public ResponseEntity<AppUserDTO> searchByEmail(@RequestParam String email) {

		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (Pattern.matches(emailRegex, email))
			return ResponseEntity.ok(service.searchByEmail(email));

		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());

	}

	@GetMapping("/students")
	@JsonView(UserView.class)
	public ResponseEntity<List<StudentDTO>> allStudent() {

		return ResponseEntity.ok(service.allStudents());

	}

	@GetMapping
	public ResponseEntity<Object> admins() {

		return ResponseEntity.ok(service.allAdmins());
	}

	@PutMapping
	public ResponseEntity<Object> updatePassword(@RequestBody @Valid AppUserDTO dto) {

		service.updatePassword(dto);
		return ResponseEntity.ok("Updated");

	}

	@DeleteMapping
	public ResponseEntity<String> deleteUser(@RequestParam String email) {

		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (Pattern.matches(emailRegex, email)) {
			service.deleteUser(email);
			return ResponseEntity.ok("Deleted");
		}

		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());

	}

	@PostMapping("/sub")
	public ResponseEntity<Object> setSubject(@RequestBody List<SubjectDTO> dtos) {

		service.setSubject(dtos);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/update/questions")
	public ResponseEntity<Object> updateQuestion(@RequestBody List<QuestionDTO> questionDTOs, @RequestParam Integer testId) {
		
		
		try {
			
			service.modifyQuestion(questionDTOs, testId);
			
			
			
		} catch (Exception e) {
			
			
			return new ResponseEntity<Object>("Something went wrong !", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	

	// set a new academic test
	@PostMapping("/test")
	public ResponseEntity<Object> uploadAssessment(@RequestBody @Valid TestDTO testDTO) {

		try {
			
			return new ResponseEntity<Object>(service.uploadAssessment(testDTO), HttpStatus.OK);
			
		} catch (Exception e) {
			
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("levels")
	public ResponseEntity<Object> findAll() {

		return new ResponseEntity<Object>(service.findAllLevels(), HttpStatus.OK);
	}

	@PostMapping("/level")
	public ResponseEntity<Object> addLevel(@RequestBody List<LevelDTO> dtos) {

		service.addLevels(dtos);

		return new ResponseEntity<>(HttpStatus.OK);

	}

	// Updates an object of Test such as setting instructions for already persisted
	// test assessments
	@PatchMapping("/test")
	public ResponseEntity<Object> updateTest(@RequestBody Map<String, Object> updates, @RequestParam Integer id) {

		try {
			service.updateTest(id, updates);
		} catch (Exception e) {
			throw new IllegalArgumentException("Something went wrong");
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/welcome")
	public ResponseEntity<Object> postWelcome(@RequestBody Map<String, Collection<String>> welcomeMsg) {

		service.createWelcomeMessages(welcomeMsg);
		return ResponseEntity.ok().build();

	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Object> deleteStudentAccount(@RequestParam String studentId){
		
	
		
		try {
			
		
			Integer id = Integer.parseInt(studentId);
			
           service.deleteStudentAccount(id);
           
           return ResponseEntity.ok().build();
		} catch (Exception e) {
			
			return new ResponseEntity<Object>("Invalid id",HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PatchMapping("/disable")
	public ResponseEntity<Object> disableStudentAccount(@RequestBody Map<String, Integer> map) {
		
		
		try {
			
			final Integer studentId = map.get("studentId");
			
			service.disableStudentAccount(studentId);
			
			return new ResponseEntity<Object>(HttpStatus.OK);
		} catch (Exception e) {
			
			
			return new ResponseEntity<Object>("Invalid id", HttpStatus.BAD_REQUEST);
			
		}
		
		
	}
	
	@PatchMapping("/enable")
	public ResponseEntity<Object> enableStudentAccount(@RequestBody Map<String, Integer> map){
		
		try {
			
			final Integer studentId = map.get("studentId");
			
			service.enableStudentAccount(studentId);
			
			return new ResponseEntity<Object>(HttpStatus.OK);
			
			
		} catch (Exception e) {
		
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@DeleteMapping("/del/question")
	public ResponseEntity<Object> deleteQuestion(@RequestParam Integer testId, @RequestParam Integer questionId){
		
		
		try {
			
		service.deleteQuestion(testId, questionId);
			
			return new ResponseEntity<Object>(HttpStatus.OK);
		} catch (Exception e) {
			
			
			
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
			
		}
		
		
	}
	
	@PatchMapping("/modify/test")
	public ResponseEntity<Object> modifyAssessment(@RequestBody Map<String, Object> modifying, @RequestParam Integer assessmentId){
		
		
		try {
			
			service.modifyAssessment(modifying, assessmentId);
		} catch (Exception e) {
			
			
			
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
		
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@DeleteMapping("/assessment")
	public ResponseEntity<Object> deleteAssessment(@RequestParam Integer testId){
		
		try {
			
			service.deleteAssessment(testId);
		} catch (Exception e) {
			
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	
//	end-point that returns all the assessment topics in a key-value pair data type
	@GetMapping("/topics")
	public ResponseEntity<Map<String, List<String>>> assessmentTopics() {
		
		try {
			return new ResponseEntity<Map<String,List<String>>>(service.getAssessmentTopics(), HttpStatus.OK);
		} catch (Exception e) {
			
			
			throw new IllegalArgumentException("Something went wrong");
		}
		
		
		
	}
	
	@PatchMapping("/edit/topic")
	public ResponseEntity<Object> updateAssessmentTopic(@RequestBody Map<String, String> record, @RequestParam String category){
		
		try {
			
			service.updateAssessmentTopic(record, category);
		} catch (Exception e) {
			
			
			 throw new IllegalArgumentException("Sorry, something went wrong");
		}
		
		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
		
	}
	
	@DeleteMapping("/del/topic")
	public ResponseEntity<Object> deleteAssessment(@RequestParam String category, @RequestParam String topic){
		
		
		try {
			
			service.deleteAssessment(category, topic);
		} catch (Exception e) {
			
			return new ResponseEntity<Object>("Something went wrong!", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@GetMapping("/subjects")
	public ResponseEntity<Object> assessmentSubjectNames(){
		
		
		
		try {
			return new ResponseEntity<Object>(service.assessmentSubjects(), HttpStatus.OK);
		} catch (Exception e) {
			
			return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PatchMapping("/update/subject_name")
	public ResponseEntity<Object> updateSubjectName(@RequestBody Map<String, String> editingObject, @RequestParam String oldName){
		
		try {
			
			service.updateSubjectName(editingObject, oldName);
		} catch (Exception e) {
			
				
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
		
	}
	
	
	@DeleteMapping("/delete/subject")
	public ResponseEntity<Object> deleteSubject(@RequestParam String category, @RequestParam String subjectName){
		
		
		try {
			
			service.deleteSubject(category, subjectName);
			
		} catch (Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@PatchMapping("/update/category")
	public ResponseEntity<Object> updateCategoryName(@RequestBody String currentName, @RequestParam String previousName) {
		
		try {
			service.updateCategoryName(currentName, previousName);
		} catch (Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
		
	}
	
	@DeleteMapping("/delete/category")
	public ResponseEntity<Object> deleteCategory(@RequestParam String category){
		System.out.println("controller");
		try {
			service.deleteCategory(category);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

}

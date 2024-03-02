package com.edugreat.akademiksresource.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.StudentInterface;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.util.AttemptUtil;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("acad/v1/students")
@AllArgsConstructor
public class StudentController {
	
	private final StudentInterface studentService;
	
	

	@GetMapping("/{studentId}/{testId}")
	public ResponseEntity<Object> getScore(@PathVariable("studentId") String stId, 
			@PathVariable("testId") String tId) {
		
		//check if the input path variables match the regular expression for only non white spaced digits
		Pattern pattern = Pattern.compile("^\\d+$");
		
		Matcher m1 = pattern.matcher(stId);
		Matcher m2 = pattern.matcher(tId);
		if(!(m1.matches() && m2.matches())) {
			
			throw new AcademicException("Invalid input '"+stId+"' or '"+tId+"'", Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
		
		Integer studentId = Integer.parseInt(stId);
		Integer testId = Integer.parseInt(tId);
		
		ResponseEntity<Object> response = studentService.getTestScore(studentId, testId);
		
		return response;
		
	}
	
	@GetMapping("/{testId}")
	public Collection<Question> takeTest(@PathVariable("testId") String tId){
		
		//CHECK TO CONFIRM THE ARGUMENT IS A VALID INTEGER
		Pattern p = Pattern.compile("^\\d+$");
		if(!p.matcher(tId).matches()) {
			throw new AcademicException("Invalid input "+tId, Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
		Integer testId = Integer.parseInt(tId);
		
		Collection<Question> questions = null;
		
		questions = studentService.takeTest(testId);
		
		return questions;
		
	}
	
	//receives the test attempt for submission
	@PostMapping("/submit")
	public void submitTest(@Valid @RequestBody AttemptUtil attempt) {
		
		
		try {
			
			studentService.submitTest(attempt);
			
		} catch (ConstraintViolationException e) {
			
			throw new AcademicException("Invalid input detected", Exceptions.ILLEGAL_DATA_FIELD.name());
		}
		
	}
	
	//get all the students in the database
	@GetMapping
	public ResponseEntity<Object> getAll(){
		
		return new ResponseEntity<Object>(studentService.getAll(), HttpStatus.OK);
		
	}
	
	//gets a student by their email
	@GetMapping("/mail")
	public ResponseEntity<Object> findByEmail(@RequestParam("email")String email){
	
		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if(!Pattern.matches(emailRegex, email)){
			throw new AcademicException("Invalid email format '"+email+"'", Exceptions.BAD_REQUEST.name());
		}
		
		return new ResponseEntity<Object>(studentService.findByEmail(email), HttpStatus.OK);
	}
	
	//get a student by their mobile number
	@GetMapping("/ph")
	public ResponseEntity<Object> findByMobile(@RequestParam("mobile")String mobile){
		
		final String mobileRegex = "^(?:\\+234|\\b0)([789]\\d{9})$";
		if(!Pattern.matches(mobileRegex, mobile)) {
			throw new AcademicException("Invalid mobile number format '"+mobile+"'", Exceptions.BAD_REQUEST.name());
		}
		
		
		return new ResponseEntity<>(studentService.findByMobileNumber(mobile), HttpStatus.OK);
		
		
	}
	
	//register a new student
	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody @Valid StudentDTO dto) throws NoSuchAlgorithmException{
	
		
		return new ResponseEntity<>(studentService.registerStudent(dto), HttpStatus.CREATED);
	}
	
	//updates the password of
	@PutMapping("/upd_pass")
	public ResponseEntity<Object> updatePassword(@RequestBody @Valid StudentDTO dto) throws NoSuchAlgorithmException{
		
		studentService.updatePassword(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
				
	}
	
	//updates the student's record, aside the password
	@PutMapping
	public ResponseEntity<Object> update(@RequestBody @Valid StudentDTO dto) throws NoSuchAlgorithmException{
		
		studentService.updateStudent(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	//delete a student from the database
	@DeleteMapping
	public ResponseEntity<Object> delete(@RequestParam("id")Integer id) {
		
		
		studentService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	
	
}

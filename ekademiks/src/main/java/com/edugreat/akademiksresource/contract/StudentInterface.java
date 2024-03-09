package com.edugreat.akademiksresource.contract;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.util.AttemptUtil;

/*
 * Interface which defines contracts for the student
 */
public interface StudentInterface {

	
	//method that serves a collection of questions for the given testId
	public Collection<Question> takeTest(int testId);
	
	//method that submits student's attempt in a test
	public void submitTest(AttemptUtil attempt);
	
	////method that returns the List of student's score in a test. A student could've taken
	//this particular test more than once, so returning a list of their scores and associated time is appropriate a
	public ResponseEntity<Object> getTestScore(int studentId, int testId);
	
	//contract that returns all the students in the database
	public List<StudentDTO> getAll();
	
	//contract that returns a student record by their email
	public StudentDTO findByEmail(String email);
	
	//contract that returns student by their phone number
	public StudentDTO findByMobileNumber(String mobile);
	
	//Contract that registers new student
	public StudentDTO registerStudent(StudentDTO dto) throws NoSuchAlgorithmException;
	
	//provides contract that updates student's password
	public void updatePassword(StudentDTO dto) throws NoSuchAlgorithmException;
	
	//provides contract that updates student's records
	
	public void updateStudent(StudentDTO dto)throws NoSuchAlgorithmException;
	
	//contract for deletion of student by their identity
	public void delete(Integer id);
	
	//for authentication purposes only where a student object is needed internally and not the dto
	public Student searchByEmail(String email);
	
}

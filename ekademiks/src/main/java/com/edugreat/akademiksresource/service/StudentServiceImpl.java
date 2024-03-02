package com.edugreat.akademiksresource.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.StudentInterface;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.StudentTest;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.util.AttemptUtil;

import lombok.RequiredArgsConstructor;

//implementation for the StudentService interface which declares contracts for the Students
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentInterface {

	private final TestDao testDao;
	private final StudentDao studentDao;
	private final StudentTestDao studentTestDao;
	private final ModelMapper mapper;

	private List<OptionLetter> responses = new ArrayList<>();

	
	
	//registers new student and return student dto thereafter
	@Override
	@Transactional
	public StudentDTO registerStudent(StudentDTO dto) throws NoSuchAlgorithmException {
	
		
		//extracts the email field in the dto
		final String email = dto.getEmail();
		
		//extract the phone number field in the dto
		final String mobile = dto.getMobileNumber();
		
		//checks if the record already exist in the database
		final boolean exists = (email == null ? studentDao.existsByMobile(mobile) : studentDao.existsByEmail(email));
		
		//throws exception if the record already exist in the database
		if(exists) {
			throw new AcademicException("Student already exists", Exceptions.BAD_REQUEST.name());
		}
		
		//converts the dto to a student object
		var student = convertToStudent(dto);
		
		//creates new salt
		byte[] salt = createSalt();
		//hash the password with the newly created salt
		byte[] hashedPassword = createPasswordHash(dto.getPassword(), salt);
		student.setStoredSalt(salt);
		student.setStoredHash(hashedPassword);
		
		studentDao.save(student);
		
		return convertToDTO(student);
		
	}
	
	//provides implementation that updates student's password
	//TODO: More functions such as sending otp to phone numbers or email should implemented in the future
	@Override
	@Transactional
	public void updatePassword(StudentDTO dto) throws NoSuchAlgorithmException {
		
		//check if the record exists in the database
		var student = studentDao.findById(dto.getId()).orElseThrow(() ->  new AcademicException("Record not found", Exceptions.BAD_REQUEST.name()));
		
		
		byte[] salt = createSalt();
		byte[] hashedPassword = createPasswordHash(dto.getPassword(), salt);
	    student.setStoredSalt(salt);
	    student.setStoredHash(hashedPassword);
	    
		
		studentDao.save(student);
		
	}
	
	//provides implementation that updates student's records
	//TODO: More functions such as sending otp to phone numbers or email should implemented in the future
	@Override
	@Transactional
	public void updateStudent(StudentDTO dto)throws NoSuchAlgorithmException{
		//check if the student's record exists
		var student = studentDao.findById(dto.getId()).orElseThrow(() -> new AcademicException("Record not found for the student", Exceptions.BAD_REQUEST.name()));
		student.setEmail(dto.getEmail());
		student.setMobileNumber(dto.getMobileNumber());
	
		
	}
	
	@Override
	@Transactional
	public void delete(Integer id) {
		
	boolean exists  = studentDao.existsById(id);
	if(exists) {
		studentDao.deleteById(id);
	}else throw new AcademicException("Intended record does not exist", Exceptions.BAD_REQUEST.name());
		
	}
		

	//finds all the students there are in the database, then map each to the student dto
	@Override
	public List<StudentDTO> getAll() {
		var studentList = new ArrayList<>(studentDao.findAll());
		
	  
		return	 studentList.stream().map(this::convertToDTO).collect(Collectors.toList());
	
	}

	//finds a student by their email address, then converts the object to student dto
	@Override
	public StudentDTO findByEmail(String email) {
		var student = findByOrThrow(email);
		
		return convertToDTO(student);
	}

	//finds a student by their mobile number, then convert to student dto
	@Override
	public StudentDTO findByMobileNumber(String mobile) {
		var student = findByOrThrow(mobile);
		
		return convertToDTO(student);
	}
	
	
	
	// return the questions associated with the test if test exists, else return
	// null
	@Override
	public Collection<Question> takeTest(int testId) {
		Optional<Test> optional = testDao.findById(testId);

		if (optional.isPresent()) {

			return optional.get().getQuestions();
		}

		// the test does not exist in the database
		throw new AcademicException("Test not found", Exceptions.TEST_NOT_FOUND.name());
	}

	@Override
	public ResponseEntity<Object> getTestScore(int studentId, int testId) {

		// check if the student and test records exist
		boolean exist = exist(studentId, testId);

		List<ScoreAndDate> scores = null;

		// fetch the records
		if (exist) {
			scores = studentTestDao.getScore(studentId, testId);

			return new ResponseEntity<>(scores, HttpStatus.FOUND);
		}

		// THROW ACADEMIC EXCEPTION HERE IF RECORD DOES NOT EXIST
		throw new AcademicException("Record not found", Exceptions.RECORD_NOT_FOUND.name());
	}

	@Transactional
	@Override
	public void submitTest(AttemptUtil attempt) {

		// get the student's identifier
		Integer studentId = attempt.getStudentId();

		// get the Test object identifier
		Integer testId = attempt.getTestId();

		// check that the student taking the test exists in the database
		// as well as information for the test they are taking.
		// TODO: Modify this method in the future to allow for non-registered students
		if (exist(studentId, testId)) {

			// get the student who took the test
			Student student = studentDao.findById(studentId).get();

			// get the test that was taken
			Test test = testDao.findById(testId).get();

			// get the list of selected options
			List<String> selectedOptions = attempt.getSelectedOptions();
			// get the time of submission
			LocalDateTime now = LocalDateTime.now();// might review this code later to allow fetching from the front-end

			// add the selected options
			for (String opt : selectedOptions) {
				checkResponse(opt);
			}

			// get the question which the student attempted in the test
			List<Question> questions = new ArrayList<>();
			Set<Question> set = testDao.findById(testId).get().getQuestions();
			questions.addAll(set);

			// Now score the student
			double score = scoreTest(questions, selectedOptions);

			// create new StudentTest object to associate the records with and return the
			// object
			StudentTest studentTest = new StudentTest(score, now, student, test, responses);
			studentTest.setGrade(String.valueOf(2 * score));
			studentTestDao.save(studentTest);

		} 
			// TODO: Modify this declaration in the future to allow for non-registered
			// students
		else
			throw new AcademicException("student and or test information not found",
					Exceptions.RECORD_NOT_FOUND.name());

	}

	// scores a test the student submitted and return their score
	// it takes the Question and selected options just to compare the answer field
	// in the question
	// and the corresponding selected option
	private double scoreTest(List<Question> questions, List<String> selectedOptions) {

		double score = 0.0;

		// sort the questions(implemented to sort by question number)
		Collections.sort(questions);

		List<String> answers = null;

		// map each question in the list to their corresponding answer
		answers = questions.stream().map(Question::getAnswer).collect(Collectors.toList());

		// iterate the two records and increment score if they're same, otherwise just
		// increment by zero
		for (int i = 0; i < selectedOptions.size(); i++) {

			score += (answers.get(i).compareTo(selectedOptions.get(i)) == 0 ? 1 : 0);

		}

		return score;
	}

	// checks if the student and test whose identifiers are provided exist
	private boolean exist(int studentId, int testId) {

		Optional<Student> op1 = studentDao.findById(studentId);

		Optional<Test> op2 = testDao.findById(testId);

		return (op1.isPresent() && op2.isPresent());

	}

	// check options submitted by students if they match with the set of enumerated
	// values, then updates the list of responses made by the student
	private synchronized void checkResponse(String res) {

		// response must be any of the alphabetic letter(A-E). So only one character is
		// allowed
		try {

			if (res.trim().length() == 1) {

				this.responses.add(OptionLetter.valueOf(res));
			} else if (res.trim().length() < 1) {
				// if the student did not provide answer to a question, add NILL to show no
				// option selected
				this.responses.add(OptionLetter.NILL);
			}

		} catch (IllegalArgumentException e) {
			throw new AcademicException("illegal options '"+res+"'", Exceptions.ILLEGAL_DATA_FIELD.name());
		}

	}

	//private helper method that converts a student dto to the actual student object for the purpose of registration
	private Student convertToStudent(StudentDTO dto) {
		
		return mapper.map(dto, Student.class);
	}
	
	//private method that converts a student object to the student dto for the purpose of network request
	private StudentDTO convertToDTO(Student student) {
		
		return mapper.map(student, StudentDTO.class);
	}
	
	//private helper method that retrieves students by their email or phone number
	private Student findByOrThrow(String parameter) {
		
		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		final String mobileRegex = "^(?:\\+234|\\b0)([789]\\d{9})$";
		
		if(Pattern.matches(emailRegex, parameter)) {
			
			final Student s = studentDao.findByEmail(parameter);
			if(s == null)
				throw new AcademicException("No record found for '"+parameter+"'", Exceptions.RECORD_NOT_FOUND.name());
			
			return s;
		}else if(Pattern.matches(mobileRegex, parameter)) {
			
			final Student s = studentDao.findByMobileNumber(parameter);
			
			if(s == null)
				throw new AcademicException("No record found for '"+parameter+"'", Exceptions.RECORD_NOT_FOUND.name());
			return s;
		}
		
		throw new AcademicException("invalid input '"+parameter+"'", Exceptions.BAD_REQUEST.name());
		
	}
	
	//private helper method that creates cryptographic salt for password encryption
	private byte[] createSalt() {
		
		var random = new SecureRandom();
		var salt = new byte[128];
		random.nextBytes(salt);
		
		return salt;
	}
	
	//private method that creates hash keys for password hashing
	private byte[] createPasswordHash(String password, byte[] salt) throws NoSuchAlgorithmException {
		
		
		var messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(salt);
		
		return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
	}

	

}

package com.edugreat.akademiksresource.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.AdminInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.dao.WelcomeMessageDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.embeddable.Options;
import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.model.WelcomeMessage;
import com.edugreat.akademiksresource.util.OptionUtil;

import lombok.AllArgsConstructor;

/*
 * This is the service that implements all the contract declared in the AdminInteerfae interface.
 * Only users with the Admin roles are allowed to invoke services implemented in the this class
 */

@Service
@AllArgsConstructor
public class AdminService implements AdminInterface {
	
	private final StudentDao studentDao;
	private final AdminsDao adminsDao;
	private final ModelMapper mapper;
	private final PasswordEncoder passwordEncoder;
	private final  LevelDao levelDao;
	private final SubjectDao subjectDao;
	private final TestDao testDao;
	private final WelcomeMessageDao welcomeMsgDao;
	

	@Override
	@Transactional
	public void updatePassword(AppUserDTO dto) {
		
		
		
		//check if the user exists in the database
		final boolean isStudent = studentDao.existsByEmail(dto.getEmail());
		if(isStudent) {
			Student student = studentDao.findByEmail(dto.getEmail()).get();
			student.setPassword(passwordEncoder.encode(dto.getPassword()));
			studentDao.save(student);
			return;
			
		}else {
			final boolean isAdmins = adminsDao.existsByEmail(dto.getEmail());
			if(isAdmins) {
				
				Admins admins = adminsDao.findByEmail(dto.getEmail()).get();
				admins.setPassword(passwordEncoder.encode(dto.getPassword()));
				adminsDao.save(admins);
				return;
			}
		}
		
		throw new AcademicException("User not found", Exceptions.RECORD_NOT_FOUND.name());
		
	}

	@Override
	public  AppUserDTO  searchByEmail(String email) {
		
		return searchUser(email);
	}

	@Override
	public List<StudentDTO> allStudents() {
		
		return studentDao.findAll().stream().map(student -> this.mapToStudentDTO(student)).collect(Collectors.toList());
	}

	@Override
	public List<AdminsDTO> allAdmins() {
		
		return adminsDao.findAll().stream().map(admins -> this.mapToAdminsDTO(admins)).collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public void deleteUser(String email) {
		final boolean isStudent = studentDao.existsByEmail(email);
		if(isStudent) {
			//fetch the student object
			var student = studentDao.findByEmail(email).get();
			
			
			studentDao.delete(student);
			
			return;
		}else if(!isStudent) {
			final boolean isAdmin = adminsDao.existsByEmail(email);
			if(isAdmin) {
				var admin = adminsDao.findByEmail(email).get();
				admin.getRoles().removeAll(admin.getRoles());
				adminsDao.delete(admin);
			}
				
		}
		
		
	}

	@Override
	@Transactional
	public SubjectDTO setSubject(SubjectDTO subjectDTO) {

		// get the academic level for the subject
		Category category = Category.valueOf(subjectDTO.getCategory());
		Level level =  levelDao.findByCategory(category);

		if (level == null)
			throw new AcademicException("category, '" + category + "' not found", Exceptions.RECORD_NOT_FOUND.name());
		
		if(subjectDao.subjectExists(subjectDTO.getSubjectName(), level.getId()))
			throw new AcademicException("Subject, "+subjectDTO.getSubjectName()+", exists for the category "+category.name(), Exceptions.BAD_REQUEST.name());
		
		Subject subject = new Subject(subjectDTO.getSubjectName(), level);
		
		
		
		level.addSubject(subject);
		
		subject = subjectDao.save(subject);
		
		
		return convertToDTO(subject);

	}
	
	@Transactional
	@Override
	public void setTest(TestDTO testDTO) {

		// check if test name exists in the database
		Test t = testDao.findByTestName(testDTO.getTestName(), Category.valueOf(testDTO.getCategory()));
		if (t != null) {

			throw new AcademicException(t.getTestName() + " already exists", Exceptions.TEST_ALREADY_EXISTS.name());
		}

		
		// fetches from the database, subject to which the test is associated
		Subject loadedSubject = findSubjectOrThrow(testDTO.getSubjectName(), testDTO.getCategory());
		
		Set<Question> validQuestions = validateQuestions(testDTO.getQuestions());
		
		// map the TestDTO object to test object
		Test validTest = mapper.map(testDTO, Test.class);
		
		validTest.setQuestions(validQuestions);
		
		// performs the bidirectional association between test and question objects
		validQuestions.stream().forEach(x -> x.setTest(validTest));
		
		loadedSubject.addTest(validTest);
		

	}
	
	@Override
	@Transactional
	public void updateTest(Integer testId, Map<String, Object> updates) {
		
		
		//fetch the database,the Test object intended to update
		Optional<Test> optionalTest = testDao.findById(testId);
		
		if(optionalTest.isPresent()) {
			
			Test existingTest = optionalTest.get();
			updates.forEach((k, v) ->{ //for each key-value pair in the map, update the existing Test object
				
				switch (k) {
				case "duration": 
					
					existingTest.setDuration((Integer)v);
					break;
				
				case "testName":
					existingTest.setTestName((String)v);
					break;
				case "instructions":
					existingTest.setInstructions(v);
				}
			});
			
			testDao.save(existingTest);
			return;
		} else {
			
			throw new AcademicException("Record Not Found For ID = "+testId, Exceptions.RECORD_NOT_FOUND.toString());
		}
	}
	
	private Subject findSubjectOrThrow(String subjectName, String category) {

		Subject subj = subjectDao.findBySubjectName(subjectName,  Category.valueOf(category));

		if (subj != null)
			return subj;

		throw new AcademicException("subject, '" + subjectName + "' not found", Exceptions.RECORD_NOT_FOUND.name());
	}
	
	// validates the question dto by validating each of the objections
		// provided in the question and mapping the dto to a question object
		private Set<Question> validateQuestions(Collection<QuestionDTO> dtos) {

			Set<Question> validQuestions = new HashSet<>();
			// for each question in the collection, validate the question and return if
			// valid
			for (QuestionDTO dto : dtos) {
				Set<Options> options = validateOptions(dto.getOptions());// validate and return validated options for each
																			// of the questions

				// map each question dto to question object and associate options to it
				Question mappedQuestion = mapper.map(dto, Question.class);
				mappedQuestion.setOptions(options);
				validQuestions.add(mappedQuestion);

			}

			return validQuestions;
		}
		
		


		// checks if the options supplied by the admin when setting Test.questions are
		// valid options
		// valid options for each question must be any of the alphabets Q-E.
		// validate and return successfully validated options, throw exception if
		// validation fails
		private Set<Options> validateOptions(List<OptionUtil> options) {

			Set<Options> validOptions = new HashSet<>();
			try {

				for (OptionUtil option : options) {
					OptionLetter.valueOf(option.getLetter());// validate the options, can throw exception if validations fails
					validOptions.add(mapper.map(option, Options.class));
				}

			} catch (IllegalArgumentException e) {
				validOptions = null;// for garbage collection
				throw new AcademicException("illegal option", Exceptions.ILLEGAL_DATA_FIELD.name());
			}

			return validOptions;
		}



	
	private SubjectDTO convertToDTO(Subject subject) {
		
		return new SubjectDTO(subject.getId(), subject.getSubjectName(), subject.getLevel().getCategory().name());
	}



	private AppUserDTO searchUser(String username) {
		
		final boolean isStudent = studentDao.existsByEmail(username);
		if(isStudent)
			return this.mapToStudentDTO(studentDao.findByEmail(username).get());
		else if(! isStudent) return this.mapToAdminsDTO(adminsDao.findByEmail(username).get());
		
		else throw new AcademicException("user not found", Exceptions.RECORD_NOT_FOUND.name());
	}
	
	private StudentDTO mapToStudentDTO(Student student) {
		
		return mapper.map(student, StudentDTO.class);
		
	}
	
	private AdminsDTO mapToAdminsDTO(Admins admins) {
		
		return mapper.map(admins, AdminsDTO.class);
	}
	
	
	
	@Override
	@Transactional
	public LevelDTO addLevel(LevelDTO dto) {
		Level level = null;

		try {
			
			// verifies that the parameter is a valid allowable category. Can throw exception on attempt to provide invalid enum type
				Category.valueOf(dto.getCategory());
				//check if Level object for that category already exists in the database
				if(levelDao.existsByCategory(Category.valueOf(dto.getCategory()))) {
					throw new AcademicException("Record for level '"+dto.getCategory()+"' exists", Exceptions.BAD_REQUEST.name());
					
				}
				 
				level = mapper.map(dto, Level.class);
				

			
			
             //batch persist the levels to the database and map the returned
			//record to the data transfer object
			level = levelDao.save(level);

		} catch (IllegalArgumentException e) {
			throw new AcademicException("Illegal parameter for category", Exceptions.BAD_REQUEST.name());
		}

		return mapper.map(level, LevelDTO.class);
	}
	
	
	
	@Override
	public Iterable<LevelDTO> findAllLevels(){
		
		
		return levelDao.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	private LevelDTO convertToDTO(Level level) {

		return mapper.map(level, LevelDTO.class);
	}

	@Transactional
	@Override
	public void createWelcomeMessages(Map<String, Collection<String>> msgs) {
		
	 WelcomeMessage welcome = new WelcomeMessage();
	 msgs.forEach((k, v) ->{
		 
		 switch (k) {
		case "welcomeMsg": Set<String> filteredMsgs = v.stream()
				 .filter(msg -> patternMatch(msg)).collect(Collectors.toSet());
		 
		 filteredMsgs.forEach(x -> welcome.addMessage(x));
		 
		 welcomeMsgDao.save(welcome);
			
			break;
		}
	 });
	}
	
	
	
	private boolean patternMatch(String msg) {
		
		return Pattern.matches("^[A-Za-z0-9\s,;:!.'\"-]+[.!?]*$", msg);
	}

	


	

}

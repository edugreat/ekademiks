package com.edugreat.akademiksresource.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AdminInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dao.QuestionDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.dao.WelcomeMessageDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.InstitutionDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.StudentRecord;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.embeddable.Options;
import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.model.WelcomeMessage;
import com.edugreat.akademiksresource.util.OptionUtil;

import jakarta.transaction.Transactional;
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
	private final LevelDao levelDao;
	private final SubjectDao subjectDao;
	private final TestDao testDao;
	private final WelcomeMessageDao welcomeMsgDao;
	private final QuestionDao questionDao;

	private final InstitutionDao institutionDao;

	@Autowired
	private CacheManager cacheManager;
	

	@Override
	@Transactional
	@CacheEvict(value = RedisValues.USER_CACHE, allEntries = true)
	public void updatePassword(AppUserDTO dto) {

		// check if the user exists in the database
		final boolean isStudent = studentDao.existsByEmail(dto.getEmail());
		if (isStudent) {
			Student student = studentDao.findByEmail(dto.getEmail()).get();
			student.setPassword(passwordEncoder.encode(dto.getPassword()));
			studentDao.save(student);
			return;

		} else {
			final boolean isAdmins = adminsDao.existsByEmail(dto.getEmail());
			if (isAdmins) {

				Admins admins = adminsDao.findByEmail(dto.getEmail()).get();
				admins.setPassword(passwordEncoder.encode(dto.getPassword()));
				adminsDao.save(admins);
				return;
			}
		}

		throw new AcademicException("User not found", Exceptions.RECORD_NOT_FOUND.name());

	}

	@Override
	public AppUserDTO searchByEmail(String email) {

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
		if (isStudent) {
			// fetch the student object
			var student = studentDao.findByEmail(email).get();

			studentDao.delete(student);

			return;
		} else if (!isStudent) {
			final boolean isAdmin = adminsDao.existsByEmail(email);
			if (isAdmin) {
				var admin = adminsDao.findByEmail(email).get();
				admin.getRoles().removeAll(admin.getRoles());
				adminsDao.delete(admin);
			}

		}

	}

	@Override
	@Transactional
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void setSubject(List<SubjectDTO> subjectDTOs) {

		List<Subject> subjects = new ArrayList<>();
		// get the academic level for the subject, check if any of the subjects already
		// exists for any of the specified levels.
		// Throw exception if any requirement fails. This is to ensure all passes
		// requirements or non gets persisted.
		subjectDTOs.forEach(dto -> {

			Category category = Category.valueOf(dto.getCategory());

			final Level level = levelDao.findByCategory(category);

			if (level == null) {

				throw new AcademicException("category '" + category + "' not found ",
						Exceptions.RECORD_NOT_FOUND.name());
			}
			// Check if the subject already exists for that particular level
			if (subjectDao.subjectExists(dto.getSubjectName(), level.getId())) {
				throw new AcademicException("subject, " + dto.getSubjectName() + ", exists for " + category.name(),
						Exceptions.BAD_REQUEST.name());
			}

		});

		subjectDTOs.forEach(dto -> {

			Category category = Category.valueOf(dto.getCategory());

			final Level level = levelDao.findByCategory(category);

			Subject subject = new Subject(dto.getSubjectName(), level);

			level.addSubject(subject);
			subjects.add(subject);

			subjectDao.save(subject);

		});

	}

	@Transactional
	@Override
	public void deleteStudentAccount(Integer studentId) {

		final Optional<Student> optional = studentDao.findById(studentId);
		if (!optional.isEmpty()) {

			Student student = optional.get();
			studentDao.delete(student);
		}

	}

	// provides implementation for modifying the content of the questions referenced
	// by the given
	// testId
	@Override
	@Transactional
	public void modifyQuestion(List<QuestionDTO> dtos, Integer testId) {
		// Get the test referenced by testId
		Optional<Test> optional = testDao.findById(testId);
		if (optional.isPresent()) {
			Test test = optional.get();

			// Get the questions contained in the test
			Set<Question> questions = test.getQuestions();

			dtos.forEach(dto -> {
				// Filter the questions that need update
				Question updatableQuestion = questions.stream()
						.filter(q -> q.getQuestionNumber() == dto.getQuestionNumber()).findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Question not found"));

				// Update the question's fields
				updatableQuestion.setAnswer(dto.getAnswer());
				updatableQuestion.setQuestion(dto.getQuestion());

				// Update the options in the updatableQuestion with the options provided by
				// 'dto'
				Set<Options> updatableOptions = new TreeSet<>();
				for (OptionUtil currentOption : dto.getOptions()) {
					Options option = new Options();
					option.setLetter(OptionLetter.valueOf(currentOption.getLetter()));
					option.setText(currentOption.getText());

					updatableOptions.add(option);
				}

				// Update the options directly in the existing question
				updatableQuestion.setOptions(updatableOptions);
			});

			testDao.saveAndFlush(test);
		}
	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void modifyAssessment(Map<String, Object> modifiedAssessment, Integer assessmentId) {

		// get the assessment to be modified
		Test test = testDao.findById(assessmentId)
				.orElseThrow(() -> new IllegalArgumentException("Assessment not found!"));

		// not minding the use of forEach, the map contains just one object of whose
		// key, value pair represent the 'topic' and 'duration'
		modifiedAssessment.forEach((topic, duration) -> {

			test.setTestName(String.valueOf(modifiedAssessment.get("topic")));
			test.setDuration(Long.valueOf(modifiedAssessment.get("duration").toString()));
		});

		testDao.saveAndFlush(test);

	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void deleteQuestion(Integer testId, Integer questionId) {

		// fetch from the database, the Test object referred to by testId
		Optional<Test> optional = testDao.findById(testId);

		if (optional.isPresent()) {

			// Get the Test object
			Test test = optional.get();

			// get the question to be deleted

			Question staleQuestion = test.getQuestions().stream().filter(q -> q.getId() == questionId).findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Question not found!"));

			// delete the question from the set of questions in the Test
			test.getQuestions().remove(staleQuestion);

			questionDao.delete(staleQuestion);

			testDao.saveAndFlush(test);

		}

	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void deleteAssessment(Integer testId) {

//		get the assessment to delete
		Test test = testDao.findById(testId).orElseThrow(() -> new IllegalArgumentException("Record not found!"));

//		get the subject associated to this assessment
		Subject associatedSubject = test.getSubject();

//		break the relationship
		associatedSubject.getTest().remove(test);

//		delete the test finally
		testDao.delete(test);

		subjectDao.saveAndFlush(associatedSubject);

	}

	@Override
	@Transactional
	public void disableStudentAccount(Integer studentId) {

		// Disables a student's account

		Optional<Student> optional = studentDao.findById(studentId);
		if (optional.isPresent()) {

			Student student = optional.get();

			student.setAccountEnabled(false);
			studentDao.save(student);

		}
	}

	@Override
	@Transactional
	public void enableStudentAccount(Integer studentId) {

		Optional<Student> optional = studentDao.findById(studentId);

		if (optional.isPresent()) {

			Student student = optional.get();

			student.setAccountEnabled(true);
			studentDao.save(student);
		}
	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public Integer uploadAssessment(TestDTO testDTO) {

		// check if test name exists in the database
		Test t = testDao.findByTestNameAndCategory(testDTO.getTestName(), Category.valueOf(testDTO.getCategory()));
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

		// return the id of the just uploaded test assessment
		Integer id = findId(testDTO.getTestName(), Category.valueOf(testDTO.getCategory()));
		if (id == null)
			throw new IllegalArgumentException("Invalid request");

		return id;

	}

	// returns the id of a test assessment using its information
	private Integer findId(String testName, Category category) {

		return testDao.findId(testName, category);
	}

	@Override
	@Transactional
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void updateTest(Integer testId, Map<String, Object> updates) {

		// fetch the database,the Test object intended to update
		Optional<Test> optionalTest = testDao.findById(testId);

		if (optionalTest.isPresent()) {

			Test existingTest = optionalTest.get();
			updates.forEach((k, v) -> { // for each key-value pair in the map, update the existing Test object

				switch (k) {
				case "duration":

					existingTest.setDuration((Integer) v);
					break;

				case "testName":
					existingTest.setTestName((String) v);
					break;
				case "instructions":
					existingTest.setInstructions(v);
				}
			});

			testDao.save(existingTest);
			return;
		}
	}

	private Subject findSubjectOrThrow(String subjectName, String category) {

		Subject subj = subjectDao.findBySubjectNameAndCategory(subjectName, Category.valueOf(category));

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
				OptionLetter.valueOf(option.getLetter());// validate the options, can throw exception if validations
				// fails
				validOptions.add(mapper.map(option, Options.class));
			}

		} catch (IllegalArgumentException e) {
			validOptions = null;// for garbage collection
			throw new AcademicException("illegal option", Exceptions.ILLEGAL_DATA_FIELD.name());
		}

		return validOptions;
	}

	private AppUserDTO searchUser(String username) {

		final boolean isStudent = studentDao.existsByEmail(username);
		if (isStudent)
			return this.mapToStudentDTO(studentDao.findByEmail(username).get());
		else if (!isStudent)
			return this.mapToAdminsDTO(adminsDao.findByEmail(username).get());

		else
			throw new AcademicException("user not found", Exceptions.RECORD_NOT_FOUND.name());
	}

	private StudentDTO mapToStudentDTO(Student student) {

		return mapper.map(student, StudentDTO.class);

	}

	private AdminsDTO mapToAdminsDTO(Admins admins) {

		return mapper.map(admins, AdminsDTO.class);
	}

	@Override
	@Transactional
	@Caching(evict = { @CacheEvict(value = RedisValues.SUBJECT_NAMES, allEntries = true),
			@CacheEvict(value = RedisValues.TOPICS_AND_DURATIONS, allEntries = true) })
	public void addLevels(List<LevelDTO> dtos) {

		try {

			// verifies that the parameter is a valid allowable category. Can throw
			// exception on attempt to provide invalid enum type
			dtos.forEach(dto -> Category.valueOf(dto.getCategory()));

			// check if Level object for that category already exists in the database

			dtos.forEach(dto -> {
				if (levelDao.existsByCategory(Category.valueOf(dto.getCategory()))) {
					throw new AcademicException("Record for level '" + dto.getCategory() + "' exists",
							Exceptions.BAD_REQUEST.name());
				}
			});

			List<Level> levels = new ArrayList<>();
			dtos.forEach(dto -> {

				Level level = mapper.map(dto, Level.class);
				levels.add(level);

			});

			// batch persist the levels to the database and map the returned
			// record to the data transfer object
			if (levels.size() > 0) {
				levelDao.saveAll(levels);
			}

		} catch (IllegalArgumentException e) {
			throw new AcademicException("Illegal parameter for category", Exceptions.BAD_REQUEST.name());
		}

	}

	@Override
	public Iterable<LevelDTO> findAllLevels() {

		return levelDao.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private LevelDTO convertToDTO(Level level) {

		return mapper.map(level, LevelDTO.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = RedisValues.WELCOME_MSG)
	public void createWelcomeMessages(Map<String, Collection<String>> msgs) {

		WelcomeMessage welcome = new WelcomeMessage();
		msgs.forEach((k, v) -> {

			switch (k) {
			case "welcomeMsg":
				Set<String> filteredMsgs = v.stream().filter(msg -> patternMatch(msg)).collect(Collectors.toSet());

				filteredMsgs.forEach(x -> welcome.addMessage(x));

				welcomeMsgDao.save(welcome);

				break;
			}
		});
	}

	private boolean patternMatch(String msg) {

		return Pattern.matches("^[A-Za-z0-9\s,;:!.'\"-]+[.!?]*$", msg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getAssessmentTopics() {

		Map<String, List<String>> map = new TreeMap<>();

		Cache juniorCategoryCache = cacheManager.getCache(RedisValues.ASSESSMENT_TOPICS);
//		get all assessment topics for junior assessments
		List<String> juniorCategory = null;

		if (juniorCategoryCache != null && juniorCategoryCache.get("JUNIOR") != null) {

			Cache.ValueWrapper valueWrapper = juniorCategoryCache.get("JUNIOR");
			juniorCategory = new ArrayList<>((List<String>) valueWrapper);
		} else {

			juniorCategory = testDao.getTopicsFor(Category.JUNIOR);
		}

		Cache seniorCategoryCache = cacheManager.getCache(RedisValues.ASSESSMENT_TOPICS);

		List<String> seniorCategory = null;

		if (seniorCategoryCache != null && juniorCategoryCache.get("SENIOR") != null) {

			Cache.ValueWrapper valueWrapper = juniorCategoryCache.get("SENIOR");
			seniorCategory = new ArrayList<>((List<String>) valueWrapper);
		} else {

			seniorCategory = testDao.getTopicsFor(Category.SENIOR);
		}

		testDao.getTopicsFor(Category.SENIOR);

		if (juniorCategory.size() > 0) {

			Collections.sort(juniorCategory);
			map.put("JUNIOR", juniorCategory);

		}

		if (seniorCategory.size() > 0) {

			Collections.sort(seniorCategory);
			map.put("SENIOR", seniorCategory);
		}

		if (!map.isEmpty()) {

			return map;
		}
		;

		return null;
	}

	@Override
	@Transactional
	public void updateAssessmentTopic(Map<String, String> record, String category) {

		record.forEach((oldName, newName) -> {

			Test updatableTest = testDao.findByTestNameAndCategory(oldName, Category.valueOf(category));
			if (updatableTest == null) {

				throw new IllegalArgumentException("Record not found!");
			}

			updatableTest.setTestName(newName);
			testDao.saveAndFlush(updatableTest);

		});

	}

	@Override
	@Transactional
	public void deleteAssessment(String category, String topic) {

		Test deletable = testDao.findByTestNameAndCategory(topic, Category.valueOf(category));

		if (deletable == null)
			throw new IllegalArgumentException("Record does not exist!");

		testDao.delete(deletable);

	}

	@Override
	public Map<String, List<String>> assessmentSubjects() {

		Map<String, List<String>> subjectNames = new TreeMap<>();

		List<String> subjectNamesForJuniorCategory = subjectDao.findSubjectNamesByCategory(Category.valueOf("JUNIOR"));

		if (subjectNamesForJuniorCategory.size() > 0) {

			Collections.sort(subjectNamesForJuniorCategory);

			subjectNames.put("JUNIOR", subjectNamesForJuniorCategory);
		}

		List<String> subjectNamesForSeniorCategory = subjectDao.findSubjectNamesByCategory(Category.valueOf("SENIOR"));

		if (subjectNamesForSeniorCategory.size() > 0) {

			Collections.sort(subjectNamesForSeniorCategory);
			subjectNames.put("SENIOR", subjectNamesForSeniorCategory);
		}

		return subjectNames;

	}

	@Override
	@Transactional
	public void updateSubjectName(Map<String, String> editedObject, String oldName) {
		editedObject.forEach((category, subjectName) -> {

			Subject updatableSubject = subjectDao.findBySubjectNameAndCategory(oldName, Category.valueOf(category));

			if (updatableSubject != null) {

				updatableSubject.setSubjectName(subjectName);

//			evicts previously cached object of subject names
				Cache cache = cacheManager.getCache(RedisValues.SUBJECT_NAMES);

				if (cache != null) {
					cache.evict(category);
				}

			} else
				throw new IllegalArgumentException("No record found for update");

		});

	}

	@Override
	@Transactional
	public void deleteSubject(String category, String subjectName) {

//			check if the record exists in the database
		final boolean recordExists = subjectDao.subjectExists(subjectName, Category.valueOf(category));

		if (!recordExists)
			throw new IllegalArgumentException("No such record !");

		var subject = subjectDao.findBySubjectNameAndCategory(subjectName, Category.valueOf(category));

		subjectDao.delete(subject);

	}

	@Override
	@Transactional
	public void updateCategoryName(String currentName, String previousName) {

//		verify the supplied category is allowed
		try {

			Category.valueOf(currentName);

			final boolean exists = levelDao.existsByCategory(Category.valueOf(previousName));

			if (!exists)
				throw new IllegalArgumentException("No record for " + previousName);

			var updatableCategory = levelDao.findByCategory(Category.valueOf(previousName));

			updatableCategory.setCategory(Category.valueOf(currentName));

			levelDao.saveAndFlush(updatableCategory);

		} catch (Exception e) {

			throw new IllegalArgumentException(e);

		}

	}

	@Override
	@Transactional
	public void deleteCategory(String category) {

		try {

//	check the existence of the record
			if (!levelDao.existsByCategory(Category.valueOf(category)))
				throw new IllegalArgumentException("Record not found");

			levelDao.deleteByCategory(Category.valueOf(category));

		} catch (Exception e) {

			throw new IllegalArgumentException(e);

		}

	}

	@Transactional
	@Override
	public void registerInstitution(InstitutionDTO institutionDTO) {
		// check if the institution already exists in the database
		Optional<Institution> optionalInstitution = institutionDao.findByNameAndLocalGovt(institutionDTO.getName(),
				institutionDTO.getLocalGovt());

		if (optionalInstitution.isPresent())
			throw new IllegalArgumentException("already exists");

		try {
			Institution institution = mapToInstitution(institutionDTO);

			institutionDao.save(institution);
		} catch (Exception e) {

			throw e;
		}

	}

	private Institution mapToInstitution(InstitutionDTO dto) {

		Institution institution = new Institution(dto.getName(), dto.getState(), dto.getLocalGovt(),
				dto.getCreatedBy());

		return institution;

	}

	@Override
	public List<InstitutionDTO> getInstitutions(Integer adminId) {

		List<Institution> institutions = institutionDao.findByCreatedByOrderByNameAsc(adminId);

		if (!institutions.isEmpty())
			return institutions.stream().map(this::mapToDTO).collect(Collectors.toList());

		return List.of();
	}

	private InstitutionDTO mapToDTO(Institution institution) {

		return new InstitutionDTO(institution.getId(), institution.getName(), institution.getStudentPopulation());

	}

	@Transactional
	@Override
	public void addStudentRecords(List<StudentRecord> studentRecords, Integer institutionId) {

//		get the institution from the database
		final Institution institution = institutionDao.findById(institutionId)
				.orElseThrow(() -> new IllegalArgumentException("Institution does not exist"));

//		get the students already registered
		final List<Student> students = institution.getStudentList();

		final List<Student> verifiedRecords = verifyStudentRecords(studentRecords);

		for (Student s : verifiedRecords) {

			final boolean successful = institution.addStudent(students, s);

			if (!successful)
				throw new IllegalArgumentException("some records already exist");

		}

		studentDao.saveAllAndFlush(verifiedRecords);

		institutionDao.saveAndFlush(institution);

	}

//	processes the given student records against the details in the database. Returns true if successful or false otherwise 
	private final List<Student> verifyStudentRecords(List<StudentRecord> records) {

		List<Student> students = new ArrayList<>();

//		use each information in the record to fetch the student from the database
		for (StudentRecord r : records) {

			Optional<Student> op = studentDao.findByEmail(r.email());

			// throws exception if the student does not exist or the supplied password does
			// not match with the actual password in the database
			if (op.isEmpty() || !(passwordEncoder.matches(r.password(), op.get().getPassword())))
				throw new IllegalArgumentException("Email and or password error");

			students.add(op.get());

		}
		return students;

	}

}

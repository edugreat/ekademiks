package com.edugreat.akademiksresource.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.dao.WelcomeMessageDao;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.StudentTest;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.projection.TopicAndDuration;

import com.edugreat.akademiksresource.util.AttemptUtil;
import lombok.RequiredArgsConstructor;

//Test service implementation class that provides implementation for Test interface
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestInterface {

	private final TestDao testDao;

	private final StudentTestDao studentTestDao;

	private final SubjectDao subjectDao;

	private final ModelMapper mapper;

	private final WelcomeMessageDao welcomeMsgDao;

	private final StudentDao studentDao;

	// helper method that gets from the database test whose id is given
	private Test getTest(Integer id) {

		Optional<Test> optional = testDao.findById(id);

		if (optional.isEmpty()) {

			throw new AcademicException("Record not found for test id: " + id, Exceptions.RECORD_NOT_FOUND.name());

		}

		return testDao.findById(id).get();

	}

	@Override
	public Collection<String> getWelcomeMessages() {

		return welcomeMsgDao.findAllMessages();
	}

	@Override
	// returns a test wrapper containing all questions for the given test id
	public TestWrapper takeTest(Integer testId) {
		TestWrapper wrapper = new TestWrapper();
		// we may need some attributes of this test object in the future(eg test
		// duration etc)
		Test test = getTest(testId);

		// get all the questions associated with the test
		Collection<Question> questions = test.getQuestions();

		// map each of the questions to dto and add to the test wrapper class
		// also map each of the options contained in the question to optionUtil object
		for (Question question : questions) {
			QuestionDTO questionDTO = mapper.map(question, QuestionDTO.class);

			wrapper.addQuestion(questionDTO);

		}

		// Get the instructions for this Test
		Collection<String> instructions = test.getInstructions();
		// add the instructions to the wrapper object
		wrapper.addInstructions(instructions);

		return wrapper;
	}

	// fetches all the subject names for the given academic level
	@Override
	public List<String> testSubjectFor(String level) {
		// return all subjects for the given level,then map to their respective subject
		// names
		return subjectDao.findSubjectNamesByCategory(Category.valueOf(level));
	}

	@Transactional
	@Override
	public String submitTest(AttemptUtil attempt) {

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
			
//			check if the student has taken this particular assessment before, if yes, delete the old
			StudentTest previousTest = studentTestDao.findByStudentAndTest(student.getId(), test.getId());
			if(previousTest != null) {
				
				student.getStudentTests().remove(previousTest);
				
//				
			}
			

			// get the time of submission
			LocalDateTime now = LocalDateTime.now();// might review this code later to allow fetching from the front-end

			List<OptionLetter> responses = new ArrayList<>();
			// add the selected options
			for (String opt : selectedOptions) {
				checkResponse(opt, responses);
			}

			// get the question which the student attempted in the test
			List<Question> questions = new ArrayList<>();
			Set<Question> questionSet = testDao.findById(testId).get().getQuestions();
			
//			get the total number of questions asked
			final int totalQuestionsAsked = questionSet.size();
			
			
			questions.addAll(questionSet);

			// Now score the student
			double score = scoreTest(questions, selectedOptions);
			
//			Compute the student's average score
		final double averageScore = 	computeAverageScore(totalQuestionsAsked, score);

			// create new StudentTest object to associate the records with and return the
			// object
			StudentTest studentTest = new StudentTest(score, now, student, test, responses);
			studentTest.setGrade(String.valueOf(averageScore));
			studentTestDao.save(studentTest);

			return "Submitted!";

		}
		// TODO: Modify this declaration in the future to allow for non-registered
		// students
		else
			throw new AcademicException("student and or test information not found",
					Exceptions.RECORD_NOT_FOUND.name());

	}
	
//	Computes student's average assessment score
	private double computeAverageScore(int totalQuestionsAsked, double score) {
		
		
		return Math.ceil(score * 100) / totalQuestionsAsked;
		
	}

	// implements the testTopics method of the interface
	@Override
	public List<TopicAndDuration> testTopicsAndDurations(String subject, String category) {
		return testDao.findAllTopicsAndDurations(subject, Category.valueOf(category));

	}

	@Override
	public TopicAndDuration testTopicAndDuration(Integer testId) {

		return testDao.retrieveTopicAndurationById(testId);
	}

	// implements the interface method to return a list of questions for the given
	// argument
	@Override
	public TestWrapper takeTest(String topic, String category) {

		Test test = testDao.findByTestNameAndCategory(topic, Category.valueOf(category));

		Collection<Question> questions = null;
		if (test != null) {

			TestWrapper wrapper = new TestWrapper();

			questions = test.getQuestions();

			questions.forEach(question -> wrapper.addQuestion(mapper.map(question, QuestionDTO.class)));
			// get the instructions for this particular test
			Collection<String> instructions = testDao.getInstructionsFor(topic, Category.valueOf(category));
			if (instructions.size() > 0) {
				wrapper.addInstructions(instructions);

				wrapper.setTestId(test.getId());
			}

			return wrapper;
		}

		return null;
	}

//	Implementation that return a map object whose key is a subject name and value is a category name the subject belongs to, using a given test id
	@Override
	public Map<String, String> subjectAndCategory(int testId) {

		String subjectName = testDao.findSubjectNameByTestId(testId);

		if (subjectName == null)
			throw new IllegalArgumentException("Record not found");

		String categoryName = testDao.findCategoryNameByTestId(testId).name();
		Map<String, String> map = new HashMap<>();
		map.put(subjectName, categoryName);

		return map;
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

			if (selectedOptions.get(i) == null)
				continue; // skips the option if the student did not choose anything

			score += (answers.get(i).compareTo(selectedOptions.get(i)) == 0 ? 1 : 0);

		}

		return score;
	}

	// check options submitted by students if they match with the set of enumerated
	// values, then updates the list of responses made by the student
	private synchronized void checkResponse(String res, List<OptionLetter> responses) {

		final List<String> ALLOWABLE_OPTIONS = List.of("A", "B", "C", "D", "E");

		// response must be any of the alphabetic letter(A-E). So only one character is
		// allowed
		try {

			if (res != null && ALLOWABLE_OPTIONS.stream().anyMatch(res::equals)) {

				responses.add(OptionLetter.valueOf(res));
			} else {
				// if the student did not provide answer to a question, add NILL to show no
				// option selected
				responses.add(OptionLetter.NILL);
			}

		} catch (IllegalArgumentException e) {
			throw new AcademicException("illegal options '" + res + "'", Exceptions.ILLEGAL_DATA_FIELD.name());
		}

	}

	// checks if the student and test whose identifiers are provided exist
	private boolean exist(int studentId, int testId) {

		Optional<Student> op1 = studentDao.findById(studentId);

		Optional<Test> op2 = testDao.findById(testId);

		return (op1.isPresent() && op2.isPresent());

	}

}

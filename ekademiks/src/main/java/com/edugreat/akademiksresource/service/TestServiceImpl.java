package com.edugreat.akademiksresource.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.embeddable.Options;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.util.OptionUtil;

import lombok.RequiredArgsConstructor;

//Test service implementation class that provides implementation for Test interface
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestInterface {

	private final TestDao testDao;

	private final StudentTestDao studentTestDao;

	private final SubjectDao subjectDao;
	private final ModelMapper mapper;

	@Transactional
	@Override
	public void setTest(TestDTO testDTO) {

		// check if test name exists in the database
		Test t = testDao.findByTestName(testDTO.getTestName());
		if (t != null) {

			throw new AcademicException(t.getTestName() + " already exists", Exceptions.TEST_ALREADY_EXISTS.name());
		}

		// fetches from the database, subject to which the test is associated
		Subject loadedSubject = findSubjectOrThrow(testDTO.getSubjectName());
		Set<Question> validQuestions = validateQuestions(testDTO.getQuestions());

		// map the TestDTO object to test object
		Test validTest = mapper.map(testDTO, Test.class);
		validTest.setQuestions(validQuestions);
		// performs the bidirectional association between test and question objects
		validQuestions.stream().forEach(x -> x.setTest(validTest));
		loadedSubject.addTest(validTest);

	}

	// helper method that gets from the database test whose id is given
	private Test getTest(Integer id) {

		Optional<Test> optional = testDao.findById(id);

		if (!optional.isPresent()) {

			throw new AcademicException("Record not found for test id: " + id, Exceptions.RECORD_NOT_FOUND.name());

		}

		return testDao.findById(id).get();

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

		return wrapper;
	}

	@Override
	public List<ScoreAndDate> getScore(int studentId, int testId) {

		List<ScoreAndDate> scores = studentTestDao.getScore(studentId, testId);

		if (!scores.isEmpty()) {

			return scores;
		}

		// TODO: Throw exception with descriptive message if list is null to indicate
		// there's not records for the given arguments

		return null;
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
				OptionLetter.valueOf(option.getLetter());// validate the options, can throw exception is validations
															// fails
				validOptions.add(mapper.map(option, Options.class));
			}

		} catch (IllegalArgumentException e) {
			validOptions = null;// for garbage collection
			throw new AcademicException("illegal option", Exceptions.ILLEGAL_DATA_FIELD.name());
		}

		return validOptions;
	}

	private Subject findSubjectOrThrow(String subjectName) {

		Subject subj = subjectDao.findBySubjectName(subjectName);

		if (subj != null)
			return subj;

		throw new AcademicException("subject, '" + subjectName + "' not found", Exceptions.RECORD_NOT_FOUND.name());
	}

	

}

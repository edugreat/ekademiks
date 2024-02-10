package com.edugreat.akademiksresource.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.StudentService;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.StudentTest;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.util.AttemptUtil;

//implementation for the StudentService interface which declares contracts for the Students
@Service
public class StudentServiceImpl implements StudentService {

	private TestDao testDao;
	private StudentDao studentDao;
	private StudentTestDao studentTestDao;

	private List<OptionLetter> response = new ArrayList<>();

	public StudentServiceImpl(TestDao testDao, StudentDao studentDao, StudentTestDao studentTestDao) {
		this.testDao = testDao;
		this.studentDao = studentDao;
		this.studentTestDao = studentTestDao;
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
			StudentTest studentTest = new StudentTest(score, now, student, test, response);
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
	// values.
	private synchronized void checkResponse(String res) {

		// response must be any of the alphabetic letter(A-E). So only one character is
		// allowed
		try {

			if (res.trim().length() == 1) {

				this.response.add(OptionLetter.valueOf(res));
			} else if (res.trim().length() < 1) {
				// if the student did not provide answer to a question, add NILL to show no
				// option selected
				this.response.add(OptionLetter.NILL);
			}

		} catch (IllegalArgumentException e) {
			//System.out.println("option " + res + " not allowed!");
		}

	}

}

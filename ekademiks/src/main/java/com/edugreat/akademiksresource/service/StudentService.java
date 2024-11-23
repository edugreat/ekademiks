package com.edugreat.akademiksresource.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.StudentInterface;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;

import lombok.RequiredArgsConstructor;

//implementation for the StudentService interface which declares contracts for the Students
@Service
@RequiredArgsConstructor
public class StudentService implements StudentInterface {

	private final TestDao testDao;
	private final StudentTestDao studentTestDao;
	

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
	public List<ScoreAndDate> getScore(int studentId, int testId) {

		List<ScoreAndDate> scores = studentTestDao.getScore(studentId, testId);

		if (!scores.isEmpty()) {

			return scores;
		}

		// TODO: Throw exception with descriptive message if list is null to indicate
		// there's not records for the given arguments

		return null;
	}

		
	}



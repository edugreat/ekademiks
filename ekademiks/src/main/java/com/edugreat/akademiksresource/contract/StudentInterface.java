package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.projection.ScoreAndDate;

public interface StudentInterface {

	// method that serves a collection of questions for the given testId
     Collection<Question> takeTest(int testId);

	// for the given testId and studentId, retrieve the list of scores
	// the student made alongside the date the score was made.
	// since students are allowed to re-take a tests, it's
	// appropriate to return list of scores made
     List<ScoreAndDate> getScore(int studentId, int testId);
	

}
